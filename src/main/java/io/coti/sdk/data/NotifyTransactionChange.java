package io.coti.sdk.data;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.coti.basenode.http.data.TransactionResponseData;
import io.coti.basenode.http.data.TransactionStatus;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Slf4j
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
