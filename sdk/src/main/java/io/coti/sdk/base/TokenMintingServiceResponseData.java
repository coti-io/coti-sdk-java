package io.coti.sdk.base;

import lombok.Data;

import java.math.BigDecimal;
import java.time.Instant;

@Data
public class TokenMintingServiceResponseData implements ITransactionResponseData {

    private String mintingCurrencyHash;
    private BigDecimal mintingAmount;
    private String receiverAddress;
    private Instant createTime;
    private BigDecimal feeAmount;
    private String signerHash;
    private SignatureData signature;

    public TokenMintingServiceResponseData() {
    }

    public TokenMintingServiceResponseData(BaseTransactionData baseTransactionData) {
        TokenMintingServiceData tokenMintingServiceData = ((TokenMintingFeeBaseTransactionData) baseTransactionData).getServiceData();
        this.setMintingAmount(tokenMintingServiceData.getMintingAmount());
        this.setMintingCurrencyHash(tokenMintingServiceData.getMintingCurrencyHash().toString());
        this.setFeeAmount(tokenMintingServiceData.getFeeAmount());
        this.setReceiverAddress(tokenMintingServiceData.getReceiverAddress().toString());
        this.setCreateTime(tokenMintingServiceData.getCreateTime());
        this.setSignerHash(tokenMintingServiceData.getSignerHash().toString());
        this.setSignature(tokenMintingServiceData.getSignature());
    }
}
