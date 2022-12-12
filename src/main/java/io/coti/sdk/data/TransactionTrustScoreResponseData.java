package io.coti.sdk.data;

import io.coti.basenode.data.SignatureData;
import io.coti.basenode.data.TransactionTrustScoreData;
import lombok.Data;

import java.io.Serializable;

@Data
public class TransactionTrustScoreResponseData implements Serializable {

    private double trustScore;
    private String trustScoreNodeHash;
    private SignatureData trustScoreNodeSignature;

    public TransactionTrustScoreResponseData() {
    }

    public TransactionTrustScoreResponseData(TransactionTrustScoreData transactionTrustScoreData) {
        this.trustScore = transactionTrustScoreData.getTrustScore();
        this.trustScoreNodeHash = transactionTrustScoreData.getTrustScoreNodeHash().toHexString();
        this.trustScoreNodeSignature = transactionTrustScoreData.getTrustScoreNodeSignature();
    }
}
