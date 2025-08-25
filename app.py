from fastapi import FastAPI, UploadFile, File
from pydantic import BaseModel
import hashlib, sqlite3, datetime

DB = "reputation.db"

def db():
    conn = sqlite3.connect(DB)
    conn.execute("CREATE TABLE IF NOT EXISTS samples(sha256 TEXT PRIMARY KEY, label TEXT, risk REAL, source TEXT, created_at TEXT)")
    return conn

app = FastAPI(title="AntivirusAI Reputation API", version="1.0-pro1")

class HashQuery(BaseModel):
    sha256: str
    pkg: str | None = None

@app.post("/reputation")
def reputation(q: HashQuery):
    conn = db()
    cur = conn.execute("SELECT label, risk, source FROM samples WHERE sha256=?", (q.sha256,))
    row = cur.fetchone()
    if not row:
        return {"found": False}
    label, risk, source = row
    return {"found": True, "label": label, "risk": float(risk), "source": source}

class AddSample(BaseModel):
    sha256: str
    label: str
    risk: float
    source: str = "manual"

@app.post("/admin/add")
def add_sample(s: AddSample):
    conn = db()
    conn.execute("INSERT OR REPLACE INTO samples(sha256,label,risk,source,created_at) VALUES(?,?,?,?,?)",
                 (s.sha256, s.label, float(s.risk), s.source, datetime.datetime.utcnow().isoformat()))
    conn.commit()
    return {"ok": True}

@app.post("/scan/yara")
async def scan_yara(file: UploadFile = File(...)):
    data = await file.read()
    digest = hashlib.sha256(data).hexdigest()
    matches = []
    if b"AccessibilityService" in data:
        matches.append("ACCESSIBILITY_ABUSE_HINT")
    return {"sha256": digest, "matches": matches}
