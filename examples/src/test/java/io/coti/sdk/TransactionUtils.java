package io.coti.sdk;

import io.coti.basenode.data.Hash;
import io.coti.basenode.http.CustomHttpComponentsClientHttpRequestFactory;
import io.coti.sdk.http.AddTransactionRequest;
import io.coti.sdk.http.AddTransactionResponse;
import lombok.experimental.UtilityClass;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.web.client.RestTemplate;

@UtilityClass
public class TransactionUtils {

    public Hash sendTransaction(AddTransactionRequest request, String fullNodeAddress) {
        RestTemplate restTemplate = new RestTemplate(new CustomHttpComponentsClientHttpRequestFactory());
        HttpEntity<AddTransactionRequest> entity = new HttpEntity<>(request);
        AddTransactionResponse response = restTemplate.exchange(fullNodeAddress + "/transaction", HttpMethod.PUT, entity, AddTransactionResponse.class).getBody();

        if (response != null && response.getStatus().equals("Success")) {
            System.out.println("####################################################################");
            System.out.println("##################      " + response.getMessage() + "      ###################");
            System.out.println("# " + request.getHash() + " #");
            System.out.println("####################################################################");
            return request.getHash();
        }
        return null;
    }
}
