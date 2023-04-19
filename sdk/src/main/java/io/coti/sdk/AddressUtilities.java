package io.coti.sdk;

import io.coti.basenode.data.Hash;
import io.coti.basenode.exceptions.CotiRunTimeException;
import io.coti.basenode.http.AddressBulkRequest;
import io.coti.basenode.http.AddressRequest;
import io.coti.basenode.http.AddressesExistsResponse;
import io.coti.basenode.http.CustomHttpComponentsClientHttpRequestFactory;
import io.coti.sdk.http.AddAddressResponse;
import io.coti.sdk.utils.Constants;
import lombok.experimental.UtilityClass;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@UtilityClass
public class AddressUtilities {

    private static final RestTemplate restTemplate = new RestTemplate(new CustomHttpComponentsClientHttpRequestFactory());

    public AddAddressResponse addAddressToNode(Hash address, String fullNodeUrl) {
        AddressRequest addressRequest = new AddressRequest();
        addressRequest.setAddress(address);

        HttpEntity<AddressRequest> entity = new HttpEntity<>(addressRequest);
        AddAddressResponse addressResponse = restTemplate.exchange(fullNodeUrl + Constants.ADDRESS, HttpMethod.PUT, entity, AddAddressResponse.class).getBody();
        if (addressResponse == null || addressResponse.getStatus().equals(Constants.ERROR)) {
            throw new CotiRunTimeException("Transaction TrustScore Data call failed!");
        }

        return addressResponse;
    }

    public AddressesExistsResponse checkAddressExists(List<Hash> addresses, String fullNodeUrl) {
        AddressBulkRequest addressBulkRequest = new AddressBulkRequest();
        addressBulkRequest.setAddresses(addresses);

        AddressesExistsResponse addressesResponse = restTemplate.postForObject(fullNodeUrl + Constants.ADDRESS, addressBulkRequest, AddressesExistsResponse.class);
        if (addressesResponse == null || addressesResponse.getStatus().equals(Constants.ERROR)) {
            throw new CotiRunTimeException("Transaction TrustScore Data call failed!");
        }

        return addressesResponse;
    }
}
