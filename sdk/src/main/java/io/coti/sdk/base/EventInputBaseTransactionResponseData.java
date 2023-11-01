package io.coti.sdk.base;


import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class EventInputBaseTransactionResponseData extends BaseTransactionResponseData {

    protected String event;
    protected boolean hardFork;

    public EventInputBaseTransactionResponseData() {
        super();
    }

    public EventInputBaseTransactionResponseData(BaseTransactionData baseTransactionData) {
        super(baseTransactionData);
        EventInputBaseTransactionData eventInputBaseTransactionData = (EventInputBaseTransactionData) baseTransactionData;
        this.event = eventInputBaseTransactionData.getEvent().name();
        this.hardFork = eventInputBaseTransactionData.getEvent().isHardFork();
    }
}
