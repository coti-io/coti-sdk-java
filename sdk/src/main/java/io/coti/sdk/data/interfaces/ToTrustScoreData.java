package io.coti.sdk.data.interfaces;

import io.coti.basenode.data.TransactionTrustScoreData;

@FunctionalInterface
public interface ToTrustScoreData {
    TransactionTrustScoreData toTrustScoreData();
}
