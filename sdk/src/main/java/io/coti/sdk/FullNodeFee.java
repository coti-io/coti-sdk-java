package io.coti.sdk;

import io.coti.basenode.data.Hash;
import io.coti.basenode.data.SignatureData;
import io.coti.basenode.exceptions.CotiRunTimeException;
import io.coti.basenode.http.CustomHttpComponentsClientHttpRequestFactory;
import io.coti.sdk.http.FullNodeFeeRequest;
import io.coti.sdk.http.FullNodeFeeResponse;
import io.coti.sdk.utils.Constants;
import io.coti.sdk.utils.CryptoUtils;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;

public class FullNodeFee {

    private String fullNodeAddress;
    private Hash nativeCurrencyHash;


    public FullNodeFee(String fullNodeAddress, Hash nativeCurrencyHash) {
        this.fullNodeAddress = fullNodeAddress;
        this.nativeCurrencyHash = nativeCurrencyHash;
    }

    public FullNodeFeeResponse createFullNodeFee(Hash userPrivateKey, Hash userHash, BigDecimal amount, boolean feeIncluded) {
        RestTemplate restTemplate = new RestTemplate(new CustomHttpComponentsClientHttpRequestFactory());
        SignatureData signature = CryptoUtils.signFullNodeFeeData(amount, userPrivateKey, nativeCurrencyHash);
        FullNodeFeeRequest fullNodeFeeRequest = new FullNodeFeeRequest(feeIncluded, nativeCurrencyHash, amount, userHash, signature);

        HttpEntity<FullNodeFeeRequest> entity = new HttpEntity<>(fullNodeFeeRequest);
        FullNodeFeeResponse fullNodeFee = restTemplate.exchange(fullNodeAddress + Constants.FULL_NODE_FEE, HttpMethod.PUT, entity, FullNodeFeeResponse.class).getBody();

        if (fullNodeFee == null || fullNodeFee.getStatus().equals("Error")) {
            throw new CotiRunTimeException("FullNodeFee call failed!");
        }
        return fullNodeFee;
    }
}
