package io.coti.sdk.http;

import io.coti.sdk.base.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class AddAddressResponse extends BaseResponse {

    private String address;
    private AddressStatus addressStatus;

    public AddAddressResponse() {
    }

    public AddAddressResponse(String address, AddressStatus addressStatus) {
        this.address = address;
        this.addressStatus = addressStatus;
    }
}
