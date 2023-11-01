package io.coti.sdk.base;


import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.Instant;


@Data
public class FullNodeFeeResponseData implements IResponseData {

    private String hash;
    private String amount;
    private String originalAmount;
    private String addressHash;
    private String currencyHash;
    private String originalCurrencyHash;
    private Instant createTime;
    private String name;
    private SignatureData signatureData;

    public FullNodeFeeResponseData(FullNodeFeeData fullNodeFeeData) {
        this.hash = fullNodeFeeData.getHash().toString();
        this.amount = fullNodeFeeData.getAmount().toPlainString();
        this.originalAmount = fullNodeFeeData.getOriginalAmount().toPlainString();
        this.addressHash = fullNodeFeeData.getAddressHash().toString();
        this.currencyHash = fullNodeFeeData.getCurrencyHash() != null ? fullNodeFeeData.getCurrencyHash().toString() : null;
        this.originalCurrencyHash = fullNodeFeeData.getOriginalCurrencyHash() != null ? fullNodeFeeData.getOriginalCurrencyHash().toString() : null;
        this.createTime = fullNodeFeeData.getCreateTime();
        this.name = BaseTransactionName.getName(FullNodeFeeData.class).name();
        this.signatureData = fullNodeFeeData.getSignatureData();
    }
}
