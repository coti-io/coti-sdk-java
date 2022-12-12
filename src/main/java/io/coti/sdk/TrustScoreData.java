package io.coti.sdk;

import io.coti.basenode.crypto.CryptoHelper;
import io.coti.basenode.data.Hash;
import io.coti.basenode.data.SignatureData;
import io.coti.basenode.http.CustomHttpComponentsClientHttpRequestFactory;
import io.coti.sdk.http.GetTransactionTrustScoreRequest;
import io.coti.sdk.http.GetTransactionTrustScoreResponse;
import io.coti.sdk.utils.CryptoUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;

@Slf4j
@Service
public class TrustScoreData {

    @Value("${trust.score.backend.address}")
    private String trustScoreAddress;
    @Value("${user.hash}")
    private String userHashString;
    @Value("${seed}")
    private String seed;
    private Hash userHash;
    private static final String TRANSACTION_TRUST_SCORE = "/transactiontrustscore";

    @PostConstruct
    private void init() {
        userHash = new Hash(userHashString);
    }

    public GetTransactionTrustScoreResponse getTransactionTrustScoreData(Hash transactionHash) {
        RestTemplate restTemplate = new RestTemplate(new CustomHttpComponentsClientHttpRequestFactory());
        Hash userPrivateKey = CryptoUtils.getPrivateKeyFromSeed((new Hash(seed)).getBytes());
        SignatureData signatureData = CryptoHelper.signBytes(transactionHash.getBytes(), userPrivateKey.toHexString());
        GetTransactionTrustScoreRequest transactionTrustScoreRequest = new GetTransactionTrustScoreRequest(userHash, transactionHash, signatureData);

        GetTransactionTrustScoreResponse getTransactionTrustScoreResponse = null;
        try {
            getTransactionTrustScoreResponse = restTemplate.postForObject(trustScoreAddress + TRANSACTION_TRUST_SCORE, transactionTrustScoreRequest, GetTransactionTrustScoreResponse.class);
        } catch (Exception e) {
            log.error("Exception for getting TransactionTrustScoreData: ", e);
        }

        return getTransactionTrustScoreResponse;
    }
}
