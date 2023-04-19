package io.coti.sdk;

import io.coti.basenode.data.Hash;
import io.coti.basenode.exceptions.BalanceException;
import io.coti.basenode.http.CustomHttpComponentsClientHttpRequestFactory;
import io.coti.basenode.http.GetBalancesRequest;
import io.coti.sdk.http.GetAccountBalanceResponse;
import io.coti.sdk.utils.Constants;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AccountBalance {

    private AccountBalance() {
    }

    public static BigDecimal getAccountBalance(Hash address, String fullNodeAddress) {
        RestTemplate restTemplate = new RestTemplate(new CustomHttpComponentsClientHttpRequestFactory());
        GetBalancesRequest getBalance = new GetBalancesRequest();
        List<Hash> addresses = new ArrayList<>(Collections.singletonList(address));
        getBalance.setAddresses(addresses);

        GetAccountBalanceResponse getBalancesResponse;

        getBalancesResponse = restTemplate.postForObject(fullNodeAddress + Constants.BALANCE, getBalance, GetAccountBalanceResponse.class);
        if (getBalancesResponse == null || getBalancesResponse.getStatus().equals(Constants.ERROR)) {
            throw new BalanceException("Receiving Account balance failed!");
        }

        return validateAndReturnResponse(address, getBalancesResponse);
    }

    private static BigDecimal validateAndReturnResponse(Hash address, GetAccountBalanceResponse getBalancesResponse) {
        if (getBalancesResponse != null && getBalancesResponse.getAddressesBalance() != null &&
                getBalancesResponse.getAddressesBalance().get(address.toString()) != null) {
            return getBalancesResponse.getAddressesBalance().get(address.toString()).getAddressBalance();
        }
        return new BigDecimal(Constants.ZERO);
    }
}
