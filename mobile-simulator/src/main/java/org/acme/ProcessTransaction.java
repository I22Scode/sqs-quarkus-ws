package org.acme;

import jakarta.ws.rs.POST;

import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;
import jakarta.ws.rs.Path;

@Path("/validate")
@RegisterRestClient(configKey="process-tx-api")
public interface ProcessTransaction {
    
    @POST
    public Transaction sendTransaction(Transaction tx);
}
