package io.coti.sdk.base;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;

@Data
public class GetBalancesRequest implements IRequest {

    @NotNull(message = "Addresses must not be blank")
    private List<Hash> addresses;

}