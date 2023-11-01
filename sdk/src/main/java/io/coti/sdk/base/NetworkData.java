package io.coti.sdk.base;


import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.EnumMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Data
public class NetworkData implements IPropagatable, ISignable, ISignValidatable {

    private static final long serialVersionUID = 1145085728326369679L;
    private Map<NodeType, Map<Hash, NetworkNodeData>> multipleNodeMaps;
    private Map<NodeType, NetworkNodeData> singleNodeNetworkDataMap;
    private Hash signerHash;
    private SignatureData signature;

    public NetworkData() {
        multipleNodeMaps = new EnumMap<>(NodeType.class);
        NodeTypeService.getNodeTypeList(true).forEach(nodeType -> multipleNodeMaps.put(nodeType, new ConcurrentHashMap<>()));

        singleNodeNetworkDataMap = new EnumMap<>(NodeType.class);
        NodeTypeService.getNodeTypeList(false).forEach(nodeType -> singleNodeNetworkDataMap.put(nodeType, null));

    }

    @Override
    public Hash getHash() {
        return new Hash(this.hashCode());
    }

    @Override
    public void setHash(Hash hash) {
        // no implementation
    }

    @JsonInclude()
    public Map<NodeType, NetworkNodeData> getSingleNodeNetworkDataMap() {
        return singleNodeNetworkDataMap;
    }
}