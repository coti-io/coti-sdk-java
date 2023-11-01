package io.coti.sdk.base;


import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
public class PaymentInputBaseTransactionResponseData extends InputBaseTransactionResponseData {

    private List<PaymentItemData> items;
    private String encryptedMerchantName;

    public PaymentInputBaseTransactionResponseData(BaseTransactionData baseTransactionData) {
        super(baseTransactionData);
        PaymentInputBaseTransactionData paymentInputBaseTransactionData = (PaymentInputBaseTransactionData) baseTransactionData;
        this.items = paymentInputBaseTransactionData.getItems();
        this.encryptedMerchantName = paymentInputBaseTransactionData.getEncryptedMerchantName();

    }
}
