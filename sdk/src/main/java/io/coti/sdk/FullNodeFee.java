package io.coti.sdk;

import io.coti.sdk.base.*;
import io.coti.sdk.http.FullNodeFeeRequest;
import io.coti.sdk.http.FullNodeFeeResponse;
import io.coti.sdk.utils.Constants;
import io.coti.sdk.utils.CryptoUtils;
import lombok.experimental.UtilityClass;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;

@UtilityClass
public class FullNodeFee {

    public FullNodeFeeResponse createFullNodeFee(Hash userPrivateKey, Hash userHash, BigDecimal amount, boolean feeIncluded, Hash nativeCurrencyHash,
                                                 String fullNodeAddress) {
        SignatureData signature = CryptoUtils.signFullNodeFeeData(amount, userPrivateKey, nativeCurrencyHash);
        FullNodeFeeRequest fullNodeFeeRequest = new FullNodeFeeRequest(feeIncluded, nativeCurrencyHash, amount, userHash, signature);

        HttpEntity<FullNodeFeeRequest> entity = new HttpEntity<>(fullNodeFeeRequest);
        ResponseEntity<FullNodeFeeResponse> fullNodeFee = (ResponseEntity<FullNodeFeeResponse>) Utilities.putRequest(fullNodeAddress + Constants.FULL_NODE_FEE, entity, FullNodeFeeResponse.class);
        return fullNodeFee.getBody();
    }
}
