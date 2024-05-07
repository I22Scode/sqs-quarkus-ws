import gradio as gr
import websocket
import requests
from typing import Optional
from pydantic import BaseModel
import uuid
import time
import asyncio

"""
A simple UI to demonstrate the async transaction processing.
It uses a basic transaction object.
Use the gradio.app for user interface in python, for quick demo
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




def connectUser(username):
    ws.connect(f"ws://localhost:8081/chat/{username}")
    ws.send(f"Hello, Server from {username}")
    out= ws.recv()
    #asyncio.get_event_loop().run_until_complete(handle_messages())
    return out
    
def disConnectUser():
    ws.close()


def sendTransaction(data):
    """
    Send a transaction to the Transaction service via REST API
    """
    tx = Transaction(id = uuid.uuid4().hex, 
                     amount = data,
                     status = "Pending",
                     creationTS= int(time.time()),
                     endTS = 0)
    requests.post("http://localhost:8082/transactions", json= tx.model_dump())
    return tx

# ---------------------- User interface part
  
with gr.Blocks() as demo:
    gr.Markdown("""
        # Send Transaction with a messenger front end
                
        Connect and start sending transaction
    """)
    output = gr.Textbox(label="Message from transaction processing")

    async def handle_messages():
        """
        Message coming from Tx validation orchestrator
        """
        while True:
            try:
                message = await ws.recv()
                output.update(message)
            except Exception as e:
                print("Error on connection or receiver")
                break


    with gr.Row():
        name = gr.Textbox(label="Enter Fake User Name")
        connect_btn = gr.Button("Connect")
        connect_btn.click(fn=connectUser, inputs=name, outputs=output, api_name="connectUser")
        disconnect_btn = gr.Button("Disconnect")
        disconnect_btn.click(fn=disConnectUser, api_name="disConnectUser")
    
    with gr.Blocks():
        amount = gr.Textbox(label="Transaction Amount (the other attributes are populated)")
        submit_btn = gr.Button("Submit")
        txOut = gr.Textbox(label="Transaction Sent")
        submit_btn.click(fn=sendTransaction, inputs= amount, outputs = txOut, api_name="sendTransaction")
        
    asyncio.run(handle_messages())
    
demo.launch()
