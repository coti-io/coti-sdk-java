package io.coti.sdk.data.interfaces;


import io.coti.sdk.base.FullNodeFeeData;

@FunctionalInterface
public interface ToFullNodeFeeData {
    FullNodeFeeData toFullNodeFeeData();
}
