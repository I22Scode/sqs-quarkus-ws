cd tx_validator
docker build -t j9r/tx-validator .
cd ../sqs-producer
./mwnw package -DskipTests
docker build -f src/main/docker/Dockerfile.jvm -t j9r/tx-producer .
cd ../sqs-consumer
./mwnw package -DskipTests
docker build -f src/main/docker/Dockerfile.jvm -t j9r/tx-consumer .