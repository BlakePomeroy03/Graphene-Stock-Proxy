from fastapi import FastAPI

# Initialize the API
app = FastAPI(title="Graphene Stock Proxy")

@app.get("/")
def health_check():
    return {"status": "Proxy is active and shielding requests."}

@app.get("/api/stocks")
def get_mock_stocks():
    # Phase 1: Hardcoded data to test the pipeline.
    # Later, we will replace this with live Finnhub/Yahoo data.
    return [
        {"ticker": "XLK", "price": 151.45, "change": 4.50, "isUp": True},
        {"ticker": "AMD", "price": 202.02, "change": -2.56, "isUp": False},
        {"ticker": "VOO", "price": 480.10, "change": 1.20, "isUp": True}
    ]