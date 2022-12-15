package io.coti.sdk;

import io.coti.basenode.data.FullNodeFeeData;
import io.coti.basenode.data.Hash;
import io.coti.basenode.data.NetworkFeeData;
import io.coti.basenode.exceptions.CotiRunTimeException;
import io.coti.basenode.http.CustomHttpComponentsClientHttpRequestFactory;
import io.coti.sdk.http.NetworkFeeRequest;
import io.coti.sdk.http.NetworkFeeResponse;
import io.coti.sdk.http.NetworkFeeValidateRequest;
import io.coti.sdk.utils.Constants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.web.client.RestTemplate;

@Slf4j
public class NetworkFee {

    private String trustScoreAddress;

    public NetworkFee(String trustScoreAddress) {
        this.trustScoreAddress = trustScoreAddress;
    }

    public NetworkFeeResponse createNetworkFee(FullNodeFeeData fullNodeFeeData, Hash userHash, boolean feeIncluded) {
        RestTemplate restTemplate = new RestTemplate(new CustomHttpComponentsClientHttpRequestFactory());
        NetworkFeeRequest networkFeeRequest = new NetworkFeeRequest(userHash, fullNodeFeeData, feeIncluded);

        NetworkFeeResponse networkFeeResponse = null;
        try {
            HttpEntity<NetworkFeeRequest> entity = new HttpEntity<>(networkFeeRequest);
            networkFeeResponse = restTemplate.exchange(trustScoreAddress + Constants.NETWORK_FEE, HttpMethod.PUT, entity, NetworkFeeResponse.class).getBody();
        } catch (Exception e) {
            log.error("Exception for CreateNetworkFee: ", e);
        }
        if (networkFeeResponse == null || networkFeeResponse.getStatus().equals("Error")) {
            throw new CotiRunTimeException("Create NetworkFee call failed!");
        }
        return networkFeeResponse;
    }

    public NetworkFeeResponse validateNetworkFee(FullNodeFeeData fullNodeFeeData, NetworkFeeData networkFeeData, Hash userHash, boolean feeIncluded) {
        RestTemplate restTemplate = new RestTemplate(new CustomHttpComponentsClientHttpRequestFactory());
        NetworkFeeValidateRequest networkFeeValidateRequest = new NetworkFeeValidateRequest(fullNodeFeeData, networkFeeData, userHash, feeIncluded);

        NetworkFeeResponse networkFeeResponse = null;
        try {
            networkFeeResponse = restTemplate.postForObject(trustScoreAddress + Constants.NETWORK_FEE, networkFeeValidateRequest, NetworkFeeResponse.class);
        } catch (Exception e) {
            log.error("Exception for ValidateNetworkFee: ", e);
        }
        if (networkFeeResponse == null || networkFeeResponse.getStatus().equals("Error")) {
            throw new CotiRunTimeException("Validate NetworkFee call failed!");
        }
        return networkFeeResponse;
    }
}
