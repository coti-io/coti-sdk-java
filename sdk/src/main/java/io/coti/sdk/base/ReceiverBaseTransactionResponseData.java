package io.coti.sdk.base;


import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class ReceiverBaseTransactionResponseData extends OutputBaseTransactionResponseData {

    private String receiverDescription;

    public ReceiverBaseTransactionResponseData() {
        super();
    }

    public ReceiverBaseTransactionResponseData(BaseTransactionData baseTransactionData) {
        super(baseTransactionData);

        ReceiverBaseTransactionData receiverBaseTransactionData = (ReceiverBaseTransactionData) baseTransactionData;
        this.receiverDescription = receiverBaseTransactionData.getReceiverDescription() != null ? receiverBaseTransactionData.getReceiverDescription().toString() : null;
    }
}