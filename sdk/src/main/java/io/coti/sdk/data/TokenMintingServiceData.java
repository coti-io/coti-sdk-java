package io.coti.sdk.data;

import io.coti.basenode.data.Hash;
import io.coti.basenode.data.SignatureData;
import io.coti.basenode.data.interfaces.ISignValidatable;
import io.coti.basenode.data.interfaces.ITokenServiceData;
import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.Instant;

@Data
public class TokenMintingServiceData implements ITokenServiceData, ISignValidatable {

    @NotNull
    private Hash mintingCurrencyHash;
    @NotNull
    private BigDecimal mintingAmount;
    @NotNull
    private @Valid Hash receiverAddress;
    @NotNull
    private Instant createTime;
    private BigDecimal feeAmount;
    @NotNull
    private @Valid Hash signerHash;
    @NotNull
    private @Valid SignatureData signature;

    public TokenMintingServiceData() {
    }

    @Override
    public byte[] getMessageInBytes() {
        return signature.getR().concat(signature.getS()).getBytes(StandardCharsets.UTF_8);
    }
}