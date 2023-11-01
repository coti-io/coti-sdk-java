package io.coti.sdk.data.interfaces;


import io.coti.sdk.base.TransactionTrustScoreData;

@FunctionalInterface
public interface ToTrustScoreData {
    TransactionTrustScoreData toTrustScoreData();
}
