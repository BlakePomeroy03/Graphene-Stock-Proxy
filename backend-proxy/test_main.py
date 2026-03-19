from fastapi.testclient import TestClient
from main import app

client = TestClient(app)

def test_health_check():
    response = client.get("/")
    assert response.status_code == 200
    assert response.json() == {"status": "Proxy is active"}

def test_get_stocks_valid_tickers():
    response = client.get("/api/stocks?tickers=AAPL,MSFT")
    assert response.status_code == 200
    
    data = response.json()
    assert isinstance(data, list)
    
    if len(data) > 0:
        first_stock = data[0]
        assert "ticker" in first_stock
        assert "price" in first_stock
        assert "change" in first_stock
        assert "isUp" in first_stock
        assert isinstance(first_stock["isUp"], bool)