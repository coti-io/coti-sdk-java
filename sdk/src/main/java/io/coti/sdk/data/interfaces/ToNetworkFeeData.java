package io.coti.sdk.data.interfaces;


import io.coti.sdk.base.NetworkFeeData;

@FunctionalInterface
public interface ToNetworkFeeData {
    NetworkFeeData toNetworkFeeData();
}
