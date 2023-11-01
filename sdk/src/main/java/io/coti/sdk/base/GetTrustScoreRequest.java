package io.coti.sdk.base;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class GetTrustScoreRequest implements IRequest {

    @NotNull
    private Hash userHash;
}
