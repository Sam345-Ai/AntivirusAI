from fastapi.testclient import TestClient
from backend.app import app

client = TestClient(app)

def test_reputation_not_found():
    r = client.post('/reputation', json={'sha256': 'x'*64})
    assert r.status_code == 200
    assert r.json()['found'] is False
