import datetime
import json
import logging
from typing import List
from fastapi import FastAPI, HTTPException, WebSocket, WebSocketDisconnect
from fastapi.middleware.cors import CORSMiddleware
from pydantic import BaseModel, Field
import numpy as np
from scipy import optimize

logging.basicConfig(
    level=logging.INFO,
    format="%(asctime)s [%(levelname)s] [CID:%(name)s] %(message)s"
)
logger = logging.getLogger("xirr-service")

app = FastAPI(
    title="InvestFlow XIRR Calculation Engine",
    version="1.0.0",
    description="High-performance financial XIRR (Extended Internal Rate of Return) calculation engine via REST and WebSocket"
)

app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

class CashFlow(BaseModel):
    date: datetime.date = Field(..., description="Transaction date (YYYY-MM-DD)")
    amount: float = Field(..., description="Cash flow amount (negative for outflows/investments, positive for inflows/current value)")

class XirrRequest(BaseModel):
    cashFlows: List[CashFlow]

class XirrResponse(BaseModel):
    xirr: float
    annualizedPercentage: float
    iterations: int
    converged: bool

def calculate_xirr(cash_flows: List[CashFlow]) -> float:
    """
    Calculate Extended Internal Rate of Return (XIRR) using Newton-Raphson method with scipy.optimize.
    NPV(r) = sum( C_i / (1 + r)^((d_i - d_0) / 365) ) = 0
    """
    if len(cash_flows) < 2:
        raise ValueError("At least 2 cash flows (at least one negative and one positive) are required")

    amounts = [cf.amount for cf in cash_flows]
    has_negative = any(a < 0 for a in amounts)
    has_positive = any(a > 0 for a in amounts)
    if not (has_negative and has_positive):
        raise ValueError("Cash flows must contain at least one outflow (<0) and one inflow (>0)")

    dates = [cf.date for cf in cash_flows]
    start_date = min(dates)
    days = [(d - start_date).days for d in dates]

    def npv(rate: float) -> float:
        if rate <= -1.0:
            return float("inf")
        val = sum(a / ((1.0 + rate) ** (day / 365.0)) for a, day in zip(amounts, days))
        return val

    # Optimize using Brent's method or Newton with fallback
    try:
        sol = optimize.root_scalar(npv, bracket=[-0.9999, 10.0], method='brentq')
        if sol.converged:
            return round(float(sol.root), 6)
    except Exception:
        # Fallback to newton starting at 10%
        try:
            rate = optimize.newton(npv, x0=0.1, maxiter=200, tol=1e-6)
            return round(float(rate), 6)
        except Exception as e:
            raise ValueError(f"XIRR calculation did not converge: {str(e)}")

    raise ValueError("Failed to calculate XIRR")

@app.get("/health")
def health_check():
    return {"status": "UP", "service": "xirr-service"}

@app.post("/calculate/xirr", response_model=XirrResponse)
def compute_xirr(request: XirrRequest):
    try:
        rate = calculate_xirr(request.cashFlows)
        return XirrResponse(
            xirr=rate,
            annualizedPercentage=round(rate * 100.0, 2),
            iterations=1,
            converged=True
        )
    except ValueError as e:
        logger.error(f"XIRR calculation error: {str(e)}")
        raise HTTPException(status_code=400, detail=str(e))

@app.websocket("/ws/xirr")
async def websocket_xirr(websocket: WebSocket):
    await websocket.accept()
    logger.info("WebSocket connection established for XIRR calculation")
    try:
        while True:
            data = await websocket.receive_text()
            try:
                payload = json.loads(data)
                cash_flows = [CashFlow(**cf) for cf in payload.get("cashFlows", [])]
                rate = calculate_xirr(cash_flows)
                response = {
                    "status": "SUCCESS",
                    "xirr": rate,
                    "annualizedPercentage": round(rate * 100.0, 2),
                    "requestId": payload.get("requestId")
                }
            except Exception as ex:
                response = {
                    "status": "ERROR",
                    "error": str(ex),
                    "requestId": payload.get("requestId") if 'payload' in locals() else None
                }
            await websocket.send_text(json.dumps(response))
    except WebSocketDisconnect:
        logger.info("WebSocket disconnected")
