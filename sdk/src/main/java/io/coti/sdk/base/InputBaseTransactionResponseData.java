package io.coti.sdk.base;


import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class InputBaseTransactionResponseData extends BaseTransactionResponseData {

    protected InputBaseTransactionResponseData() {
    }

    public InputBaseTransactionResponseData(BaseTransactionData baseTransactionData) {
        super(baseTransactionData);
    }
}
