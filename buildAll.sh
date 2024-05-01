cd tx_validator
docker build -t j9r/tx-validator .
cd ../sqs-producer
./mwnw install
docker build -f src/main/docker.jvm -t j9r/tx-producer .
cd ../sqs-consumer
./mwnw install
docker build -f src/main/docker.jvm -t j9r/tx-consumer .