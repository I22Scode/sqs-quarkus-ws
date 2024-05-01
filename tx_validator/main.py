from fastapi import FastAPI
import random 
from pydantic import BaseModel
import uvicorn

app = FastAPI()

class Transaction(BaseModel):
    id: str
    status: str
    amount: int

    
@app.get("/health")
async def get_hello():
    return { "status" : "Running"}

@app.post("/validate")
def validate_tx(transaction: Transaction):
    r = random.random() 
    if r < 0.6:
        transaction.status = "validated"
    else:
        transaction.status = "rejected"
    return transaction


if __name__ == "__main__":
    uvicorn.run(app, host="0.0.0.0", port=8001)