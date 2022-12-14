package io.coti.sdk;

import io.coti.basenode.crypto.CryptoHelper;
import io.coti.basenode.data.Hash;
import io.coti.basenode.data.SignatureData;
import io.coti.basenode.exceptions.CotiRunTimeException;
import io.coti.basenode.http.CustomHttpComponentsClientHttpRequestFactory;
import io.coti.sdk.http.GetTransactionTrustScoreRequest;
import io.coti.sdk.http.GetTransactionTrustScoreResponse;
import io.coti.sdk.utils.Constants;
import org.springframework.web.client.RestTemplate;

public class TrustScoreData {

    private String trustScoreAddress;

    public TrustScoreData(String trustScoreAddress) {
        this.trustScoreAddress = trustScoreAddress;
    }

    public GetTransactionTrustScoreResponse getTransactionTrustScoreData(Hash transactionHash, Hash userPrivateKey, Hash userHash) {
        RestTemplate restTemplate = new RestTemplate(new CustomHttpComponentsClientHttpRequestFactory());
        SignatureData signatureData = CryptoHelper.signBytes(transactionHash.getBytes(), userPrivateKey.toHexString());
        GetTransactionTrustScoreRequest transactionTrustScoreRequest = new GetTransactionTrustScoreRequest(userHash, transactionHash, signatureData);

        GetTransactionTrustScoreResponse getTransactionTrustScoreResponse = restTemplate.postForObject(trustScoreAddress + Constants.TRANSACTION_TRUST_SCORE, transactionTrustScoreRequest, GetTransactionTrustScoreResponse.class);

        if (getTransactionTrustScoreResponse == null || getTransactionTrustScoreResponse.getStatus().equals("Error")) {
            throw new CotiRunTimeException("Transaction TrustScore Data call failed!");
        }

        return getTransactionTrustScoreResponse;
    }
}
