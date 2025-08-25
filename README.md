# Antivirus AI Pro (ChatGPT-Assisted)

Siap untuk skripsi: Android (offline-first) + Backend reputasi + Dummy model.

## Backend
```
pip install -r backend/requirements.txt
uvicorn backend.app:app --reload --port 8080
```
Tambah sampel reputasi:
```
curl -X POST http://localhost:8080/admin/add -H "Content-Type: application/json"   -d '{"sha256":"bad_sha256_hash_here","label":"malware","risk":0.95,"source":"lab"}'
```

## Android
- Buka `android/` di Android Studio, run.
- Isi URL backend (opsional) di input atas: `http://10.0.2.2:8080`.

## Skoring
`score = (1 - P(benign)) + bonus_heuristik`, lalu di-*max* dengan `risk` dari reputasi.
