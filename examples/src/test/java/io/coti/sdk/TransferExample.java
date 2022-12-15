package io.coti.sdk;

import io.coti.basenode.crypto.CryptoHelper;
import io.coti.basenode.crypto.NodeCryptoHelper;
import io.coti.basenode.crypto.OriginatorCurrencyCrypto;
import io.coti.basenode.data.Hash;
import io.coti.basenode.data.NetworkData;
import io.coti.basenode.data.NetworkNodeData;
import io.coti.basenode.data.NodeType;
import io.coti.basenode.http.CustomHttpComponentsClientHttpRequestFactory;
import io.coti.sdk.http.AddTransactionRequest;
import io.coti.sdk.http.AddTransactionResponse;
import io.coti.sdk.utils.CryptoUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.springframework.beans.factory.config.MethodInvokingBean;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Random;

@Slf4j
public class TransferExample {

    public static void main(String[] args) throws Exception {
        PropertiesConfiguration config = new PropertiesConfiguration();
        config.load("transfer.properties");

        String seed = config.getString("seed");
        if (seed.equals("")) {
            throw new Exception("seed needed");
        }
        Hash nativeCurrencyHash = OriginatorCurrencyCrypto.calculateHash("COTI");
        String userPrivateKey = CryptoUtils.getPrivateKeyFromSeed((new Hash(seed).getBytes())).toHexString();
        setNodePrivateKey(userPrivateKey);

        String userHash = CryptoHelper.getPublicKeyFromPrivateKey(userPrivateKey);

        int walletAddressIndex = config.getInt("source.address.index");
        String transactionDescription = config.getString("transaction.description");
        int transactionAmount = config.getInt("transfer.amount");

        String trustScoreAddress = null;
        String fullNodeAddress = null;
        String nodeManagerAddress = config.getString("node.manager.address");
        if (!(nodeManagerAddress == null || nodeManagerAddress.equals(""))) {
            NetworkDetails networkDetails = new NetworkDetails();
            NetworkData networkData = networkDetails.getNodesDetails(nodeManagerAddress);

            Map<Hash, NetworkNodeData> fullNodes = networkData.getMultipleNodeMaps().get(NodeType.FullNode);
            Random randomlySelectedFullNode = new Random();
            NetworkNodeData fullNode = (NetworkNodeData) fullNodes.values().toArray()[randomlySelectedFullNode.nextInt(fullNodes.size())];
            fullNodeAddress = fullNode.getWebServerUrl();

            Map<Hash, NetworkNodeData> trustNodes = networkData.getMultipleNodeMaps().get(NodeType.TrustScoreNode);
            Random randomlySelectedTrustNode = new Random();
            NetworkNodeData trustNode = (NetworkNodeData) trustNodes.values().toArray()[randomlySelectedTrustNode.nextInt(trustNodes.size())];
            trustScoreAddress = trustNode.getWebServerUrl();

        } else {
            trustScoreAddress = config.getString("trust.score.backend.address");
            fullNodeAddress = config.getString("full.node.backend.address");
        }

        TransactionCreation transactionCreation = new TransactionCreation(seed, userHash, trustScoreAddress, fullNodeAddress, walletAddressIndex, nativeCurrencyHash);
        AddTransactionRequest request = null;
        request = new AddTransactionRequest(transactionCreation.createTransactionData(new BigDecimal(transactionAmount), transactionDescription));
        sendTransaction(request, fullNodeAddress);
    }

    private static void setNodePrivateKey(String userPrivateKey) throws Exception {
        MethodInvokingBean methodInvokingBean = new MethodInvokingBean();
        methodInvokingBean.setStaticMethod(NodeCryptoHelper.class.getName() + ".nodePrivateKey");
        methodInvokingBean.setArguments(userPrivateKey);
        try {
            methodInvokingBean.afterPropertiesSet();
        } catch (Exception e) {
            throw e;
        }
    }

    public static void sendTransaction(AddTransactionRequest request, String fullNodeAddress) throws Exception {
        RestTemplate restTemplate = new RestTemplate(new CustomHttpComponentsClientHttpRequestFactory());
        AddTransactionResponse response = null;
        try {
            HttpEntity<AddTransactionRequest> entity = new HttpEntity<>(request);
            response = restTemplate.exchange(fullNodeAddress + "/transaction", HttpMethod.PUT, entity, AddTransactionResponse.class).getBody();
        } catch (Exception e) {
            throw e;
        }
        if (response != null && response.getStatus().equals("Success")) {
            log.info("####################################################################");
            log.info("#################      {}      ####################", response.getMessage());
            log.info("# {} #", request.getHash());
            log.info("####################################################################");
        } else {
            throw new Exception("Adding new Transaction failed!");
        }
    }
}
