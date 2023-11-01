package io.coti.sdk.base;


import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
public class AddressBalanceData implements Serializable {

    private BigDecimal addressBalance;
    private BigDecimal addressPreBalance;

    public AddressBalanceData() {
    }

    public AddressBalanceData(BigDecimal addressBalance, BigDecimal addressPreBalance) {
        this.addressBalance = addressBalance;
        this.addressPreBalance = addressPreBalance;
    }
}
