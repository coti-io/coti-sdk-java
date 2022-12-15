package io.coti.sdk.http;

import io.coti.sdk.data.FullNodeFeeResponseData;
import lombok.Data;

@Data
public class FullNodeFeeResponse extends BaseResponse {

    private FullNodeFeeResponseData fullNodeFee;

    public FullNodeFeeResponse() {
    }

    public FullNodeFeeResponse(FullNodeFeeResponseData fullNodeFeeResponseData) {
        this.fullNodeFee = fullNodeFeeResponseData;
    }
}
