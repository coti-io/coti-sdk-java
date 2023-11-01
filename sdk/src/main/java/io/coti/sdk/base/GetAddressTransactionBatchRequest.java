package io.coti.sdk.base;


import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import java.util.List;

@Data
public class GetAddressTransactionBatchRequest implements IRequest {

    @NotEmpty(message = "Addresses must not be blank")
    private List<@Valid Hash> addresses;
    @Valid
    private boolean extended;
    private boolean includeRuntimeTrustScore;
}
