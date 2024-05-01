package org.acme;

import io.quarkus.runtime.annotations.RegisterForReflection;

@RegisterForReflection
public class Transaction {
    public String id;
    public String status = "pending";
    public int amount = 0;

    public Transaction() {
        super();
    }
}
