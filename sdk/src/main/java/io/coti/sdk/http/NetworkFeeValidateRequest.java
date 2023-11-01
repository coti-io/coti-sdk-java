package io.coti.sdk.http;


import io.coti.sdk.base.*;
import io.coti.sdk.base.NetworkFeeData;
import lombok.Data;
import lombok.NonNull;

import javax.validation.constraints.NotNull;

@Data
public class NetworkFeeValidateRequest implements IRequest {

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
