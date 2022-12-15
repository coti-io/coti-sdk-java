package io.coti.sdk;

import com.google.gson.Gson;
import io.coti.basenode.data.NetworkData;
import io.coti.basenode.exceptions.NetworkException;
import io.coti.basenode.http.CustomHttpComponentsClientHttpRequestFactory;
import io.coti.basenode.http.Response;
import io.coti.sdk.utils.Constants;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

public class NetworkDetails {

    public NetworkData getNodesDetails(String nodeManagerHttpAddress) {
        RestTemplate restTemplate = new RestTemplate(new CustomHttpComponentsClientHttpRequestFactory());

        NetworkData networkData;
        try {
            networkData = restTemplate.getForObject(nodeManagerHttpAddress + Constants.NODES, NetworkData.class);
        } catch (HttpStatusCodeException e) {
            throw new NetworkException("Error at getting network details. Node manager error: " + new Gson().fromJson(e.getResponseBodyAsString(), Response.class));
        } catch (Exception e) {
            throw e;
        }
        if (networkData == null) {
            throw new NetworkException("Null network from node manager");
        }

        return networkData;

    }


}
