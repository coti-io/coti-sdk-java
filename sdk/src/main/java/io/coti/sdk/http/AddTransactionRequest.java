package io.coti.sdk.http;

import io.coti.sdk.base.*;
import lombok.Data;
import lombok.NonNull;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.Instant;
import java.util.List;

@Data
public class AddTransactionRequest implements Serializable {

    @NotEmpty
    @NonNull
    private List<@Valid BaseTransactionData> baseTransactions;
    @NotNull
    @NonNull
    private @Valid Hash hash;
    @NotEmpty
    @NonNull
    private String transactionDescription;
    @NotNull
    @NonNull
    private Instant createTime;
    @NotEmpty
    @NonNull
    private List<@Valid TransactionTrustScoreData> trustScoreResults;
    @NotNull
    @NonNull
    private Hash senderHash;
    @NotNull
    @NonNull
    private @Valid SignatureData senderSignature;
    @NotNull
    @NonNull
    private TransactionType type;

    public AddTransactionRequest(TransactionData transactionData) {
        this.baseTransactions = transactionData.getBaseTransactions();
        this.hash = transactionData.getHash();
        this.transactionDescription = transactionData.getTransactionDescription();
        this.createTime = transactionData.getCreateTime();
        this.trustScoreResults = transactionData.getTrustScoreResults();
        this.senderHash = transactionData.getSenderHash();
        this.senderSignature = transactionData.getSenderSignature();
        this.type = transactionData.getType();
    }
}
