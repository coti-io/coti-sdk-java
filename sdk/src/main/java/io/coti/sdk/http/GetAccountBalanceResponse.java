package io.coti.sdk.http;

import io.coti.sdk.data.AddressBalanceData;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.HashMap;
import java.util.Map;

@Data
@EqualsAndHashCode(callSuper = true)
public class GetAccountBalanceResponse extends BaseResponse {

    private Map<String, AddressBalanceData> addressesBalance;

    public GetAccountBalanceResponse() {
        addressesBalance = new HashMap<>();
    }

}
