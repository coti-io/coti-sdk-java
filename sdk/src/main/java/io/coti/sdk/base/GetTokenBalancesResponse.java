package io.coti.sdk.base;


import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Map;

@Data
@EqualsAndHashCode(callSuper = true)
public class GetTokenBalancesResponse extends BaseResponse {

    private Map<Hash, Map<Hash, AddressBalanceData>> tokenBalances;

    public GetTokenBalancesResponse() {
        super();
    }

    public GetTokenBalancesResponse(Map<Hash, Map<Hash, AddressBalanceData>> tokenBalances) {
        this.tokenBalances = tokenBalances;
    }
}