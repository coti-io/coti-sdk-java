package io.coti.sdk.data.interfaces;

import io.coti.sdk.base.TrustScoreNodeResultData;

@FunctionalInterface
public interface ToTrustScoreNodeResultData {
    TrustScoreNodeResultData toTrustScoreNodeResultData();
}
