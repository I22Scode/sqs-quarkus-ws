package org.acme;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import io.quarkus.scheduler.Scheduled;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.SqsAsyncClient;
import software.amazon.awssdk.services.sqs.model.DeleteMessageRequest;
import software.amazon.awssdk.services.sqs.model.Message;
import software.amazon.awssdk.services.sqs.model.ReceiveMessageRequest;
import software.amazon.awssdk.services.sqs.model.ReceiveMessageResponse;

import java.util.List;
import java.util.stream.Collectors;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.logging.Logger;


@ApplicationScoped
public class TransactionConsumer {
    private static final Logger LOGGER = Logger.getLogger(TransactionConsumer.class);
    static ObjectReader TX_READER = new ObjectMapper().readerFor(Transaction.class);

    @RestClient 
    ValidationService validationService;

    @Inject
    MobileSocketServer notifyConnected;

    @Inject
    SqsClient sqsClient;

    @ConfigProperty(name = "queue.maxNumberOfMessages")
    int maxNumberOfMessages;

    @ConfigProperty(name = "queue.url")
    String queueUrl;

    @ConfigProperty(name = "queue.wait-time-seconds")
    int waitTimeSeconds;

   
    @GET
    @Path("/health")
    @Produces(MediaType.TEXT_PLAIN)
    public String hello() {
        return "running";
    }

    @Scheduled(every = "{queue.poll-time-seconds}")
    public void processTransaction() {
        try {
            ReceiveMessageRequest request = ReceiveMessageRequest.builder()
                        .queueUrl(queueUrl)
                        .maxNumberOfMessages(maxNumberOfMessages)
                        .waitTimeSeconds(waitTimeSeconds)
                        .build();
            List<Message> messages = sqsClient.receiveMessage(request).messages();
    
            List<Transaction> txs = messages.stream()
                .map(Message::body)
                .map(this::toTransaction)
                .collect(Collectors.toList());

            for (Transaction tx : txs) {
                Transaction txo = validationService.validateTransaction((tx));
                notifyConnected.broadcast(transactionJson(txo));
                System.out.println(txo.toString());
            }
            messages.forEach(message -> {
            String receiptHandle = message.receiptHandle();
            DeleteMessageRequest delReq = DeleteMessageRequest.builder()
                    .queueUrl(queueUrl)
                    .receiptHandle(receiptHandle)
                    .build();
                    sqsClient.deleteMessage(delReq);
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
        
    }

    private Transaction toTransaction(String message) {
        Transaction tx = null;
        try {
            tx = TX_READER.readValue(message);
        } catch (Exception e) {
            LOGGER.error("Error decoding message", e);
            throw new RuntimeException(e);
        }
        return tx;
    }

    private String transactionJson(Transaction tx) {
        String rep;
        try {
            rep = new ObjectMapper().writeValueAsString(tx);
        } catch (Exception e) {
            LOGGER.error("Error decoding message", e);
            throw new RuntimeException(e);
        }
        return rep;
    }
}
