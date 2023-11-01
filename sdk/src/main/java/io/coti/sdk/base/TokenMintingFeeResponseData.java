package io.coti.sdk.base;


import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class TokenMintingFeeResponseData extends TokenFeeResponseData {

    TokenMintingServiceResponseData tokenMintingServiceData;

    public TokenMintingFeeResponseData() {
        super();
    }

    public TokenMintingFeeResponseData(BaseTransactionData baseTransactionData) {
        super(baseTransactionData);
        tokenMintingServiceData = new TokenMintingServiceResponseData(baseTransactionData);
    }
}
