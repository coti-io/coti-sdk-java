package io.coti.sdk;

import io.coti.basenode.crypto.CryptoHelper;
import io.coti.basenode.crypto.TransactionCrypto;
import io.coti.basenode.data.*;
import io.coti.basenode.exceptions.BalanceException;
import io.coti.sdk.http.GetTransactionTrustScoreResponse;
import io.coti.sdk.utils.Constants;
import io.coti.sdk.utils.CryptoUtils;
import io.coti.sdk.utils.Mapper;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TransactionCreation {

    private String fullNodeAddress;
    private String trustScoreAddress;
    private int walletAddressIndex;
    private Hash nativeCurrencyHash;
    private Hash userPrivateKey;
    private Hash senderHash;
    private Hash addressHash;
    private String seed;

    public TransactionCreation(String seed, String userHash, String trustScoreAddress, String fullNodeAddress,
                               int walletAddressIndex, Hash nativeCurrencyHash) {
        this.trustScoreAddress = trustScoreAddress;
        this.fullNodeAddress = fullNodeAddress;
        this.walletAddressIndex = walletAddressIndex;
        this.nativeCurrencyHash = nativeCurrencyHash;
        this.seed = seed;
        this.addressHash = CryptoHelper.generateAddress(seed, walletAddressIndex);
        this.userPrivateKey = CryptoUtils.getPrivateKeyFromSeed((new Hash(seed)).getBytes());
        this.senderHash = new Hash(userHash);
    }

    public TransactionData createTransferTransaction(BigDecimal amount, String transactionDescription, Hash receiverAddress,
                                                     boolean feeIncluded) throws BalanceException {
        if (balanceNotValid(addressHash, fullNodeAddress, amount)) {
            throw new BalanceException(Constants.INSUFFICIENT_FUNDS_MESSAGE);
        }
        BaseTransactionCreation baseTransactionCreation = new BaseTransactionCreation(nativeCurrencyHash, fullNodeAddress, trustScoreAddress);
        List<BaseTransactionData> baseTransactions = baseTransactionCreation.createBaseTransactions(userPrivateKey, senderHash, amount, addressHash, feeIncluded, receiverAddress);
        CryptoUtils.createAndSetBaseTransactionsHash(baseTransactions);
        GetTransactionTrustScoreResponse trustScoreResponse = getTrustScore(baseTransactions, userPrivateKey, senderHash);
        double trustScore = 0;
        TransactionTrustScoreData transactionTrustScoreData = new TransactionTrustScoreData(trustScore);
        if (trustScoreResponse.getTransactionTrustScoreData() != null) {
            transactionTrustScoreData = Mapper.map(trustScoreResponse.getTransactionTrustScoreData()).toTrustScoreData();
            trustScore = trustScoreResponse.getTransactionTrustScoreData().getTrustScore();
        }
        TransactionData transactionData = createTransferTransactionData(baseTransactions, transactionDescription, trustScore, addressHash);
        transactionData.setTrustScoreResults(Collections.singletonList(transactionTrustScoreData));
        transactionData.setSenderHash(senderHash);
        transactionData.setSenderSignature(CryptoUtils.signTransactionData(transactionData, userPrivateKey));


        return transactionData;
    }

    private boolean balanceNotValid(Hash addressHash, String fullNodeAddress, BigDecimal amount) {
        BigDecimal balance = AccountBalance.getAccountBalance(addressHash, fullNodeAddress);
        return balance.compareTo(amount) < 0;
    }

    private GetTransactionTrustScoreResponse getTrustScore(List<BaseTransactionData> baseTransactions, Hash userPrivateKey, Hash senderHash) {
        TrustScoreData trustScoreData = new TrustScoreData(trustScoreAddress);
        return trustScoreData.getTransactionTrustScoreData(CryptoUtils.getHashFromBaseTransactionHashesData(baseTransactions), userPrivateKey, senderHash);
    }

    private TransactionData createTransferTransactionData(List<BaseTransactionData> baseTransactions, String description, double trustScore,
                                                          Hash addressHash) {
        Map<Hash, Integer> addressHashToAddressIndexMap = new HashMap<>();
        addressHashToAddressIndexMap.put(addressHash, walletAddressIndex);
        Instant creationTime = Instant.now();
        TransactionData transactionData = new TransactionData(baseTransactions, description, trustScore, creationTime, TransactionType.Transfer);
        transactionData.setAmount(getTotalNativeAmount(transactionData));
        transactionData.setAttachmentTime(creationTime);
        CryptoUtils.signBaseTransactions(transactionData, addressHashToAddressIndexMap, seed);

        TransactionCrypto transactionCrypto = new TransactionCrypto();
        transactionCrypto.signMessage(transactionData, senderHash, userPrivateKey.toHexString());
        return transactionData;
    }

    private BigDecimal getTotalNativeAmount(TransactionData transactionData) {
        BigDecimal totalAmount = BigDecimal.ZERO;
        List<BaseTransactionData> baseTransactions = transactionData.getBaseTransactions();

        for (BaseTransactionData baseTransaction : baseTransactions) {
            if (nativeCurrencyHash.equals(baseTransaction.getCurrencyHash())) {
                totalAmount = totalAmount.add(baseTransaction.getAmount().signum() > 0 ? baseTransaction.getAmount() : BigDecimal.ZERO);
            }
        }

        return totalAmount;
    }
}
