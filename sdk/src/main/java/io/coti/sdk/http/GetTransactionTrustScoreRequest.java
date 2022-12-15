package io.coti.sdk.http;

import io.coti.basenode.data.Hash;
import io.coti.basenode.data.SignatureData;
import io.coti.basenode.data.interfaces.ISignValidatable;
import lombok.Data;
import lombok.NonNull;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Data
public class GetTransactionTrustScoreRequest implements ISignValidatable, Serializable {

    @NotNull
    @NonNull
    private Hash userHash;
    @NotNull
    @NonNull
    private Hash transactionHash;
    @NotNull
    @NonNull
    private SignatureData userSignature;

    @Override
    public SignatureData getSignature() {
        return userSignature;
    }

    @Override
    public Hash getSignerHash() {
        return userHash;
    }
}
