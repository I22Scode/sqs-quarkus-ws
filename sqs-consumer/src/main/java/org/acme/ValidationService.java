package org.acme;

import jakarta.ws.rs.POST;

import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;
import jakarta.ws.rs.Path;

@Path("/validate")
@RegisterRestClient(configKey="validation-api")
public interface ValidationService {
    
    @POST
    public Transaction validateTransaction(Transaction tx);
}
