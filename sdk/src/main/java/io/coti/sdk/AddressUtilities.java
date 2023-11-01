package io.coti.sdk;


import io.coti.sdk.base.*;
import io.coti.sdk.http.AddAddressResponse;
import io.coti.sdk.http.GetAccountBalanceResponse;
import io.coti.sdk.utils.Constants;
import lombok.experimental.UtilityClass;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@UtilityClass
public class AddressUtilities {

    public AddAddressResponse addAddressToNode(Hash address, String fullNodeUrl) {
        AddressRequest addressRequest = new AddressRequest();
        addressRequest.setAddress(address);
        HttpEntity<AddressRequest> entity = new HttpEntity<>(addressRequest);
        ResponseEntity<AddAddressResponse> addressResponse = (ResponseEntity<AddAddressResponse>) Utilities.putRequest(fullNodeUrl + Constants.ADDRESS, entity, AddAddressResponse.class);
        return addressResponse.getBody();
    }

    public AddressesExistsResponse checkAddressExists(List<Hash> addresses, String fullNodeUrl) {
        AddressBulkRequest addressBulkRequest = new AddressBulkRequest();
        addressBulkRequest.setAddresses(addresses);
        ResponseEntity<AddressesExistsResponse> addressesResponse = (ResponseEntity<AddressesExistsResponse>) Utilities.postRequest(fullNodeUrl + Constants.ADDRESS, addressBulkRequest, AddressesExistsResponse.class);
        return addressesResponse.getBody();
    }

    public static BigDecimal getAddressBalance(Hash address, String fullNodeAddress) {
        GetBalancesRequest getBalance = new GetBalancesRequest();
        List<Hash> addresses = new ArrayList<>(Collections.singletonList(address));
        getBalance.setAddresses(addresses);
        ResponseEntity<GetAccountBalanceResponse> getBalancesResponse = (ResponseEntity<GetAccountBalanceResponse>) Utilities.postRequest(fullNodeAddress + Constants.BALANCE, getBalance, GetAccountBalanceResponse.class);
        return validateAndReturnResponse(address, getBalancesResponse.getBody());
    }

    private static BigDecimal validateAndReturnResponse(Hash address, GetAccountBalanceResponse getBalancesResponse) {
        if (getBalancesResponse != null && getBalancesResponse.getAddressesBalance() != null &&
                getBalancesResponse.getAddressesBalance().get(address.toString()) != null) {
            return getBalancesResponse.getAddressesBalance().get(address.toString()).getAddressBalance();
        }
        return new BigDecimal(Constants.ZERO);
    }
}
