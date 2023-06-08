package io.coti.sdk.http;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.coti.basenode.http.BaseResponse;
import io.coti.sdk.data.TransactionTrustScoreResponseData;
import lombok.Data;

@Data
public class GetTransactionTrustScoreResponse extends BaseResponse {

    @JsonProperty("transactionTrustScoreData")
    private TransactionTrustScoreResponseData transactionTrustScoreData;

    public GetTransactionTrustScoreResponse() {
        super();
    }

    public GetTransactionTrustScoreResponse(TransactionTrustScoreResponseData transactionTrustScoreData) {
        this.transactionTrustScoreData = transactionTrustScoreData;
    }
}
