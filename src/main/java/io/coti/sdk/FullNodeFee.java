package io.coti.sdk;

import io.coti.basenode.crypto.OriginatorCurrencyCrypto;
import io.coti.basenode.data.Hash;
import io.coti.basenode.data.SignatureData;
import io.coti.basenode.http.CustomHttpComponentsClientHttpRequestFactory;
import io.coti.sdk.http.FullNodeFeeRequest;
import io.coti.sdk.http.FullNodeFeeResponse;
import io.coti.sdk.utils.CryptoUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import java.math.BigDecimal;

@Slf4j
@Service
public class FullNodeFee {

    @Value("${full.node.backend.address}")
    private String fullNodeAddress;
    @Value("${user.hash}")
    private String userHashString;
    @Value("${native.currency.symbol:COTI}")
    private String nativeCurrencySymbol;
    @Value("${seed}")
    private String seed;
    private Hash userHash;
    private Hash nativeCurrencyHash;
    private static final String FULL_NODE_FEE = "/fee";

    @PostConstruct
    private void init() {
        userHash = new Hash(userHashString);
        nativeCurrencyHash = OriginatorCurrencyCrypto.calculateHash(nativeCurrencySymbol);
    }

    public FullNodeFeeResponse createFullNodeFee(BigDecimal amount, boolean feeIncluded) {
        RestTemplate restTemplate = new RestTemplate(new CustomHttpComponentsClientHttpRequestFactory());
        Hash userPrivateKey = CryptoUtils.getPrivateKeyFromSeed((new Hash(seed)).getBytes());
        SignatureData signature = CryptoUtils.signFullNodeFeeData(amount, userPrivateKey, nativeCurrencyHash);
        FullNodeFeeRequest fullNodeFeeRequest = new FullNodeFeeRequest(feeIncluded, nativeCurrencyHash, amount, userHash, signature);

        FullNodeFeeResponse fullNodeFee = null;
        try {
            HttpEntity<FullNodeFeeRequest> entity = new HttpEntity<>(fullNodeFeeRequest);
            fullNodeFee = restTemplate.exchange(fullNodeAddress + FULL_NODE_FEE, HttpMethod.PUT, entity, FullNodeFeeResponse.class).getBody();
        } catch (Exception e) {
            log.error("Exception for getting FullNodeFee: ", e);
        }

        return fullNodeFee;
    }
}
