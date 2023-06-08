package io.coti.sdk;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.coti.basenode.crypto.TransactionCrypto;
import io.coti.basenode.data.*;
import io.coti.basenode.exceptions.BalanceException;
import io.coti.basenode.exceptions.CotiRunTimeException;
import io.coti.basenode.http.*;
import io.coti.basenode.http.data.TransactionResponseData;
import io.coti.sdk.data.WalletDetails;
import io.coti.sdk.http.FullNodeFeeResponse;
import io.coti.sdk.http.GetTransactionTrustScoreResponse;
import io.coti.sdk.http.NetworkFeeResponse;
import io.coti.sdk.utils.Constants;
import io.coti.sdk.utils.CryptoUtils;
import io.coti.sdk.utils.Mapper;
import lombok.experimental.UtilityClass;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.*;

import static io.coti.sdk.utils.Constants.*;

@UtilityClass
public class TransactionUtilities {

    private final ObjectMapper mapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            .setSerializationInclusion(JsonInclude.Include.NON_NULL);

    public TransactionData createTransferTransaction(BigDecimal amount, String transactionDescription, Hash receiverAddress,
                                                     boolean feeIncluded, WalletDetails walletDetails) throws BalanceException {
        if (balanceNotValid(walletDetails.getAddressHash(), walletDetails.getFullNodeAddress(), amount)) {
            throw new BalanceException(Constants.INSUFFICIENT_FUNDS_MESSAGE);
        }
        List<BaseTransactionData> baseTransactions = createTransferBaseTransactions(walletDetails.getUserPrivateKey(), walletDetails.getSenderHash(), amount, walletDetails.getAddressHash(), feeIncluded, receiverAddress, walletDetails);
        return createTransactionDataByType(transactionDescription, baseTransactions, TransactionType.Transfer, walletDetails);
    }

    public List<BaseTransactionData> createTransferBaseTransactions(Hash userPrivateKey, Hash userHash, BigDecimal amount, Hash addressHash, boolean feeIncluded, Hash receiverAddress, WalletDetails walletDetails) {
        FullNodeFeeResponse fullNodeFeeResponse = FullNodeFee.createFullNodeFee(userPrivateKey, userHash, amount, feeIncluded, walletDetails.getNativeCurrencyHash(), walletDetails.getFullNodeAddress());
        FullNodeFeeData fullNodeFeeData = Mapper.map(fullNodeFeeResponse.getFullNodeFee()).toFullNodeFeeData();
        fullNodeFeeData.setHash(new Hash(fullNodeFeeResponse.getFullNodeFee().getHash()));
        fullNodeFeeData.setSignature(fullNodeFeeResponse.getFullNodeFee().getSignatureData());

        NetworkFeeResponse networkFeeResponse = NetworkFee.createNetworkFee(fullNodeFeeData, userHash, feeIncluded, walletDetails.getTrustScoreAddress());
        NetworkFeeData networkFeeData = Mapper.map(networkFeeResponse.getNetworkFeeData()).toNetworkFeeData();

        networkFeeResponse = NetworkFee.validateNetworkFee(fullNodeFeeData, networkFeeData, userHash, feeIncluded, walletDetails.getTrustScoreAddress());
        NetworkFeeData firstValidatedNetworkFeeData = Mapper.map(networkFeeResponse.getNetworkFeeData()).toNetworkFeeData();

        networkFeeResponse = NetworkFee.validateNetworkFee(fullNodeFeeData, firstValidatedNetworkFeeData, userHash, feeIncluded, walletDetails.getTrustScoreAddress());
        NetworkFeeData validatedNetworkFeeData = Mapper.map(networkFeeResponse.getNetworkFeeData()).toNetworkFeeData();

        BigDecimal fullAmount = amount.add(fullNodeFeeData.getAmount()).add(validatedNetworkFeeData.getAmount());

        List<BaseTransactionData> baseTransactions = new ArrayList<>();
        baseTransactions.add(fullNodeFeeData);
        baseTransactions.add(validatedNetworkFeeData);
        baseTransactions.add(new InputBaseTransactionData(addressHash, walletDetails.getNativeCurrencyHash(), fullAmount.multiply(new BigDecimal(-1)), Instant.now()));
        baseTransactions.add(new ReceiverBaseTransactionData(receiverAddress, walletDetails.getNativeCurrencyHash(), amount, walletDetails.getNativeCurrencyHash(), amount, Instant.now()));

        return baseTransactions;
    }

