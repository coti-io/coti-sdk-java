package io.coti.sdk.data.interfaces;

import io.coti.basenode.data.FullNodeFeeData;

@FunctionalInterface
public interface ToFullNodeFeeData {
    FullNodeFeeData toFullNodeFeeData();
}
