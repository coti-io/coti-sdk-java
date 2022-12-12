package io.coti.sdk.data.interfaces;

import io.coti.basenode.data.NetworkFeeData;

@FunctionalInterface
public interface ToNetworkFeeData {
    NetworkFeeData toNetworkFeeData();
}
