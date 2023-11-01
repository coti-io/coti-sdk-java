package io.coti.sdk.base;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
public class RollingReserveResponseData extends OutputBaseTransactionResponseData {

    private BigDecimal reducedAmount;
    private List<TrustScoreNodeResultResponseData> rollingReserveTrustScoreNodeResult;

    public RollingReserveResponseData() {
        super();
    }

    public RollingReserveResponseData(BaseTransactionData baseTransactionData) {
        super(baseTransactionData);

        RollingReserveData rollingReserveData = (RollingReserveData) baseTransactionData;
        this.reducedAmount = rollingReserveData.getReducedAmount();
        this.rollingReserveTrustScoreNodeResult = new ArrayList<>();

        for (TrustScoreNodeResultData trustScoreNodeResultData : rollingReserveData.getTrustScoreNodeResult()) {
            rollingReserveTrustScoreNodeResult.add(new TrustScoreNodeResultResponseData(trustScoreNodeResultData));
        }
    }
}
