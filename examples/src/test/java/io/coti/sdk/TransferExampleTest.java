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
import org.apache.commons.configuration.PropertiesConfiguration;
import org.junit.Test;
import org.springframework.beans.factory.config.MethodInvokingBean;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Random;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class TransferExampleTest {

    private static void setNodePrivateKey(String userPrivateKey) throws Exception {
        MethodInvokingBean methodInvokingBean = new MethodInvokingBean();
        methodInvokingBean.setStaticMethod(NodeCryptoHelper.class.getName() + ".nodePrivateKey");
        methodInvokingBean.setArguments(userPrivateKey);
        methodInvokingBean.afterPropertiesSet();
    }

    public static Hash sendTransaction(AddTransactionRequest request, String fullNodeAddress)  {
        RestTemplate restTemplate = new RestTemplate(new CustomHttpComponentsClientHttpRequestFactory());
        HttpEntity<AddTransactionRequest> entity = new HttpEntity<>(request);
        AddTransactionResponse response = restTemplate.exchange(fullNodeAddress + "/transaction", HttpMethod.PUT, entity, AddTransactionResponse.class).getBody();

        if (response != null && response.getStatus().equals("Success")) {
            System.out.println("####################################################################");
            System.out.println("#################      " + response.getMessage());
            System.out.println("# " + request.getHash() + " #");
            System.out.println("####################################################################");
            return request.getHash();
        }
        return null;
    }

    public static void main(String[] args) throws Exception {
        TransferExampleTest transferExampleTest = new TransferExampleTest();
        transferExampleTest.transferTest();
    }

    @Test
    public void transferTest() throws Exception {
        PropertiesConfiguration config = new PropertiesConfiguration();
        config.load("src/test/resources/transfer.properties");

        String seed = config.getString("seed");
        if (seed == null || seed.equals("")) {
            seed = System.getenv("TESTNET_SEED");
            if (seed == null || seed.equals("")) {
                throw new Exception("seed needed");
            }
        }
        String receiverAddressString = config.getString("receiver.address");
        if (receiverAddressString == null || receiverAddressString.equals("")) {
            receiverAddressString = System.getenv("TESTNET_ADDRESS");
            if (receiverAddressString == null || receiverAddressString.equals("")) {
                throw new Exception("receiver address needed");
            }
        }
        Hash receiverAddress = new Hash(receiverAddressString);

        Hash nativeCurrencyHash = OriginatorCurrencyCrypto.calculateHash("COTI");
        String userPrivateKey = CryptoUtils.getPrivateKeyFromSeed((new Hash(seed).getBytes())).toHexString();
        setNodePrivateKey(userPrivateKey);

        String userHash = CryptoHelper.getPublicKeyFromPrivateKey(userPrivateKey);

        int walletAddressIndex = config.getInt("source.address.index");
        String transactionDescription = config.getString("transaction.description");
        int transactionAmount = config.getInt("transfer.amount");

        String trustScoreAddress;
        String fullNodeAddress;
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

        boolean feeIncluded = config.getBoolean("fee.included");
        TransactionCreation transactionCreation = new TransactionCreation(seed, userHash, trustScoreAddress, fullNodeAddress, walletAddressIndex, nativeCurrencyHash);
        AddTransactionRequest request = new AddTransactionRequest(transactionCreation.createTransferTransaction(new BigDecimal(transactionAmount), transactionDescription, receiverAddress, feeIncluded));
        Hash transactionTx = sendTransaction(request, fullNodeAddress);

        assertThat(transactionTx).isNotNull();
    }
}
