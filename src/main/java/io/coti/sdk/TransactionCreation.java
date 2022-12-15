package io.coti.sdk;

import io.coti.basenode.crypto.NodeCryptoHelper;
import io.coti.basenode.crypto.TransactionCrypto;
import io.coti.basenode.data.*;
import io.coti.basenode.exceptions.CotiRunTimeException;
import io.coti.sdk.http.GetTransactionTrustScoreResponse;
import io.coti.sdk.utils.Constants;
import io.coti.sdk.utils.CryptoUtils;
import io.coti.sdk.utils.Mapper;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
public class TransactionCreation {

    private String trustScoreAddress;
    private String fullNodeAddress;
    private int walletAddressIndex;
    private Hash nativeCurrencyHash;
    private Hash userPrivateKey;
    private Hash senderHash;
    private Hash addressHash;

    public TransactionCreation(String seed, String userHash, String trustScoreAddress, String fullNodeAddress,
                               int walletAddressIndex, Hash nativeCurrencyHash) {
        this.trustScoreAddress = trustScoreAddress;
        this.fullNodeAddress = fullNodeAddress;
        this.walletAddressIndex = walletAddressIndex;
        this.nativeCurrencyHash = nativeCurrencyHash;
        this.addressHash = NodeCryptoHelper.generateAddress(seed, walletAddressIndex);
        this.userPrivateKey = CryptoUtils.getPrivateKeyFromSeed((new Hash(seed)).getBytes());
        this.senderHash = new Hash(userHash);
    }

    public TransactionData createTransactionData(BigDecimal amount, String transactionDescription) {
        TransactionData transactionData = null;
        try {
            if (balanceNotValid(addressHash, fullNodeAddress, amount)) {
                log.error(Constants.INSUFFICIENT_FUNDS_MESSAGE);
                return transactionData;
            }
            BaseTransactionCreation baseTransactionCreation = new BaseTransactionCreation(nativeCurrencyHash, fullNodeAddress, trustScoreAddress);
            List<BaseTransactionData> baseTransactions = baseTransactionCreation.createBaseTransactions(userPrivateKey, senderHash, amount, addressHash, true);
            CryptoUtils.createAndSetBaseTransactionsHash(baseTransactions);
            GetTransactionTrustScoreResponse trustScoreResponse = getTrustScore(baseTransactions, userPrivateKey, senderHash);
            double trustScore = 0;
            TransactionTrustScoreData transactionTrustScoreData = new TransactionTrustScoreData(trustScore);
            if (trustScoreResponse.getTransactionTrustScoreData() != null) {
                transactionTrustScoreData = Mapper.map(trustScoreResponse.getTransactionTrustScoreData()).toTrustScoreData();
                trustScore = trustScoreResponse.getTransactionTrustScoreData().getTrustScore();
            }
            transactionData = createTransactionData(baseTransactions, transactionDescription, trustScore, addressHash, TransactionType.Transfer);
            transactionData.setTrustScoreResults(Collections.singletonList(transactionTrustScoreData));
            transactionData.setSenderHash(senderHash);
            transactionData.setSenderSignature(CryptoUtils.signTransactionData(transactionData, userPrivateKey));
        } catch (CotiRunTimeException e) {
            log.error("Exception: ", e);
        }

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

    private TransactionData createTransactionData(List<BaseTransactionData> baseTransactions, String description, double trustScore,
                                                  Hash addressHash, TransactionType transfer) {
        Map<Hash, Integer> addressHashToAddressIndexMap = new HashMap<>();
        addressHashToAddressIndexMap.put(addressHash, walletAddressIndex);
        TransactionData transactionData = createNewTransaction(baseTransactions, description, trustScore, Instant.now(), transfer);
        transactionData.setAttachmentTime(Instant.now());
        try {
            CryptoUtils.signBaseTransactions(transactionData, addressHashToAddressIndexMap);
        } catch (Exception e) {
            log.error("Transaction signing base transactions error", e);
        }
        TransactionCrypto transactionCrypto = new TransactionCrypto();
        transactionCrypto.signMessage(transactionData);
        log.info("New transfer transaction {} created successfully", transactionData.getHash());
        return transactionData;
    }

    private TransactionData createNewTransaction(List<BaseTransactionData> baseTransactions, String transactionDescription,
                                                 double senderTrustScore, Instant createTime, TransactionType type) {
        TransactionData transactionData = new TransactionData(baseTransactions, transactionDescription, senderTrustScore, createTime, type);
        transactionData.setAmount(getTotalNativeAmount(transactionData));
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