    public List<BaseTransactionData> createTokenBaseTransactions(FullNodeFeeData fullNodeFeeData, BaseTransactionData tokenFeeBT, Hash addressHash, BigDecimal amount) {
        List<BaseTransactionData> baseTransactions = new ArrayList<>();
        baseTransactions.add(fullNodeFeeData);
        baseTransactions.add(tokenFeeBT);
        baseTransactions.add(new InputBaseTransactionData(addressHash, fullNodeFeeData.getCurrencyHash(), amount.multiply(new BigDecimal(-1)), Instant.now()));
        return baseTransactions;
    }

    public TransactionData createTokenTransactionByType(BaseTransactionData tokenFeeBT, FullNodeFeeResponse fullNodeFeeResponse,
                                                        String transactionDescription, TransactionType transactionType, WalletDetails transactionDetails) {
        FullNodeFeeData fullNodeFeeData = Mapper.map(fullNodeFeeResponse.getFullNodeFee()).toFullNodeFeeData();
        fullNodeFeeData.setHash(new Hash(fullNodeFeeResponse.getFullNodeFee().getHash()));
        fullNodeFeeData.setSignature(fullNodeFeeResponse.getFullNodeFee().getSignatureData());

        BigDecimal amount = tokenFeeBT.getAmount().add(fullNodeFeeData.getAmount());
        if (balanceNotValid(transactionDetails.getAddressHash(), transactionDetails.getFullNodeAddress(), amount)) {
            BigDecimal balance = AddressUtilities.getAddressBalance(transactionDetails.getAddressHash(), transactionDetails.getFullNodeAddress());
            throw new BalanceException(String.format(Constants.INSUFFICIENT_FUNDS_MESSAGE, balance, amount));
        }

        List<BaseTransactionData> baseTransactions = createTokenBaseTransactions(fullNodeFeeData, tokenFeeBT, transactionDetails.getAddressHash(), amount);
        return createTransactionDataByType(transactionDescription, baseTransactions, transactionType, transactionDetails);
    }

    private boolean balanceNotValid(Hash addressHash, String fullNodeAddress, BigDecimal amount) {
        BigDecimal balance = AddressUtilities.getAddressBalance(addressHash, fullNodeAddress);
        return balance.compareTo(amount) < 0;
    }

    private TransactionData createTransactionDataByType(String transactionDescription, List<BaseTransactionData> baseTransactions, TransactionType transactionType,
                                                        WalletDetails transactionDetails) {
        CryptoUtils.createAndSetBaseTransactionsHash(baseTransactions);
        TransactionTrustScoreData transactionTrustScoreData = getTrustScoreData(baseTransactions, transactionDetails);
        double trustScore = transactionTrustScoreData.getTrustScore();
        if (trustScore == ZERO) {
            throw new CotiRunTimeException(INSUFFICIENT_TRUST_SCORE_MESSAGE);
        }

        Map<Hash, Integer> addressHashToAddressIndexMap = new HashMap<>();
        addressHashToAddressIndexMap.put(transactionDetails.getAddressHash(), transactionDetails.getWalletAddressIndex());
        Instant creationTime = Instant.now();
        TransactionData transactionData = new TransactionData(baseTransactions, transactionDescription, trustScore, creationTime, transactionType);
        transactionData.setAmount(getTotalNativeAmount(transactionData, transactionDetails));
        transactionData.setAttachmentTime(creationTime);
        CryptoUtils.signBaseTransactions(transactionData, addressHashToAddressIndexMap, transactionDetails.getSeed());
        TransactionCrypto transactionCrypto = new TransactionCrypto();
        transactionCrypto.signMessage(transactionData, transactionDetails.getSenderHash(), transactionDetails.getUserPrivateKey().toHexString());
        transactionData.setTrustScoreResults(Collections.singletonList(transactionTrustScoreData));
        transactionData.setSenderHash(transactionDetails.getSenderHash());
        transactionData.setSenderSignature(CryptoUtils.signTransactionData(transactionData, transactionDetails.getUserPrivateKey()));

        return transactionData;
    }

