package io.coti.sdk.data;

import lombok.Data;

@Data
public class TotalTransactionsMessage {

    private int totalTransactions;

    public TotalTransactionsMessage() {
    }

    public TotalTransactionsMessage(int totalTransactions) {
        this.totalTransactions = totalTransactions;
    }
}
