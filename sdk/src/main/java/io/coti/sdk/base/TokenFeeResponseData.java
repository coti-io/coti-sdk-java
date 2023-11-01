package io.coti.sdk.base;


import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class TokenFeeResponseData extends OutputBaseTransactionResponseData {

    private String signerHash;

    public TokenFeeResponseData() {
        super();
    }

    public TokenFeeResponseData(BaseTransactionData baseTransactionData) {
        super(baseTransactionData);
        signerHash = ((TokenFeeBaseTransactionData) baseTransactionData).getSignerHash().toString();
    }
}
