package io.coti.sdk;

import io.coti.basenode.data.*;
import io.coti.sdk.http.FullNodeFeeResponse;
import io.coti.sdk.http.NetworkFeeResponse;
import io.coti.sdk.utils.Mapper;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class BaseTransactionCreation {

    private Hash nativeCurrencyHash;
    private String fullNodeAddress;
    private String trustScoreAddress;

    public BaseTransactionCreation(Hash nativeCurrencyHash, String fullNodeAddress, String trustScoreAddress) {
        this.nativeCurrencyHash = nativeCurrencyHash;
        this.fullNodeAddress = fullNodeAddress;
        this.trustScoreAddress = trustScoreAddress;
    }


    public List<BaseTransactionData> createBaseTransactions(Hash userPrivateKey, Hash userHash, BigDecimal amount, Hash addressHash, boolean feeIncluded) {
        FullNodeFee fullNodeFee = new FullNodeFee(fullNodeAddress, nativeCurrencyHash);
        FullNodeFeeResponse fullNodeFeeResponse = fullNodeFee.createFullNodeFee(userPrivateKey, userHash, amount, feeIncluded);
        FullNodeFeeData fullNodeFeeData = Mapper.map(fullNodeFeeResponse.getFullNodeFee()).toFullNodeFeeData();
        fullNodeFeeData.setHash(new Hash(fullNodeFeeResponse.getFullNodeFee().getHash()));
        fullNodeFeeData.setSignature(fullNodeFeeResponse.getFullNodeFee().getSignatureData());

        NetworkFee networkFee = new NetworkFee(trustScoreAddress);
        NetworkFeeResponse networkFeeResponse = networkFee.createNetworkFee(fullNodeFeeData, userHash, feeIncluded);
        NetworkFeeData networkFeeData = Mapper.map(networkFeeResponse.getNetworkFeeData()).toNetworkFeeData();

        networkFeeResponse = networkFee.validateNetworkFee(fullNodeFeeData, networkFeeData, userHash, feeIncluded);
        NetworkFeeData firstValidatedNetworkFeeData = Mapper.map(networkFeeResponse.getNetworkFeeData()).toNetworkFeeData();

        networkFeeResponse = networkFee.validateNetworkFee(fullNodeFeeData, firstValidatedNetworkFeeData, userHash, feeIncluded);
        NetworkFeeData validatedNetworkFeeData = Mapper.map(networkFeeResponse.getNetworkFeeData()).toNetworkFeeData();

        BigDecimal fullAmount = amount.add(fullNodeFeeData.getAmount()).add(validatedNetworkFeeData.getAmount());

        List<BaseTransactionData> baseTransactions = new ArrayList<>();
        baseTransactions.add(fullNodeFeeData);
        baseTransactions.add(validatedNetworkFeeData);
        baseTransactions.add(new InputBaseTransactionData(addressHash, nativeCurrencyHash, fullAmount.multiply(new BigDecimal(-1)), Instant.now()));
        baseTransactions.add(new ReceiverBaseTransactionData(addressHash, nativeCurrencyHash, amount, nativeCurrencyHash, amount, Instant.now()));

        return baseTransactions;
    }
}
