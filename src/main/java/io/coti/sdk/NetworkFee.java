package io.coti.sdk;

import io.coti.basenode.data.FullNodeFeeData;
import io.coti.basenode.data.Hash;
import io.coti.basenode.data.NetworkFeeData;
import io.coti.basenode.http.CustomHttpComponentsClientHttpRequestFactory;
import io.coti.sdk.http.NetworkFeeRequest;
import io.coti.sdk.http.NetworkFeeResponse;
import io.coti.sdk.http.NetworkFeeValidateRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;

@Slf4j
@Service
public class NetworkFee {

    RestTemplate restTemplate;
    @Value("${trust.score.backend.address}")
    private String trustScoreAddress;
    @Value("${user.hash}")
    private String userHashString;
    private Hash userHash;
    private static final String NETWORK_FEE = "/networkFee";

    @PostConstruct
    private void init() {
        userHash = new Hash(userHashString);
        restTemplate = new RestTemplate(new CustomHttpComponentsClientHttpRequestFactory());
    }

    public NetworkFeeResponse createNetworkFee(FullNodeFeeData fullNodeFeeData, boolean feeIncluded) {
        NetworkFeeRequest networkFeeRequest = new NetworkFeeRequest(userHash, fullNodeFeeData, feeIncluded);

        NetworkFeeResponse networkFeeResponse = null;
        try {
            HttpEntity<NetworkFeeRequest> entity = new HttpEntity<>(networkFeeRequest);
            networkFeeResponse = restTemplate.exchange(trustScoreAddress + NETWORK_FEE, HttpMethod.PUT, entity, NetworkFeeResponse.class).getBody();
        } catch (Exception e) {
            log.error("Exception for CreateNetworkFee: ", e);
        }
        return networkFeeResponse;
    }

    public NetworkFeeResponse validateNetworkFee(FullNodeFeeData fullNodeFeeData, NetworkFeeData networkFeeData, boolean feeIncluded) {
        NetworkFeeValidateRequest networkFeeValidateRequest = new NetworkFeeValidateRequest(fullNodeFeeData, networkFeeData, userHash, feeIncluded);

        NetworkFeeResponse networkFeeResponse = null;
        try {
            networkFeeResponse = restTemplate.postForObject(trustScoreAddress + NETWORK_FEE, networkFeeValidateRequest, NetworkFeeResponse.class);
        } catch (Exception e) {
            log.error("Exception for ValidateNetworkFee: ", e);
        }

        return networkFeeResponse;
    }
}
