package io.coti.sdk.base;


import java.util.List;

public interface ITrustScoreNodeValidatable {
    List<TrustScoreNodeResultData> getTrustScoreNodeResult();

    void setTrustScoreNodeResult(List<TrustScoreNodeResultData> trustScoreNodeResult);
}
