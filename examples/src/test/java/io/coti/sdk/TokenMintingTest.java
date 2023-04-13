package io.coti.sdk;

import io.coti.basenode.crypto.CryptoHelper;
import io.coti.basenode.crypto.OriginatorCurrencyCrypto;
import io.coti.basenode.data.*;
import io.coti.basenode.exceptions.CotiRunTimeException;
import io.coti.basenode.http.GetTokenMintingFeeQuoteRequest;
import io.coti.basenode.http.GetUserTokensRequest;
import io.coti.basenode.http.GetUserTokensResponse;
import io.coti.basenode.http.TokenMintingFeeRequest;
import io.coti.basenode.http.data.TokenResponseData;
import io.coti.sdk.http.AddTransactionRequest;
import io.coti.sdk.http.FullNodeFeeResponse;
import io.coti.sdk.utils.CryptoUtils;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;

import static io.coti.sdk.utils.Constants.NATIVE_CURRENCY_SYMBOL;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class TokenMintingTest {

    public static void main(String[] args) throws Exception {
        TokenMintingTest tokenMintingTest = new TokenMintingTest();
        tokenMintingTest.tokenMintingTest();
    }

    @Test
    void tokenMintingTest() throws Exception {
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
        Hash userPrivateKey = CryptoUtils.getPrivateKeyFromSeed((new Hash(seed).getBytes()));
        String userHashString = CryptoHelper.getPublicKeyFromPrivateKey(userPrivateKey.toHexString());
        Hash userHash = new Hash(userHashString);
        Hash nativeCurrencyHash = OriginatorCurrencyCrypto.calculateHash(NATIVE_CURRENCY_SYMBOL);

        String tokenName = tokenConfig.getString("name");
        String tokenSymbol = tokenConfig.getString("symbol");
        BigDecimal mintingAmount = tokenConfig.getBigDecimal("mintingAmount");
        String fullNodeAddress = config.getString("full.node.backend.address");
        String financialUrl = config.getString("financial.node.backend.address");
        String trustScoreAddress = config.getString("trust.score.backend.address");
        int walletAddressIndex = config.getInt("target.address.index");
        Hash receiverAddress = CryptoHelper.generateAddress(seed, walletAddressIndex);
        GetUserTokensRequest getUserTokensRequest = new GetUserTokensRequest();
        getUserTokensRequest.setUserHash(userHash);
        getUserTokensRequest.setCreateTime(Instant.now());
        getUserTokensRequest.setSignature(CryptoUtils.signGetUserTokensRequest(getUserTokensRequest, userPrivateKey));

        GetUserTokensResponse getUserTokensResponse = TokenManagement.getUserTokens(getUserTokensRequest, fullNodeAddress);
        Hash currencyHash = getValidCurrencyHash(getUserTokensResponse, tokenName, tokenSymbol, mintingAmount);
        if (currencyHash == null) {
            throw new CotiRunTimeException("User does not have required Token.");
        }
        GetTokenMintingFeeQuoteRequest getTokenMintingFeeQuoteRequest = TokenManagement.getTokenMintingFeeQuoteRequest(userHash, currencyHash, mintingAmount, userPrivateKey);
        MintingFeeQuoteData mintingFeeQuoteData = TokenManagement.getTokenMintingFeeQuote(getTokenMintingFeeQuoteRequest, financialUrl);
        BigDecimal mintingAmountResult = mintingFeeQuoteData.getMintingAmount();
        BigDecimal mintingFee = mintingFeeQuoteData.getMintingFee();
        TokenMintingServiceData tokenMintingServiceData = TokenManagement.
                getTokenMintingServiceData(currencyHash, mintingAmountResult, mintingFee, userHash, receiverAddress, userPrivateKey);
        TokenMintingFeeRequest tokenMintingFeeRequest = TokenManagement.getTokenMintingFeeRequest(tokenMintingServiceData, mintingFeeQuoteData);
        TokenMintingFeeBaseTransactionData tokenMFBT = TokenManagement.getTokenMintingFee(tokenMintingFeeRequest, financialUrl);
        BigDecimal mintFeeAmount = tokenMFBT.getAmount();
        FullNodeFee fullNodeFee = new FullNodeFee(fullNodeAddress, nativeCurrencyHash);
        FullNodeFeeResponse fullNodeFeeResponse = fullNodeFee.createFullNodeFee(userPrivateKey, userHash, mintFeeAmount, false);
        String transactionDescription = "Mint Token: " + tokenName;
        TransactionCreation transactionCreation = new TransactionCreation(seed, userHash.toHexString(), trustScoreAddress, fullNodeAddress, walletAddressIndex, nativeCurrencyHash);
        AddTransactionRequest request = new AddTransactionRequest(transactionCreation.
                createTokenTransactionByType(tokenMFBT, fullNodeFeeResponse, transactionDescription, TransactionType.TokenMinting));
        Hash transactionTx = TransactionUtils.sendTransaction(request, fullNodeAddress);

        assertThat(transactionTx).isNotNull();
    }

    private Hash getValidCurrencyHash(GetUserTokensResponse getUserTokensResponse, String tokenName, String tokenSymbol, BigDecimal mintingAmount) {
        for (TokenResponseData tokenData : getUserTokensResponse.getUserTokens()) {
            if (tokenData.getCurrencyName().equals(tokenName) && tokenData.getCurrencySymbol().equals(tokenSymbol) &&
                    tokenData.isConfirmed() && tokenData.getMintableAmount().compareTo(tokenData.getMintedAmount()) == 1 &&
                    mintingAmount.compareTo(tokenData.getMintableAmount().subtract(tokenData.getMintedAmount())) != 1
            ) {
                return new Hash(tokenData.getCurrencyHash());
            }
        }
        return null;
    }
}
