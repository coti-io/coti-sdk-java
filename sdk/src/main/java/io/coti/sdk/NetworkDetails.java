package io.coti.sdk;

import io.coti.sdk.base.NetworkData;
import io.coti.sdk.utils.Constants;
import org.springframework.http.ResponseEntity;

public class NetworkDetails {

    public NetworkData getNodesDetails(String nodeManagerHttpAddress) {
        ResponseEntity<NetworkData> networkData = (ResponseEntity<NetworkData>) Utilities.getRequest(nodeManagerHttpAddress + Constants.NODES, NetworkData.class);
        return networkData.getBody();
    }
}
