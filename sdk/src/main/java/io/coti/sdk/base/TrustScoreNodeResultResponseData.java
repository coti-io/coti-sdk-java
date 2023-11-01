package io.coti.sdk.base;

import lombok.Data;

@Data
public class TrustScoreNodeResultResponseData implements IResponseData {

    private String trustScoreNodeHash;
    private SignatureData trustScoreNodeSignature;
    private boolean valid;

    private TrustScoreNodeResultResponseData() {

    }

    public TrustScoreNodeResultResponseData(TrustScoreNodeResultData trustScoreNodeResultData) {
        this.trustScoreNodeHash = trustScoreNodeResultData.getTrustScoreNodeHash() == null ? null : trustScoreNodeResultData.getTrustScoreNodeHash().toString();
        this.trustScoreNodeSignature = trustScoreNodeResultData.getSignature();
        this.valid = trustScoreNodeResultData.isValid();

    }

}
