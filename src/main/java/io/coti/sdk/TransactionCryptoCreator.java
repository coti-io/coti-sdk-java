package io.coti.sdk;

import io.coti.basenode.crypto.BaseTransactionCrypto;
import io.coti.basenode.crypto.CryptoHelper;
import io.coti.basenode.crypto.TransactionCrypto;
import io.coti.basenode.crypto.TransactionSenderCrypto;
import io.coti.basenode.data.BaseTransactionData;
import io.coti.basenode.data.Hash;
import io.coti.basenode.data.SignatureData;
import io.coti.basenode.data.TransactionData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Slf4j
@Service
public class TransactionCryptoCreator {

    @Autowired
    private TransactionCrypto transactionCrypto;
    @Autowired
    private TransactionSenderCrypto transactionSenderCrypto;

    public void signBaseTransactions(TransactionData transactionData, Map<Hash, Integer> addressHashToAddressIndexMap) {

        try {
            if (transactionData.getHash() == null) {
                transactionCrypto.setTransactionHash(transactionData);
            }
            for (BaseTransactionData baseTransactionData : transactionData.getInputBaseTransactions()) {
                BaseTransactionCrypto.getByBaseTransactionClass(baseTransactionData.getClass()).signMessage(transactionData, baseTransactionData, addressHashToAddressIndexMap.get(baseTransactionData.getAddressHash()));
            }
        } catch (Exception e) {
            log.error("Transaction signing base transactions error", e);
        }
    }

    public SignatureData signTransactionData(TransactionData transactionData, Hash userPrivateKey) {
        byte[] bytesToHash = transactionSenderCrypto.getSignatureMessage(transactionData);
        return CryptoHelper.signBytes(bytesToHash, userPrivateKey.toHexString());
    }
}