package io.coti.sdk.http;

import io.coti.sdk.data.NetworkFeeResponseData;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NonNull;

@Data
@EqualsAndHashCode(callSuper = true)
public class NetworkFeeResponse extends BaseResponse {

    @NonNull
    private NetworkFeeResponseData networkFeeData;

    public NetworkFeeResponse() {
    }

    public NetworkFeeResponse(NetworkFeeResponseData networkFeeResponseData) {
        this.networkFeeData = networkFeeResponseData;
    }
}
