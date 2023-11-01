package io.coti.sdk.base;


import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class FullNodeFeeResponseHttpData extends OutputBaseTransactionResponseData {

    public FullNodeFeeResponseHttpData() {
        super();
    }

    public FullNodeFeeResponseHttpData(BaseTransactionData baseTransactionData) {
        super(baseTransactionData);
    }
}
