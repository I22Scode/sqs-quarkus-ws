# A demonstration to get SQS messages using Quarkus app

Goal is to scale the asynchronous processing of transaction started from a mobile app.

## Requirements

* support mobile simulator as a chat interface. The submit button send a transaction
* Use AWS SQS queue for getting transaction messages.
* Use a producer of transaction which exposes a REST api to receive transaction from the mobile simulator
* Consume then contact a remote server to validate the transaction, then push the information to the connected mobile app.
* Push a validated message back to a web-socket app

## Components of this demo

![](./docs/component-view.drawio.png)

Target architecture for a production ready solution:

![](./docs/prod-component-view.drawio.png)


### Physical deployment on AWS

![]()

## Run all the components locally with SQS localstack.

* Need to get aws CLI
* Need to get localstack cli using python virtual env and ` pip install localstack`
* use `aws configure` with key = test but region needs to be `us-east-1` as it is the default region for localstack
* Start docker compose with the different working components.

    ```sh
    docker-compose up -d
    ```

* Create the queue: QUEUE_URL=`aws sqs create-queue --queue-name=TxQueue --profile localstack --endpoint-url=http://localhost:4566`  or the script `create_queue.sh`

* Verify the queues with: 

    ```sh
    aws sqs list-queues --profile localstack --endpoint-url=http://localhost:4566
    ```

* Use the Producer Swagger to test sending a Transaction

* Start the Mobile simulator, and go to [http://localhost:7860/](http://localhost:7860/)

    ```sh
    gradio main.py
    # or
    python main.py
    ```

    Enter a user name and then connect, so the client app can connect via websocket to server, 
    
    ![](./docs/simul-1.PNG)

    then enter a transaction amount. A new Transaction object is created and sent with a timestamp to the producer, then sqs, and the consumer has a trace showing it consumed from SQS. 

    ```
    Transaction{id='1d04a5d3d8e54c068363f2365b399a87'status='rejected', amount=45, creation time=1715028952}
    ```


