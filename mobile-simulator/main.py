import gradio as gr
import websocket
import requests
from typing import Optional
from pydantic import BaseModel
import uuid
import time
import asyncio

"""
A simple UI to demonstrate the async transaction processing
"""

# ---- Server side code

class Transaction(BaseModel):
    id: Optional[str] = ""
    status: Optional[str] = "Pending"
    amount: int = 0
    creationTS: int = 0
    endTS: int = 0

websocket.enableTrace(True)
ws = websocket.WebSocket()

output = gr.Textbox(label="Message from transaction processing")

async def handle_messages():
        while True:
            try:
                message = await websocket.recv()
                output.update(message)
            except websocket.exceptions.ConnectionClosed:
                print("Connection closed.")
                break


def connectUser(username):
    ws.connect(f"ws://localhost:8081/chat/{username}")
    ws.send(f"Hello, Server from {username}")
    out= ws.recv()
    #asyncio.get_event_loop().run_until_complete(handle_messages())
    return out
    
def disConnectUser():
    ws.close()

def sendTransaction(data):
    tx = Transaction(id = uuid.uuid4().hex, 
                     amount = data,
                     status = "Pending",
                     creationTS= int(time.time()),
                     endTS = 0)
    requests.post("http://localhost:8082/transactions", json= tx.model_dump())
    return tx
    
with gr.Blocks() as demo:
    gr.Markdown("""
        # Send Transaction with chat
                
        Connect and start sending transaction
    """)
   
    output = gr.Textbox(label="Message from transaction processing")

    with gr.Row():
        name = gr.Textbox(label="UserName")
        connect_btn = gr.Button("Connect")
        connect_btn.click(fn=connectUser, inputs=name, outputs=output, api_name="connectUser")
        disconnect_btn = gr.Button("Disconnect")
        disconnect_btn.click(fn=disConnectUser, api_name="disConnectUser")
    
    with gr.Blocks():
        amount = gr.Textbox(label="Transaction Amount")
        submit_btn = gr.Button("Submit")
        txOut = gr.Textbox(label="Transaction Sent")
        submit_btn.click(fn=sendTransaction, inputs= amount, outputs = txOut, api_name="sendTransaction")
        

demo.launch()
