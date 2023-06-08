package io.coti.sdk.utils;

import io.coti.basenode.crypto.*;
import io.coti.basenode.data.*;
import io.coti.basenode.http.GetTokenMintingFeeQuoteRequest;
import io.coti.basenode.http.GetUserTokensRequest;
import io.coti.sdk.data.TokenMintingServiceData;
import lombok.experimental.UtilityClass;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.List;
import java.util.Map;

@UtilityClass
public class CryptoUtils {

    @NotNull
    public Hash getPrivateKeyFromSeed(byte[] seed) {
        return CryptoHelper.cryptoHash(seed);
    }

    public SignatureData signFullNodeFeeData(BigDecimal amount, Hash userPrivateKey, Hash nativeCurrencyHash) {
        byte[] nativeCurrencyHashInBytes = nativeCurrencyHash.getBytes();
        String decimalAmountRepresentation = amount.stripTrailingZeros().toPlainString();
        byte[] originalAmountInBytes = decimalAmountRepresentation.getBytes(StandardCharsets.UTF_8);

        ByteBuffer fullNodeFeeBuffer = ByteBuffer.allocate(nativeCurrencyHashInBytes.length + originalAmountInBytes.length)
                .put(nativeCurrencyHashInBytes).put(originalAmountInBytes);
        return CryptoHelper.signBytes(CryptoUtils.getPrivateKeyFromSeed(fullNodeFeeBuffer.array()).getBytes(), userPrivateKey.toHexString());
    }

    public SignatureData signTreasuryCreateDepositData(BigDecimal leverage, BigDecimal locking, BigDecimal nextLock, Hash userPrivateKey) {

        byte[] leverageBytes = leverage.stripTrailingZeros().toPlainString().getBytes(StandardCharsets.UTF_8);
        byte[] lockingBytes = locking.stripTrailingZeros().toPlainString().getBytes(StandardCharsets.UTF_8);
        byte[] nextLockBytes = nextLock.stripTrailingZeros().toPlainString().getBytes(StandardCharsets.UTF_8);
        byte[] instantBytes = ByteBuffer.allocate(8).putLong(Instant.now().toEpochMilli()).array();
        ByteBuffer TreasuryBuffer = ByteBuffer.allocate( leverageBytes.length + lockingBytes.length + nextLockBytes.length + instantBytes.length)
                .put(leverageBytes).put(lockingBytes).put(nextLockBytes).put(instantBytes);
        return CryptoHelper.signBytes(CryptoUtils.getPrivateKeyFromSeed(TreasuryBuffer.array()).getBytes(), userPrivateKey.toHexString());
    }


    public void createAndSetBaseTransactionsHash(List<BaseTransactionData> baseTransactions) {
        for (BaseTransactionData baseTransactionData : baseTransactions) {
            if (baseTransactionData.getHash() == null) {
                BaseTransactionCrypto.getByBaseTransactionClass(baseTransactionData.getClass()).createAndSetBaseTransactionHash(baseTransactionData);
            }
        }
    }

    public Hash getHashFromBaseTransactionHashesData(List<BaseTransactionData> baseTransactions) {
        byte[] bytesToHash = getBaseTransactionsHashesBytes(baseTransactions);
        return CryptoHelper.cryptoHash(bytesToHash);
    }

    private byte[] getBaseTransactionsHashesBytes(List<BaseTransactionData> baseTransactions) {
        ByteBuffer baseTransactionHashBuffer = ByteBuffer.allocate(baseTransactions.size() * Constants.BASE_TRANSACTION_HASH_SIZE);
        baseTransactions.forEach(baseTransaction -> {
            byte[] baseTransactionHashBytes = baseTransaction.getHash().getBytes();
            baseTransactionHashBuffer.put(baseTransactionHashBytes);
        });
        return baseTransactionHashBuffer.array();
    }

    public byte[] getTokenMintingServiceDataBytes(TokenMintingServiceData tokenMintingServiceData) {
        byte[] bytesOfCurrencyHash = tokenMintingServiceData.getMintingCurrencyHash().getBytes();
        byte[] bytesOfAmount = tokenMintingServiceData.getMintingAmount().stripTrailingZeros().toPlainString().getBytes(StandardCharsets.UTF_8);
        byte[] bytesOfFeeAmount = tokenMintingServiceData.getFeeAmount() != null ? tokenMintingServiceData.getFeeAmount().stripTrailingZeros().toPlainString().getBytes(StandardCharsets.UTF_8) : new byte[0];
        byte[] bytesOfReceiverAddress = tokenMintingServiceData.getReceiverAddress().getBytes();
        ByteBuffer tokenMintingDataBuffer = ByteBuffer
                .allocate(bytesOfCurrencyHash.length + bytesOfAmount.length + bytesOfFeeAmount.length + bytesOfReceiverAddress.length + 8)
                .put(bytesOfCurrencyHash)
                .put(bytesOfAmount)
                .put(bytesOfFeeAmount)
                .put(bytesOfReceiverAddress)
                .putLong(tokenMintingServiceData.getCreateTime().toEpochMilli());
        return CryptoHelper.cryptoHash(tokenMintingDataBuffer.array()).getBytes();
    }

