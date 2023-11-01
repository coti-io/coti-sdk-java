package io.coti.sdk.base;


import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

@Data
public class AddressBulkRequest implements Serializable {

    @NotNull(message = "Address Hashes must not be blank")
    public List<@Valid Hash> addresses;

}
