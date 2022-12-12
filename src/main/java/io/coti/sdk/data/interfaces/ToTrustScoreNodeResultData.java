package io.coti.sdk.data.interfaces;

import io.coti.basenode.data.TrustScoreNodeResultData;

@FunctionalInterface
public interface ToTrustScoreNodeResultData {
    TrustScoreNodeResultData toTrustScoreNodeResultData();
}
