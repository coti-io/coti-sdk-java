package io.coti.sdk;

import io.coti.basenode.crypto.CryptoHelper;
import io.coti.basenode.crypto.OriginatorCurrencyCrypto;
import io.coti.basenode.data.*;
import io.coti.basenode.http.GenerateTokenFeeRequest;
import io.coti.basenode.http.GetTransactionResponse;
import io.coti.sdk.http.AddTransactionRequest;
import io.coti.sdk.http.FullNodeFeeResponse;
import io.coti.sdk.utils.CryptoUtils;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.junit.jupiter.api.Test;

import java.io.InputStream;
import java.math.BigDecimal;

import static io.coti.sdk.utils.Constants.NATIVE_CURRENCY_SYMBOL;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class TokenGenerationExampleTest {

    public static void main(String[] args) throws Exception {
        TokenGenerationExampleTest tokenGenerationTest = new TokenGenerationExampleTest();
        tokenGenerationTest.tokenGenerationTest();
    }

    @Test
    void tokenGenerationTest() throws Exception {
        //Loading properties from the properties file
        PropertiesConfiguration config = new PropertiesConfiguration();
        InputStream transferInput = TokenGenerationExampleTest.class.getClassLoader().getResourceAsStream("transfer.properties");
        config.load(transferInput);
        PropertiesConfiguration tokenConfig = new PropertiesConfiguration();
        InputStream tokenInput = TokenGenerationExampleTest.class.getClassLoader().getResourceAsStream("token.properties");
        tokenConfig.load(tokenInput);
        String seed = config.getString("seed");
        if (seed == null || seed.equals("")) {
            seed = System.getenv("TESTNET_SEED");
            if (seed == null || seed.equals("")) {
                throw new Exception("seed needed");
            }
        }
        String userPrivateKey = CryptoUtils.getPrivateKeyFromSeed((new Hash(seed).getBytes())).toHexString();
        String userHash = CryptoHelper.getPublicKeyFromPrivateKey(userPrivateKey);
        Hash nativeCurrencyHash = OriginatorCurrencyCrypto.calculateHash(NATIVE_CURRENCY_SYMBOL);

        String tokenName = tokenConfig.getString("name");
        String tokenSymbol = tokenConfig.getString("symbol");
        String tokenDescription = tokenConfig.getString("description");
        BigDecimal totalSupply = tokenConfig.getBigDecimal("totalSupply");
        int scale = tokenConfig.getInt("scale");
        OriginatorCurrencyData originatorCurrencyData = TokenData.getOriginatorCurrencyData(userPrivateKey, userHash, tokenName, tokenSymbol, tokenDescription, totalSupply, scale);

        String rateSource = tokenConfig.getString("rateSource");
        String protectionModel = tokenConfig.getString("protectionModel");
        CurrencyTypeRegistrationData currencyTypeRegistrationData = TokenData.getCurrencyTypeRegistrationData(userPrivateKey, userHash, tokenSymbol, rateSource, protectionModel);

        GenerateTokenFeeRequest generateTokenFeeRequest = new GenerateTokenFeeRequest();
        generateTokenFeeRequest.setCurrencyTypeData(currencyTypeRegistrationData);
        generateTokenFeeRequest.setOriginatorCurrencyData(originatorCurrencyData);

        String financialUrl = config.getString("financial.node.backend.address");
        String fullNodeAddress = config.getString("full.node.backend.address");
        String trustScoreAddress = config.getString("trust.score.backend.address");
        int walletAddressIndex = config.getInt("source.address.index");

        assertThat(isNodeSupportMultiCurrencyApis(fullNodeAddress)).isTrue();

        //Prepare AddTransaction request for Token Generation
        BaseTransactionData tokenGenerationFeeBT = TokenUtilities.getTokenGenerationFeeBT(generateTokenFeeRequest, financialUrl);
        BigDecimal amount = tokenGenerationFeeBT.getAmount();
        FullNodeFee fullNodeFee = new FullNodeFee(fullNodeAddress, nativeCurrencyHash);
        FullNodeFeeResponse fullNodeFeeResponse = fullNodeFee.createFullNodeFee(new Hash(userPrivateKey), new Hash(userHash), amount, false);
        String transactionDescription = "Generate Token";
        TransactionCreation transactionCreation = new TransactionCreation(seed, userHash, trustScoreAddress, fullNodeAddress, walletAddressIndex, nativeCurrencyHash);
        AddTransactionRequest request = new AddTransactionRequest(transactionCreation.createTokenTransactionByType(tokenGenerationFeeBT, fullNodeFeeResponse, transactionDescription, TransactionType.TokenGeneration));
        //Send Generate Transaction
        Hash transactionTx = TransactionUtils.sendTransaction(request, fullNodeAddress);

        assertThat(transactionTx).isNotNull();
    }

    //Check if the Multi-DAG hard fork occurred
    private boolean isNodeSupportMultiCurrencyApis(String fullNodeAddress) {
        GetTransactionResponse getTransactionResponse = TokenUtilities.getConfirmedMultiDagEvent(fullNodeAddress);
        if (getTransactionResponse.getTransactionData().getTransactionDescription().equals("Multi DAG") && getTransactionResponse.getTransactionData().getTrustChainTrustScore() > 100) {
            return true;
        }
        return false;
    }
}
