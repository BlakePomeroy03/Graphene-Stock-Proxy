import os
import requests
from fastapi import FastAPI, HTTPException
from pydantic import BaseModel
from dotenv import load_dotenv

load_dotenv()

FINNHUB_API_KEY = os.getenv("FINNHUB_API_KEY")
FINNHUB_URL = "https://finnhub.io/api/v1/quote"

app = FastAPI(title="Graphene Stock Proxy")

class StockResponse(BaseModel):
    ticker: str
    price: float
    change: float
    isUp: bool

def fetch_stock_data(ticker: str) -> StockResponse:
    print(f"Fetching data for {ticker}...")
    params = {"symbol": ticker, "token": FINNHUB_API_KEY}
    
    # Add timeout=5 to prevent infinite hanging
    response = requests.get(FINNHUB_URL, params=params, timeout=5)
    
    print(f"Response status for {ticker}: {response.status_code}")
    
    if response.status_code != 200:
        raise HTTPException(status_code=response.status_code, detail="Failed to fetch data")
        
    data = response.json()
    
    if "c" not in data or data["c"] == 0:
        raise HTTPException(status_code=404, detail=f"Ticker {ticker} not found")

    current_price = data["c"]
    change = data["d"]
    
    return StockResponse(
        ticker=ticker.upper(),
        price=current_price,
        change=change,
        isUp=change >= 0
    )

@app.get("/")
def health_check():
    return {"status": "Proxy is active"}

@app.get("/api/stocks")
def get_stocks(tickers: str = "XLK,AMD,VOO"):
    ticker_list = [t.strip() for t in tickers.split(",")]
    results = []
    
    for ticker in ticker_list:
        try:
            stock_data = fetch_stock_data(ticker)
            results.append(stock_data)
        except HTTPException as e:
            # Return the exact error to the browser instead of skipping
            return {"error": f"Failed on {ticker}", "detail": e.detail, "status_code": e.status_code}

    return results