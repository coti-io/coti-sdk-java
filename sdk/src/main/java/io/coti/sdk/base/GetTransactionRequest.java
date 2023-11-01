package io.coti.sdk.base;


import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

@Data
public class GetTransactionRequest implements IRequest {

    @NotNull(message = "Transaction hash must not be blank")
    private @Valid Hash transactionHash;
}
