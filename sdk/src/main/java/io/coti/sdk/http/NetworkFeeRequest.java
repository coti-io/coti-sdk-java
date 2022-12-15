package io.coti.sdk.http;

import io.coti.basenode.data.FullNodeFeeData;
import io.coti.basenode.data.Hash;
import lombok.Data;
import lombok.NonNull;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

@Data
public class NetworkFeeRequest {

    @NotNull
    @NonNull
    private @Valid Hash userHash;
    @NotNull
    @NonNull
    private @Valid FullNodeFeeData fullNodeFeeData;
    @NonNull
    private boolean feeIncluded;

}