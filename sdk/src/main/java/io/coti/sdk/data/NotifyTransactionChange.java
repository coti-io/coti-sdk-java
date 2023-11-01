package io.coti.sdk.data;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import io.coti.sdk.base.*;
@Data
public class NotifyTransactionChange {

    private TransactionStatus status;
    private TransactionResponseData transactionResponseData;

    @JsonCreator
    public NotifyTransactionChange(@JsonProperty("status") TransactionStatus transactionStatus,
                                   @JsonProperty("transactionData") TransactionResponseData transactionResponseData) {
        this.status = transactionStatus;
        this.transactionResponseData = transactionResponseData;
    }
}
