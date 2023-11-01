package io.coti.sdk.base;


import lombok.Data;

import java.math.BigDecimal;

@Data
public class TokenGenerationServiceResponseData implements ITransactionResponseData {

    private OriginatorCurrencyResponseData originatorCurrencyData;
    private CurrencyTypeResponseData currencyTypeData;
    private BigDecimal feeAmount;

    public TokenGenerationServiceResponseData() {
    }

    public TokenGenerationServiceResponseData(BaseTransactionData baseTransactionData) {
        originatorCurrencyData = new OriginatorCurrencyResponseData(((TokenGenerationFeeBaseTransactionData) baseTransactionData).getServiceData().getOriginatorCurrencyData());
        currencyTypeData = new CurrencyTypeResponseData(((TokenGenerationFeeBaseTransactionData) baseTransactionData).getServiceData().getCurrencyTypeData());
        this.feeAmount = ((TokenGenerationFeeBaseTransactionData) baseTransactionData).getServiceData().getFeeAmount();
    }
}
