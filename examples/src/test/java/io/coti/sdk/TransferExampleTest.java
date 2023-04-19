package io.coti.sdk;

import io.coti.basenode.crypto.CryptoHelper;
import io.coti.basenode.crypto.OriginatorCurrencyCrypto;
import io.coti.basenode.data.Hash;
import io.coti.basenode.data.NetworkData;
import io.coti.basenode.data.NetworkNodeData;
import io.coti.basenode.data.NodeType;
import io.coti.basenode.http.GetTransactionRequest;
import io.coti.basenode.http.GetTransactionResponse;
import io.coti.basenode.http.GetTransactionsResponse;
import io.coti.sdk.http.AddTransactionRequest;
import io.coti.sdk.utils.CryptoUtils;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.junit.jupiter.api.Test;

import java.io.InputStream;
import java.math.BigDecimal;
import java.util.Map;
import java.util.Random;

import static io.coti.sdk.utils.Constants.NATIVE_CURRENCY_SYMBOL;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class TransferExampleTest {

    public static void main(String[] args) throws Exception {
        TransferExampleTest transferExampleTest = new TransferExampleTest();
        transferExampleTest.transferTest();
    }

    @Test
    void transferTest() throws Exception {
        //Loading properties from the properties file
        PropertiesConfiguration config = new PropertiesConfiguration();
        InputStream transferInput = TransferExampleTest.class.getClassLoader().getResourceAsStream("transfer.properties");
        config.load(transferInput);

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
        Hash nativeCurrencyHash = OriginatorCurrencyCrypto.calculateHash(NATIVE_CURRENCY_SYMBOL);
        String userPrivateKey = CryptoUtils.getPrivateKeyFromSeed((new Hash(seed).getBytes())).toHexString();
        String userHash = CryptoHelper.getPublicKeyFromPrivateKey(userPrivateKey);

        int walletAddressIndex = config.getInt("source.address.index");
        String transactionDescription = config.getString("transaction.description");
        BigDecimal transactionAmount = config.getBigDecimal("transfer.amount");

        String trustScoreAddress;
        String fullNodeAddress;
        String nodeManagerAddress = config.getString("node.manager.address");
        //retrieving URLs from node manager only if the nodeManager URL exists in properties file
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

        } else {//using URLs from properties file
            trustScoreAddress = config.getString("trust.score.backend.address");
            fullNodeAddress = config.getString("full.node.backend.address");
        }

        boolean feeIncluded = config.getBoolean("fee.included");
        //creating Transaction
        TransactionCreation transactionCreation = new TransactionCreation(seed, userHash, trustScoreAddress, fullNodeAddress, walletAddressIndex, nativeCurrencyHash);
        AddTransactionRequest request = new AddTransactionRequest(transactionCreation.createTransferTransaction(transactionAmount, transactionDescription, receiverAddress, feeIncluded));
        //Sending Transaction
        Hash transactionHash = TransactionUtils.sendTransaction(request, fullNodeAddress);

        assertThat(transactionHash).isNotNull();
        assertThat(transactionIsPending(transactionHash, fullNodeAddress)).isFalse();

        //Checking Non-indexed transaction from the Full Node
        GetTransactionsResponse getTransactions = TransactionUtilities.getNoneIndexedTransactions(fullNodeAddress);
        assertThat(getTransactions).isNotNull();
        assertThat(getTransactions.getTransactionsData().toArray()).isEmpty();
    }

    //Checking Transaction status based on Transaction Data from the Full Node
    private boolean transactionIsPending(Hash transactionHash, String fullNodeAddress) {
        GetTransactionRequest transactionRequest = new GetTransactionRequest();
        transactionRequest.setTransactionHash(transactionHash);
        GetTransactionResponse response = TransactionUtilities.getTransaction(transactionRequest, fullNodeAddress);
        System.out.println("Transaction Details by hash " + transactionHash + " :");
        System.out.println("Transaction Type = " + response.getTransactionData().getType() + ";");
        System.out.println("Transaction Description = " + response.getTransactionData().getTransactionDescription() + ";");
        System.out.println("Transaction Amount = " + response.getTransactionData().getAmount() + ";");
        System.out.println("Transaction Index = " + response.getTransactionData().getIndex() + ";");
        System.out.println("Transaction Trust Chain Trust Score = " + response.getTransactionData().getTrustChainTrustScore() + ";");

        return response.getTransactionData().isTrustChainConsensus();
    }
}
