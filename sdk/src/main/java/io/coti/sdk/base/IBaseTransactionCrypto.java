package io.coti.sdk.base;


public interface IBaseTransactionCrypto {
    byte[] getMessageInBytes(BaseTransactionData baseTransactionData);

    Class<? extends BaseTransactionData> getBaseTransactionClass();

    void createAndSetBaseTransactionHash(BaseTransactionData baseTransactionData);

    byte[] getSignatureMessage(TransactionData transactionData);

    byte[] getSignatureMessage(TransactionData transactionData, TrustScoreNodeResultData trustScoreNodeResultData);

    void signMessage(TransactionData transactionData, BaseTransactionData baseTransactionData, int index);

    void signMessage(TransactionData transactionData, BaseTransactionData baseTransactionData, int index, String seed);

    <T extends BaseTransactionData & ITrustScoreNodeValidatable> void signMessage(TransactionData transactionData, T baseTransactionData, TrustScoreNodeResultData trustScoreNodeResultData);

    String getPublicKey(BaseTransactionData receiverBaseTransactionData);

    String getPublicKey(TrustScoreNodeResultData trustScoreNodeResultData);

    Hash createBaseTransactionHashFromData(BaseTransactionData baseTransactionData);

    boolean isBaseTransactionValid(TransactionData transactionData, BaseTransactionData baseTransactionData);

    boolean verifySignature(TransactionData transactionData, BaseTransactionData baseTransactionData);

}
