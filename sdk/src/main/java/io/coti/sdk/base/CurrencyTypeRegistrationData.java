package io.coti.sdk.base;



import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class CurrencyTypeRegistrationData extends CurrencyTypeData implements ISignable, ISignValidatable {

    private String symbol;

    public CurrencyTypeRegistrationData(String symbol, CurrencyTypeData currencyTypeData) {
        super(currencyTypeData);
        this.symbol = symbol;
    }
}
