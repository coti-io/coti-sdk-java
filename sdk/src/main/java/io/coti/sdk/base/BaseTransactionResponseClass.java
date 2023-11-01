package io.coti.sdk.base;




public enum BaseTransactionResponseClass {
    IBT(InputBaseTransactionResponseData.class),
    PIBT(PaymentInputBaseTransactionResponseData.class),
    FFBT(FullNodeFeeResponseHttpData.class),
    NFBT(NetworkFeeResponseData.class),
    TGBT(TokenGenerationFeeResponseData.class),
    TMBT(TokenMintingFeeResponseData.class),
    RRBT(RollingReserveResponseData.class),
    RBT(ReceiverBaseTransactionResponseData.class),
    EIBT(EventInputBaseTransactionResponseData.class);

    private Class<? extends BaseTransactionResponseData> responseClass;

    <T extends BaseTransactionResponseData> BaseTransactionResponseClass(Class<T> responseClass) {
        this.responseClass = responseClass;
    }

    public Class<? extends BaseTransactionResponseData> getResponseClass() {
        return responseClass;
    }

    public static BaseTransactionResponseClass getName(Class<?> baseTransactionClass) {
        for (BaseTransactionResponseClass name : values()) {
            if (name.getResponseClass() == baseTransactionClass) {
                return name;
            }
        }
        return null;
    }
}