    private TransactionTrustScoreData getTrustScoreData(List<BaseTransactionData> baseTransactions, WalletDetails transactionDetails) {
        GetTransactionTrustScoreResponse trustScoreResponse = TrustScoreUtilities.getTransactionTrustScoreData(CryptoUtils.getHashFromBaseTransactionHashesData(baseTransactions), transactionDetails.getUserPrivateKey(), transactionDetails.getSenderHash(), transactionDetails);
        double trustScore = 0;
        TransactionTrustScoreData transactionTrustScoreData = new TransactionTrustScoreData(trustScore);
        if (trustScoreResponse.getTransactionTrustScoreData() != null) {
            transactionTrustScoreData = Mapper.map(trustScoreResponse.getTransactionTrustScoreData()).toTrustScoreData();
        }
        return transactionTrustScoreData;
    }

    private BigDecimal getTotalNativeAmount(TransactionData transactionData, WalletDetails transactionDetails) {
        BigDecimal totalAmount = BigDecimal.ZERO;
        List<BaseTransactionData> baseTransactions = transactionData.getBaseTransactions();

        for (BaseTransactionData baseTransaction : baseTransactions) {
            if (transactionDetails.getNativeCurrencyHash().equals(baseTransaction.getCurrencyHash())) {
                totalAmount = totalAmount.add(baseTransaction.getAmount().signum() > 0 ? baseTransaction.getAmount() : BigDecimal.ZERO);
            }
        }

        return totalAmount;
    }

    public GetTransactionResponse getTransaction(GetTransactionRequest getTransactionRequest, String fullNodeUrl) {
        ResponseEntity<GetTransactionResponse> response = (ResponseEntity<GetTransactionResponse>) Utilities.postRequest(fullNodeUrl + TRANSACTION, getTransactionRequest, GetTransactionResponse.class);
        return response.getBody();
    }


    public GetTransactionsResponse getNoneIndexedTransactions(String fullNodeUrl) {
        ResponseEntity<GetTransactionsResponse> response = (ResponseEntity<GetTransactionsResponse>) Utilities.getRequest(fullNodeUrl + TRANSACTION_NON_INDEXED, GetTransactionsResponse.class);
        return response.getBody();
    }

    public List<TransactionResponseData> getAddressTransactionBatch(GetAddressTransactionBatchRequest getAddressTransactionBatchRequest, String fullNodeUrl) throws JsonProcessingException {
        ResponseEntity<String> response = (ResponseEntity<String>) Utilities.postRequest(fullNodeUrl + ADDRESS_TRANSACTIONS_BATCH, getAddressTransactionBatchRequest, String.class);
        return mapper.readValue(response.getBody(), new TypeReference<List<TransactionResponseData>>() {
        });
    }

    public List<TransactionResponseData> getTransactionsHistoryByTimeStamp(GetAddressTransactionBatchByTimestampRequest getAddressTransactionBatchByTimestampRequest, String fullNodeUrl) throws JsonProcessingException {
        ResponseEntity<String> response = (ResponseEntity<String>) Utilities.postRequest(fullNodeUrl + ADDRESS_TRANSACTIONS_TIMESTAMP_BATCH, getAddressTransactionBatchByTimestampRequest, String.class);
        return mapper.readValue(response.getBody(), new TypeReference<List<TransactionResponseData>>() {
        });
    }
}
