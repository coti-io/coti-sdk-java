package io.coti.sdk.utils;

import io.coti.basenode.data.*;
import io.coti.basenode.http.data.TokenGenerationFeeResponseData;
import io.coti.basenode.http.data.TrustScoreNodeResultResponseData;
import io.coti.sdk.data.FullNodeFeeResponseData;
import io.coti.sdk.data.NetworkFeeResponseData;
import io.coti.sdk.data.TransactionTrustScoreResponseData;
import io.coti.sdk.data.interfaces.*;
import lombok.experimental.UtilityClass;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@UtilityClass
public class Mapper {

    public ToFullNodeFeeData map(FullNodeFeeResponseData responseData) {
        Hash addressHash = new Hash(responseData.getAddressHash());
        Hash currencyHash = responseData.getOriginalCurrencyHash() != null ? new Hash(responseData.getCurrencyHash()) : null;
        BigDecimal amount = new BigDecimal(responseData.getAmount());
        BigDecimal originalAmount = new BigDecimal(responseData.getOriginalAmount());
        Hash originalCurrencyHash = responseData.getOriginalCurrencyHash() != null ? new Hash(responseData.getOriginalCurrencyHash()) : null;
        return () -> new FullNodeFeeData(addressHash, currencyHash, amount, originalCurrencyHash, originalAmount, responseData.getCreateTime());
    }

    public ToNetworkFeeData map(NetworkFeeResponseData responseData) {
        return () -> {
            NetworkFeeData networkFeeData = new NetworkFeeData(new Hash(responseData.getAddressHash()), responseData.getOriginalCurrencyHash() != null ? new Hash(responseData.getCurrencyHash()) : null, new BigDecimal(responseData.getAmount()), responseData.getOriginalCurrencyHash() != null ? new Hash(responseData.getOriginalCurrencyHash()) : null, new BigDecimal(responseData.getOriginalAmount()), responseData.getReducedAmount() != null ? new BigDecimal(responseData.getReducedAmount()) : null, responseData.getCreateTime());
            List<TrustScoreNodeResultData> networkFeeTrustScoreNodeResult = new ArrayList<>();
            for (TrustScoreNodeResultResponseData resultResponseData : responseData.getNetworkFeeTrustScoreNodeResult()) {
                TrustScoreNodeResultData resultData = Mapper.map(resultResponseData).toTrustScoreNodeResultData();
                networkFeeTrustScoreNodeResult.add(resultData);
            }
            networkFeeData.setNetworkFeeTrustScoreNodeResult(networkFeeTrustScoreNodeResult);
            networkFeeData.setHash(new Hash(responseData.getHash()));
            return networkFeeData;
        };
    }

    public ToTrustScoreData map(TransactionTrustScoreResponseData responseData) {
        return () -> {
            TransactionTrustScoreData trustScoreData = new TransactionTrustScoreData(responseData.getTrustScore());
            trustScoreData.setTrustScoreNodeHash(new Hash(responseData.getTrustScoreNodeHash()));
            trustScoreData.setTrustScoreNodeSignature(responseData.getTrustScoreNodeSignature());
            return trustScoreData;
        };
    }

    public ToTrustScoreNodeResultData map(TrustScoreNodeResultResponseData responseData) {
        return () -> {
            TrustScoreNodeResultData trustScoreData = new TrustScoreNodeResultData(new Hash(responseData.getTrustScoreNodeHash()), responseData.isValid());
            trustScoreData.setTrustScoreNodeSignature(responseData.getTrustScoreNodeSignature());
            return trustScoreData;
        };
    }

    public ToTokenGenerationFeeBaseTransactionData map(TokenGenerationFeeResponseData tokenGenerationFeeResponseData, TokenGenerationServiceData tokenGenerationServiceData) {
        return () -> new TokenGenerationFeeBaseTransactionData(
                new Hash(tokenGenerationFeeResponseData.getAddressHash()),
                new Hash(tokenGenerationFeeResponseData.getCurrencyHash()),
                new Hash(tokenGenerationFeeResponseData.getSignerHash()),
                tokenGenerationFeeResponseData.getAmount(),
                tokenGenerationFeeResponseData.getCreateTime(),
                tokenGenerationServiceData);
    }
}
