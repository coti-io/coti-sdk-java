package io.coti.sdk.http;

import io.coti.basenode.data.MintingFeeQuoteData;
import io.coti.basenode.http.Request;
import io.coti.sdk.data.TokenMintingServiceData;
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