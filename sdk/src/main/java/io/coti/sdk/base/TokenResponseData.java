package io.coti.sdk.base;



import lombok.Data;

import java.math.BigDecimal;
import java.time.Instant;

@Data
public class TokenResponseData implements IResponseData {

    private String currencyName;
    private String currencySymbol;
    private String currencyHash;
    private String description;
    private BigDecimal totalSupply;
    private int scale;
    private String originatorHash;
    private SignatureData originatorSignature;
    private Instant createTime;
    private String currencyGeneratingTransactionHash;
    private String currencyLastTypeChangingTransactionHash;
    private boolean confirmed;

    private CurrencyType currencyType;
    private CurrencyRateSourceType currencyRateSourceType;
    private String rateSource;
    private String protectionModel;

    private BigDecimal mintedAmount;
    private BigDecimal mintableAmount;

    public TokenResponseData() {
    }

    public TokenResponseData(CurrencyData token) {
        this.currencyName = token.getName();
        this.currencySymbol = token.getSymbol();
        this.currencyHash = token.getHash().toString();
        this.description = token.getDescription();
        this.totalSupply = token.getTotalSupply();
        this.scale = token.getScale();
        this.originatorHash = token.getOriginatorHash().toString();
        this.originatorSignature = token.getOriginatorSignature();
        this.createTime = token.getCreateTime();
        this.currencyGeneratingTransactionHash = (token.getCurrencyGeneratingTransactionHash() == null ? "" : token.getCurrencyGeneratingTransactionHash()).toString();
        this.currencyLastTypeChangingTransactionHash = (token.getCurrencyLastTypeChangingTransactionHash() == null ? "" : token.getCurrencyLastTypeChangingTransactionHash()).toString();
        this.confirmed = token.isConfirmed();

        this.currencyType = token.getCurrencyTypeData().getCurrencyType();
        this.currencyRateSourceType = token.getCurrencyTypeData().getCurrencyRateSourceType();
        this.rateSource = token.getCurrencyTypeData().getRateSource();
        this.protectionModel = token.getCurrencyTypeData().getProtectionModel();
    }
}