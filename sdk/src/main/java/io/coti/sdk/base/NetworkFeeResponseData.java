package io.coti.sdk.base;


import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
public class NetworkFeeResponseData extends OutputBaseTransactionResponseData {

    private BigDecimal reducedAmount;
    private List<TrustScoreNodeResultResponseData> networkFeeTrustScoreNodeResult;

    public NetworkFeeResponseData() {
        super();
    }

    public NetworkFeeResponseData(BaseTransactionData baseTransactionData) {
        super(baseTransactionData);

        NetworkFeeData networkFeeData = (NetworkFeeData) baseTransactionData;
        this.reducedAmount = networkFeeData.getReducedAmount();
        this.networkFeeTrustScoreNodeResult = new ArrayList<>();

        for (TrustScoreNodeResultData trustScoreNodeResultData : networkFeeData.getTrustScoreNodeResult()) {
            networkFeeTrustScoreNodeResult.add(new TrustScoreNodeResultResponseData(trustScoreNodeResultData));
        }

    }
}
