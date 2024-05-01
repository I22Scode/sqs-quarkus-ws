package org.acme;

import org.eclipse.microprofile.config.inject.ConfigProperty;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.SendMessageResponse;

import org.jboss.logging.Logger;

@Path("/transactions")
public class TransactionResource {

private static final Logger LOGGER = Logger.getLogger(TransactionResource.class);

    @Inject
    SqsClient sqs;

    @ConfigProperty(name = "queue.url")
    String queueUrl;

    static ObjectWriter TX_WRITER = new ObjectMapper().writerFor(Transaction.class);

    
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String health() {
        return "running";
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response  save_transaction(Transaction tx) throws Exception {
   String message = TX_WRITER.writeValueAsString(tx);
        SendMessageResponse response = sqs.sendMessage(m -> m.queueUrl(queueUrl).messageBody(message));
        LOGGER.infov("Send tx[{0}, {1}}]", tx.id, tx.status);
    
        return Response.ok().entity(response.messageId()).build();
    }
}
