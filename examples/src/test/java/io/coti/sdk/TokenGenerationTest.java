package io.coti.sdk;

import io.coti.basenode.crypto.CryptoHelper;
import io.coti.basenode.crypto.OriginatorCurrencyCrypto;
import io.coti.basenode.data.*;
import io.coti.basenode.http.GenerateTokenFeeRequest;
import io.coti.sdk.http.AddTransactionRequest;
import io.coti.sdk.http.FullNodeFeeResponse;
import io.coti.sdk.utils.CryptoUtils;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static io.coti.sdk.utils.Constants.NATIVE_CURRENCY_SYMBOL;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class TokenGenerationTest {

    public static void main(String[] args) throws Exception {
        TokenGenerationTest tokenGenerationTest = new TokenGenerationTest();
        tokenGenerationTest.tokenGenerationTest();
    }

    @Test
    void tokenGenerationTest() throws Exception {
        PropertiesConfiguration config = new PropertiesConfiguration();
        config.load("src/test/resources/transfer.properties");
        PropertiesConfiguration tokenConfig = new PropertiesConfiguration();
        tokenConfig.load("src/test/resources/token.properties");

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
        OriginatorCurrencyData originatorCurrencyData = TokenManagement.getOriginatorCurrencyData(userPrivateKey, userHash, tokenName, tokenSymbol, tokenDescription, totalSupply, scale);

        String rateSource = tokenConfig.getString("rateSource");
        String protectionModel = tokenConfig.getString("protectionModel");
        CurrencyTypeRegistrationData currencyTypeRegistrationData = TokenManagement.getCurrencyTypeRegistrationData(userPrivateKey, userHash, tokenSymbol, rateSource, protectionModel);

        GenerateTokenFeeRequest generateTokenFeeRequest = new GenerateTokenFeeRequest();
        generateTokenFeeRequest.setCurrencyTypeData(currencyTypeRegistrationData);
        generateTokenFeeRequest.setOriginatorCurrencyData(originatorCurrencyData);

        String financialUrl = config.getString("financial.node.backend.address");
        String fullNodeAddress = config.getString("full.node.backend.address");
        String trustScoreAddress = config.getString("trust.score.backend.address");
        int walletAddressIndex = config.getInt("source.address.index");

        BaseTransactionData tokenGenerationFeeBT = TokenManagement.getTokenGenerationFeeBT(generateTokenFeeRequest, financialUrl);
        BigDecimal amount = tokenGenerationFeeBT.getAmount();
        FullNodeFee fullNodeFee = new FullNodeFee(fullNodeAddress, nativeCurrencyHash);
        FullNodeFeeResponse fullNodeFeeResponse = fullNodeFee.createFullNodeFee(new Hash(userPrivateKey), new Hash(userHash), amount, false);
        String transactionDescription = "Generate Token";
        TransactionCreation transactionCreation = new TransactionCreation(seed, userHash, trustScoreAddress, fullNodeAddress, walletAddressIndex, nativeCurrencyHash);
        AddTransactionRequest request = new AddTransactionRequest(transactionCreation.createTokenTransactionByType(tokenGenerationFeeBT, fullNodeFeeResponse, transactionDescription, TransactionType.TokenGeneration));
        Hash transactionTx = TransactionUtils.sendTransaction(request, fullNodeAddress);

        assertThat(transactionTx).isNotNull();
    }
}
