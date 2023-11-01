package io.coti.sdk.base;


import lombok.Data;

import java.math.BigDecimal;

@Data
public class OriginatorCurrencyResponseData implements ITransactionResponseData {

    protected String name;
    protected String symbol;
    protected BigDecimal totalSupply;
    protected int scale;
    protected String originatorHash;
    protected SignatureData originatorSignature;
    private String description;

    public OriginatorCurrencyResponseData() {
    }

    public OriginatorCurrencyResponseData(OriginatorCurrencyData originatorCurrencyData) {
        this.setName(originatorCurrencyData.getName());
        this.setSymbol(originatorCurrencyData.getSymbol());
        this.setTotalSupply(originatorCurrencyData.getTotalSupply());
        this.setScale(originatorCurrencyData.getScale());
        this.setDescription(originatorCurrencyData.getDescription());
        this.setOriginatorSignature(originatorCurrencyData.getOriginatorSignature());
        this.setOriginatorHash(originatorCurrencyData.getOriginatorHash().toString());
    }
}
