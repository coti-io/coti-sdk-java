package io.coti.sdk;

import io.coti.basenode.crypto.OriginatorCurrencyCrypto;
import io.coti.basenode.data.*;
import io.coti.sdk.http.FullNodeFeeResponse;
import io.coti.sdk.http.NetworkFeeResponse;
import io.coti.sdk.utils.Mapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class BaseTransactionCreation {

    @Value("${native.currency.symbol:COTI}")
    private String nativeCurrencySymbol;
    private Hash nativeCurrencyHash;
    @Autowired
    private FullNodeFee fullNodeFee;
    @Autowired
    private NetworkFee networkFee;

    @PostConstruct
    private void init() {
        nativeCurrencyHash = OriginatorCurrencyCrypto.calculateHash(nativeCurrencySymbol);
    }


    public List<BaseTransactionData> createBaseTransactions(BigDecimal amount, Hash addressHash, boolean feeIncluded) {
        FullNodeFeeResponse fullNodeFeeResponse = fullNodeFee.createFullNodeFee(amount, feeIncluded);
        FullNodeFeeData fullNodeFeeData = Mapper.map(fullNodeFeeResponse.getFullNodeFee()).toFullNodeFeeData();
        fullNodeFeeData.setHash(new Hash(fullNodeFeeResponse.getFullNodeFee().getHash()));
        fullNodeFeeData.setSignature(fullNodeFeeResponse.getFullNodeFee().getSignatureData());

        NetworkFeeResponse networkFeeResponse = networkFee.createNetworkFee(fullNodeFeeData, feeIncluded);
        NetworkFeeData networkFeeData = Mapper.map(networkFeeResponse.getNetworkFeeData()).toNetworkFeeData();

        networkFeeResponse = networkFee.validateNetworkFee(fullNodeFeeData, networkFeeData, feeIncluded);
        NetworkFeeData firstValidatedNetworkFeeData = Mapper.map(networkFeeResponse.getNetworkFeeData()).toNetworkFeeData();

        networkFeeResponse = networkFee.validateNetworkFee(fullNodeFeeData, firstValidatedNetworkFeeData, feeIncluded);
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
