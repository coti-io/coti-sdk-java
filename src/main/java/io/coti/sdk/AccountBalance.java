package io.coti.sdk;

import io.coti.basenode.data.Hash;
import io.coti.basenode.http.CustomHttpComponentsClientHttpRequestFactory;
import io.coti.basenode.http.GetBalancesRequest;
import io.coti.sdk.http.GetAccountBalanceResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Slf4j
@Service
public class AccountBalance {

    private static final String BALANCE = "/balance";
    private static final int ZERO = 0;

    public BigDecimal getAccountBalance(Hash address, String fullNodeAddress) {
        RestTemplate restTemplate = new RestTemplate(new CustomHttpComponentsClientHttpRequestFactory());
        GetBalancesRequest getBalance = new GetBalancesRequest();
        List<Hash> addresses = new ArrayList<>(Collections.singletonList(address));
        getBalance.setAddresses(addresses);

        GetAccountBalanceResponse getBalancesResponse = null;
        try {
            getBalancesResponse = restTemplate.postForObject(fullNodeAddress + BALANCE, getBalance, GetAccountBalanceResponse.class);
        } catch (Exception e) {
            log.error("Exception for getting account balance: ", e);
        }
        return validateAndReturnResponse(address, getBalancesResponse);
    }

    private static BigDecimal validateAndReturnResponse(Hash address, GetAccountBalanceResponse getBalancesResponse) {
        if (getBalancesResponse != null && getBalancesResponse.getAddressesBalance() != null &&
                getBalancesResponse.getAddressesBalance().get(address.toString()) != null) {
            return getBalancesResponse.getAddressesBalance().get(address.toString()).getAddressBalance();
        }
        return new BigDecimal(ZERO);
    }
}
