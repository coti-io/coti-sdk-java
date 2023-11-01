package io.coti.sdk.base;


import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class AddressRequest implements IRequest {
    @NotNull(message = "Address Hash must not be blank")
    private Hash address;
}
