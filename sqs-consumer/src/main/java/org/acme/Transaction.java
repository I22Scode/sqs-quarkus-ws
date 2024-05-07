package org.acme;

import io.quarkus.runtime.annotations.RegisterForReflection;

@RegisterForReflection
public class Transaction {
    public String id;
    public String status = "pending";
    public int amount = 0;
    public long creationTS;
    public long endTS;

    public Transaction() {
        super();
    }

    @Override
    public String toString() {
        return "Transaction{" +
                "id='" + id + '\'' +
                "status='" + status + '\'' +
                ", amount=" + amount +
                ", creation time=" + creationTS +
                ", end time=" + endTS +
                '}';
    }
}
