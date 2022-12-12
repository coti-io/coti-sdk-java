package io.coti.sdk;

import io.coti.basenode.crypto.NodeCryptoHelper;
import io.coti.basenode.crypto.OriginatorCurrencyCrypto;
import io.coti.basenode.crypto.TransactionCrypto;
import io.coti.basenode.data.*;
import io.coti.sdk.http.AddTransactionRequest;
import io.coti.sdk.http.GetTransactionTrustScoreResponse;
import io.coti.sdk.utils.CryptoUtils;
import io.coti.sdk.utils.Mapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class TransactionCreation {

    public static final String INSUFFICIENT_FUNDS_MESSAGE = "Balance for address is insufficient!";
    @Value("${seed}")
    private String seed;
    @Value("${user.hash}")
    private String userHash;
    @Value("${full.node.backend.address}")
    private String fullNodeAddress;
    @Value("${native.currency.symbol:COTI}")
    private String nativeCurrencySymbol;
    @Value("${transaction.description:}")
    private String description;
    @Value("${transfer.amount}")
    private String amountString;
    @Value("${source.address.index}")
    private int walletAddressIndex;
    private Hash nativeCurrencyHash;
    private BigDecimal amount;
    @Autowired
    private TransactionCryptoCreator transactionCryptoCreator;
    @Autowired
    private TransactionCrypto transactionCrypto;
    @Autowired
    private BaseTransactionCreation baseTransactionCreation;
    @Autowired
    private TrustScoreData trustScoreData;
    @Autowired
    private AccountBalance accountBalance;

    @PostConstruct
    private void init() {
        nativeCurrencyHash = OriginatorCurrencyCrypto.calculateHash(nativeCurrencySymbol);
        amount = new BigDecimal(amountString);
    }

    public AddTransactionRequest createAddTransactionRequest() {
        Hash addressHash = NodeCryptoHelper.generateAddress(seed, walletAddressIndex);
        Hash userPrivateKey = CryptoUtils.getPrivateKeyFromSeed((new Hash(seed)).getBytes());
        Hash senderHash = new Hash(userHash);
        if (balanceNotValid(addressHash, fullNodeAddress)) {
            throw new RuntimeException(INSUFFICIENT_FUNDS_MESSAGE);
        }
        List<BaseTransactionData> baseTransactions = baseTransactionCreation.createBaseTransactions(amount, addressHash, true);
        CryptoUtils.createAndSetBaseTransactionsHash(baseTransactions);
        GetTransactionTrustScoreResponse trustScoreResponse = getTrustScore(baseTransactions);
        double trustScore = 0;
        TransactionTrustScoreData transactionTrustScoreData = new TransactionTrustScoreData(trustScore);
        if (trustScoreResponse != null && trustScoreResponse.getTransactionTrustScoreData() != null) {
            transactionTrustScoreData = Mapper.map(trustScoreResponse.getTransactionTrustScoreData()).toTrustScoreData();
            trustScore = trustScoreResponse.getTransactionTrustScoreData().getTrustScore();
        }
        TransactionData transactionData = createTransactionData(baseTransactions, description, trustScore, addressHash, TransactionType.Transfer);
        transactionData.setTrustScoreResults(Collections.singletonList(transactionTrustScoreData));
        transactionData.setSenderHash(senderHash);
        transactionData.setSenderSignature(transactionCryptoCreator.signTransactionData(transactionData, userPrivateKey));

        return new AddTransactionRequest(transactionData);
    }

    private boolean balanceNotValid(Hash addressHash, String fullNodeAddress) {
        BigDecimal balance = accountBalance.getAccountBalance(addressHash, fullNodeAddress);
        return balance.compareTo(amount) < 0;
    }

    private GetTransactionTrustScoreResponse getTrustScore(List<BaseTransactionData> baseTransactions) {
        return trustScoreData.getTransactionTrustScoreData(CryptoUtils.getHashFromBaseTransactionHashesData(baseTransactions));
    }

    private TransactionData createTransactionData(List<BaseTransactionData> baseTransactions, String description, double trustScore, Hash addressHash, TransactionType transfer) {
        Map<Hash, Integer> addressHashToAddressIndexMap = new HashMap<>();
        addressHashToAddressIndexMap.put(addressHash, walletAddressIndex);
        TransactionData transactionData = createNewTransaction(baseTransactions, description, trustScore, Instant.now(), transfer);
        transactionData.setAttachmentTime(Instant.now());
        transactionCryptoCreator.signBaseTransactions(transactionData, addressHashToAddressIndexMap);
        transactionCrypto.signMessage(transactionData);
        log.info("New transfer transaction {} created successfully", transactionData.getHash());
        return transactionData;
    }

    private TransactionData createNewTransaction(List<BaseTransactionData> baseTransactions, String transactionDescription, double senderTrustScore, Instant createTime, TransactionType type) {
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
