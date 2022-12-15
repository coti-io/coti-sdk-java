package io.coti.sdk.http;

import io.coti.basenode.data.FullNodeFeeData;
import io.coti.basenode.data.Hash;
import io.coti.basenode.data.NetworkFeeData;
import lombok.Data;
import lombok.NonNull;

import javax.validation.constraints.NotNull;

@Data
public class NetworkFeeValidateRequest {

    @NotNull
    @NonNull
    private FullNodeFeeData fullNodeFeeData;
    @NotNull
    @NonNull
    private NetworkFeeData networkFeeData;
    @NotNull
    @NonNull
    private Hash userHash;
    @NonNull
    private boolean feeIncluded;
}
