package io.coti.sdk.http;

import lombok.Data;
import io.coti.sdk.base.*;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class FullNodeFeeResponse extends BaseResponse {

    private FullNodeFeeResponseData fullNodeFee;

    public FullNodeFeeResponse() {
    }

    public FullNodeFeeResponse(FullNodeFeeResponseData fullNodeFeeResponseData) {
        this.fullNodeFee = fullNodeFeeResponseData;
    }
}
