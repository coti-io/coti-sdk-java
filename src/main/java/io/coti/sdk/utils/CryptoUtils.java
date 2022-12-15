package io.coti.sdk.utils;

import io.coti.basenode.crypto.BaseTransactionCrypto;
import io.coti.basenode.crypto.CryptoHelper;
import io.coti.basenode.crypto.TransactionCrypto;
import io.coti.basenode.crypto.TransactionSenderCrypto;
import io.coti.basenode.data.BaseTransactionData;
import io.coti.basenode.data.Hash;
import io.coti.basenode.data.SignatureData;
import io.coti.basenode.data.TransactionData;
import lombok.experimental.UtilityClass;
import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
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

    private static byte[] getBaseTransactionsHashesBytes(List<BaseTransactionData> baseTransactions) {
        ByteBuffer baseTransactionHashBuffer = ByteBuffer.allocate(baseTransactions.size() * Constants.BASE_TRANSACTION_HASH_SIZE);
        baseTransactions.forEach(baseTransaction -> {
            byte[] baseTransactionHashBytes = baseTransaction.getHash().getBytes();
            baseTransactionHashBuffer.put(baseTransactionHashBytes);
        });
        return baseTransactionHashBuffer.array();
    }

    public static void signBaseTransactions(TransactionData transactionData, Map<Hash, Integer> addressHashToAddressIndexMap) {
        TransactionCrypto transactionCrypto = new TransactionCrypto();
        if (transactionData.getHash() == null) {
            transactionCrypto.setTransactionHash(transactionData);
        }
        for (BaseTransactionData baseTransactionData : transactionData.getInputBaseTransactions()) {
            BaseTransactionCrypto.getByBaseTransactionClass(baseTransactionData.getClass()).signMessage(transactionData, baseTransactionData, addressHashToAddressIndexMap.get(baseTransactionData.getAddressHash()));
        }
    }

    public static SignatureData signTransactionData(TransactionData transactionData, Hash userPrivateKey) {
        TransactionSenderCrypto transactionSenderCrypto = new TransactionSenderCrypto();
        byte[] bytesToHash = transactionSenderCrypto.getSignatureMessage(transactionData);
        return CryptoHelper.signBytes(bytesToHash, userPrivateKey.toHexString());
    }
}
