package io.coti.sdk.http;

import io.coti.sdk.base.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

@Data
@EqualsAndHashCode(callSuper = true)
public class TokenMintingFeeRequest extends Request {

    @NotNull
    private @Valid TokenMintingServiceData tokenMintingServiceData;
    private @Valid MintingFeeQuoteData mintingFeeQuoteData;
}