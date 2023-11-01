package io.coti.sdk.base;


import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.annotation.JsonTypeIdResolver;
import lombok.Data;

import java.math.BigDecimal;
import java.time.Instant;

@Data
@JsonTypeInfo(use = JsonTypeInfo.Id.CUSTOM, property = "name")
@JsonTypeIdResolver(BaseTransactionResponseDataResolver.class)
public abstract class BaseTransactionResponseData implements IResponseData {

    private String hash;
    private String addressHash;
    private String currencyHash;
    private BigDecimal amount;
    private Instant createTime;
    private String name;

    protected BaseTransactionResponseData() {
    }

    protected BaseTransactionResponseData(BaseTransactionData baseTransactionData) {
        this.hash = baseTransactionData.getHash().toString();
        this.addressHash = baseTransactionData.getAddressHash().toString();
        this.currencyHash = baseTransactionData.getCurrencyHash() != null ? baseTransactionData.getCurrencyHash().toString() : null;
        this.amount = baseTransactionData.getAmount();
        this.createTime = baseTransactionData.getCreateTime();
        this.name = BaseTransactionName.getName(baseTransactionData.getClass()).name();

    }
}