    public void signBaseTransactions(TransactionData transactionData, Map<Hash, Integer> addressHashToAddressIndexMap, String seed) {
        TransactionCrypto transactionCrypto = new TransactionCrypto();
        if (transactionData.getHash() == null) {
            transactionCrypto.setTransactionHash(transactionData);
        }
        for (BaseTransactionData baseTransactionData : transactionData.getInputBaseTransactions()) {
            BaseTransactionCrypto.getByBaseTransactionClass(baseTransactionData.getClass()).signMessage(transactionData, baseTransactionData, addressHashToAddressIndexMap.get(baseTransactionData.getAddressHash()), seed);
        }
    }

    public SignatureData signTransactionData(TransactionData transactionData, Hash userPrivateKey) {
        TransactionSenderCrypto transactionSenderCrypto = new TransactionSenderCrypto();
        byte[] bytesToHash = transactionSenderCrypto.getSignatureMessage(transactionData);
        return CryptoHelper.signBytes(bytesToHash, userPrivateKey.toHexString());
    }

    public SignatureData signGetUserTokensRequest(GetUserTokensRequest userTokensRequest, Hash userPrivateKey) {
        GetUserTokensRequestCrypto userTokensRequestCrypto = new GetUserTokensRequestCrypto();
        byte[] bytesToHash = userTokensRequestCrypto.getSignatureMessage(userTokensRequest);
        return CryptoHelper.signBytes(bytesToHash, userPrivateKey.toHexString());
    }

    public SignatureData signOriginatorCurrencyData(OriginatorCurrencyData originatorCurrencyData, String userPrivateKey) {
        OriginatorCurrencyCrypto originatorCurrencyCrypto = new OriginatorCurrencyCrypto();
        byte[] bytesToHash = originatorCurrencyCrypto.getSignatureMessage(originatorCurrencyData);
        return CryptoHelper.signBytes(bytesToHash, userPrivateKey);
    }

    public SignatureData signCurrencyTypeRegistrationData(CurrencyTypeRegistrationData currencyTypeRegistrationData, String userPrivateKey) {
        CurrencyTypeRegistrationCrypto currencyTypeRegistrationCrypto = new CurrencyTypeRegistrationCrypto();
        byte[] bytesToHash = currencyTypeRegistrationCrypto.getSignatureMessage(currencyTypeRegistrationData);
        return CryptoHelper.signBytes(bytesToHash, userPrivateKey);
    }

    public SignatureData signGetTokenMintingFeeQuoteRequest(GetTokenMintingFeeQuoteRequest getTokenMintingFeeQuoteRequest, Hash userPrivateKey) {
        byte[] currencyHashInBytes = getTokenMintingFeeQuoteRequest.getCurrencyHash().getBytes();
        byte[] amountInBytes = getTokenMintingFeeQuoteRequest.getMintingAmount().stripTrailingZeros().toPlainString().getBytes(StandardCharsets.UTF_8);
        byte[] createTimeInBytes = ByteBuffer.allocate(Long.BYTES).putLong(getTokenMintingFeeQuoteRequest.getCreateTime().toEpochMilli()).array();

        ByteBuffer getMintingQuotesRequestBuffer =
                ByteBuffer.allocate(currencyHashInBytes.length + amountInBytes.length + createTimeInBytes.length)
                        .put(currencyHashInBytes).put(amountInBytes).put(createTimeInBytes);
        return CryptoHelper.signBytes(CryptoHelper.cryptoHash(getMintingQuotesRequestBuffer.array()).getBytes(), userPrivateKey.toHexString());
    }

    public SignatureData signTokenMintingServiceData(TokenMintingServiceData tokenMintingServiceData, Hash userPrivateKey) {
        byte[] bytesToHash = getTokenMintingServiceDataBytes(tokenMintingServiceData);
        return CryptoHelper.signBytes(bytesToHash, userPrivateKey.toHexString());
    }
}
