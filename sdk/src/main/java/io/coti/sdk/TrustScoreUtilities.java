package io.coti.sdk;

import io.coti.basenode.crypto.CryptoHelper;
import io.coti.basenode.data.Hash;
import io.coti.basenode.data.SignatureData;
import io.coti.basenode.http.GetTrustScoreRequest;
import io.coti.basenode.http.GetUserTrustScoreResponse;
import io.coti.sdk.data.WalletDetails;
import io.coti.sdk.http.GetTransactionTrustScoreRequest;
import io.coti.sdk.http.GetTransactionTrustScoreResponse;
import io.coti.sdk.utils.Constants;
import lombok.experimental.UtilityClass;
import org.springframework.http.ResponseEntity;

@UtilityClass
public class TrustScoreUtilities {

    public GetTransactionTrustScoreResponse getTransactionTrustScoreData(Hash transactionHash, Hash userPrivateKey, Hash userHash, WalletDetails transactionDetails) {
        SignatureData signatureData = CryptoHelper.signBytes(transactionHash.getBytes(), userPrivateKey.toHexString());
        GetTransactionTrustScoreRequest transactionTrustScoreRequest = new GetTransactionTrustScoreRequest(userHash, transactionHash, signatureData);
        ResponseEntity<GetTransactionTrustScoreResponse> getTransactionTrustScoreResponse = (ResponseEntity<GetTransactionTrustScoreResponse>) Utilities.postRequest(transactionDetails.getTrustScoreAddress() + Constants.TRANSACTION_TRUST_SCORE, transactionTrustScoreRequest, GetTransactionTrustScoreResponse.class);
        return getTransactionTrustScoreResponse.getBody();
    }

    public GetUserTrustScoreResponse getUserTrustScore(Hash userHash, String trustScoreAddress) {
        GetTrustScoreRequest getTrustScoreRequest = new GetTrustScoreRequest();
        getTrustScoreRequest.setUserHash(userHash);
        ResponseEntity<GetUserTrustScoreResponse> getUserTrustScoreResponse = (ResponseEntity<GetUserTrustScoreResponse>) Utilities.postRequest(trustScoreAddress + Constants.USER_TRUST_SCORE, getTrustScoreRequest, GetUserTrustScoreResponse.class);
        return getUserTrustScoreResponse.getBody();
    }
}
