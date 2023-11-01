package io.coti.sdk;

import io.coti.sdk.base.*;
import io.coti.sdk.http.NetworkFeeRequest;
import io.coti.sdk.http.NetworkFeeResponse;
import io.coti.sdk.http.NetworkFeeValidateRequest;
import io.coti.sdk.utils.Constants;
import lombok.experimental.UtilityClass;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;

@UtilityClass
public class NetworkFee {

    public NetworkFeeResponse createNetworkFee(FullNodeFeeData fullNodeFeeData, Hash userHash, boolean feeIncluded, String trustScoreAddress) {
        NetworkFeeRequest networkFeeRequest = new NetworkFeeRequest(userHash, fullNodeFeeData, feeIncluded);
        HttpEntity<NetworkFeeRequest> entity = new HttpEntity<>(networkFeeRequest);
        ResponseEntity<NetworkFeeResponse> responseEntity = (ResponseEntity<NetworkFeeResponse>) Utilities.putRequest(trustScoreAddress + Constants.NETWORK_FEE, entity, NetworkFeeResponse.class);
        return responseEntity.getBody();
    }

    public NetworkFeeResponse validateNetworkFee(FullNodeFeeData fullNodeFeeData, NetworkFeeData networkFeeData, Hash userHash, boolean feeIncluded, String trustScoreAddress) {
        NetworkFeeValidateRequest networkFeeValidateRequest = new NetworkFeeValidateRequest(fullNodeFeeData, networkFeeData, userHash, feeIncluded);
        ResponseEntity<NetworkFeeResponse> networkFeeResponse = (ResponseEntity<NetworkFeeResponse>) Utilities.postRequest(trustScoreAddress + Constants.NETWORK_FEE, networkFeeValidateRequest, NetworkFeeResponse.class);
        return networkFeeResponse.getBody();
    }
}
