package io.coti.sdk;

import io.coti.basenode.crypto.CryptoHelper;
import io.coti.basenode.data.Hash;
import io.coti.basenode.data.SignatureData;
import io.coti.basenode.exceptions.CotiRunTimeException;
import io.coti.basenode.http.CustomHttpComponentsClientHttpRequestFactory;
import io.coti.basenode.http.GetTrustScoreRequest;
import io.coti.basenode.http.GetUserTrustScoreResponse;
import io.coti.sdk.http.GetTransactionTrustScoreRequest;
import io.coti.sdk.http.GetTransactionTrustScoreResponse;
import io.coti.sdk.utils.Constants;
import org.springframework.web.client.RestTemplate;

public class TrustScoreUtilities {

    private String trustScoreAddress;
    private static final RestTemplate restTemplate = new RestTemplate(new CustomHttpComponentsClientHttpRequestFactory());

    public TrustScoreUtilities(String trustScoreAddress) {
        this.trustScoreAddress = trustScoreAddress;
    }

    public GetTransactionTrustScoreResponse getTransactionTrustScoreData(Hash transactionHash, Hash userPrivateKey, Hash userHash) {
        SignatureData signatureData = CryptoHelper.signBytes(transactionHash.getBytes(), userPrivateKey.toHexString());
        GetTransactionTrustScoreRequest transactionTrustScoreRequest = new GetTransactionTrustScoreRequest(userHash, transactionHash, signatureData);

        GetTransactionTrustScoreResponse getTransactionTrustScoreResponse = restTemplate.postForObject(trustScoreAddress + Constants.TRANSACTION_TRUST_SCORE, transactionTrustScoreRequest, GetTransactionTrustScoreResponse.class);

        if (getTransactionTrustScoreResponse == null || getTransactionTrustScoreResponse.getStatus().equals(Constants.ERROR)) {
            throw new CotiRunTimeException("Transaction TrustScore Data call failed!");
        }

        return getTransactionTrustScoreResponse;
    }

    public GetUserTrustScoreResponse getUserTrustScore(Hash userHash) {
        GetTrustScoreRequest getTrustScoreRequest = new GetTrustScoreRequest();
        getTrustScoreRequest.setUserHash(userHash);

        GetUserTrustScoreResponse getUserTrustScoreResponse = restTemplate.postForObject(trustScoreAddress + Constants.USER_TRUST_SCORE, getTrustScoreRequest, GetUserTrustScoreResponse.class);

        if (getUserTrustScoreResponse == null || !getUserTrustScoreResponse.getStatus().equals("Success")) {
            throw new CotiRunTimeException("User TrustScore Data call failed!");
        }

        return getUserTrustScoreResponse;
    }
}
