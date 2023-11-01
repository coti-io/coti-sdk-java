package io.coti.sdk.data;

import io.coti.sdk.base.*;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class UpdatedBalanceMessage {

    private String addressHash;
    private String currencyHash;
    private BigDecimal balance;
    private BigDecimal preBalance;
    private String message;

    public UpdatedBalanceMessage() {
    }

    public UpdatedBalanceMessage(Hash addressHash, Hash currencyHash, BigDecimal balance, BigDecimal preBalance) {
        this.addressHash = addressHash.toHexString();
        this.currencyHash = currencyHash.toHexString();
        this.balance = balance;
        this.preBalance = preBalance;
        this.message = "Balance Updated!";
    }
}
