package io.coti.sdk.http;

import io.coti.sdk.base.*;
import io.coti.sdk.base.SignatureData;
import lombok.Data;
import lombok.NonNull;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.io.Serializable;
import java.math.BigDecimal;

@Data
public class FullNodeFeeRequest implements ISignValidatable, Serializable {

    @NonNull
    private boolean feeIncluded;
    @NonNull
    private @Valid Hash originalCurrencyHash;
    @Positive
    @NonNull
    private BigDecimal originalAmount;
    @NotNull
    @NonNull
    private @Valid Hash userHash;
    @NotNull
    @NonNull
    private @Valid SignatureData userSignature;

    @Override
    public SignatureData getSignature() {
        return userSignature;
    }

    @Override
    public Hash getSignerHash() {
        return userHash;
    }
}