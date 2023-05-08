package io.coti.sdk;

import io.coti.basenode.crypto.CryptoHelper;
import io.coti.basenode.crypto.OriginatorCurrencyCrypto;
import io.coti.basenode.data.*;
import io.coti.basenode.exceptions.CotiRunTimeException;
import io.coti.basenode.http.*;
import io.coti.basenode.http.data.AddressBalanceData;
import io.coti.basenode.http.data.TokenResponseData;
import io.coti.sdk.data.TokenMintingServiceData;
import io.coti.sdk.data.WalletDetails;
import io.coti.sdk.http.AddTransactionRequest;
import io.coti.sdk.http.FullNodeFeeResponse;
import io.coti.sdk.http.TokenMintingFeeRequest;
import io.coti.sdk.utils.CryptoUtils;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.junit.jupiter.api.Test;

import java.io.InputStream;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Collections;
import java.util.Map;

import static io.coti.sdk.utils.Constants.NATIVE_CURRENCY_SYMBOL;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class TokenMintingExampleTest {

    public static void main(String[] args) throws Exception {
        TokenMintingExampleTest tokenMintingTest = new TokenMintingExampleTest();
        tokenMintingTest.tokenMintingTest();
    }

    @Test
    void tokenMintingTest() throws Exception {
        //Loading properties from the properties file
        PropertiesConfiguration config = new PropertiesConfiguration();
        InputStream transferInput = TokenMintingExampleTest.class.getClassLoader().getResourceAsStream("transfer.properties");
        config.load(transferInput);
        PropertiesConfiguration tokenConfig = new PropertiesConfiguration();
        InputStream tokenInput = TokenMintingExampleTest.class.getClassLoader().getResourceAsStream("token.properties");
        tokenConfig.load(tokenInput);
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
        int walletAddressIndex = config.getInt("source.address.index");
        String receiverAddressString = config.getString("receiver.address");
        Hash receiverAddress = new Hash(receiverAddressString);

        //Check if user have tokens with given symbol
        GetUserTokensRequest getUserTokensRequest = new GetUserTokensRequest();
        getUserTokensRequest.setUserHash(userHash);
        getUserTokensRequest.setCreateTime(Instant.now());
        getUserTokensRequest.setSignature(CryptoUtils.signGetUserTokensRequest(getUserTokensRequest, userPrivateKey));
        GetUserTokensResponse getUserTokensResponse = TokenUtilities.getUserTokens(getUserTokensRequest, fullNodeAddress);
        Hash currencyHash = getValidCurrencyHash(getUserTokensResponse, tokenName, tokenSymbol, mintingAmount);
        if (currencyHash == null) {
            throw new CotiRunTimeException("User does not have required Token.");
        }

        //Create AddTransaction request for Token Minting
        GetTokenMintingFeeQuoteRequest getTokenMintingFeeQuoteRequest = TokenData.getTokenMintingFeeQuoteRequest(userHash, currencyHash, mintingAmount, userPrivateKey);
        MintingFeeQuoteData mintingFeeQuoteData = TokenUtilities.getTokenMintingFeeQuote(getTokenMintingFeeQuoteRequest, financialUrl);
        BigDecimal mintingAmountResult = mintingFeeQuoteData.getMintingAmount();
        BigDecimal mintingFee = mintingFeeQuoteData.getMintingFee();
        TokenMintingServiceData tokenMintingServiceData = TokenData.getTokenMintingServiceData(currencyHash, mintingAmountResult, mintingFee, userHash, receiverAddress, userPrivateKey);
        TokenMintingFeeRequest tokenMintingFeeRequest = TokenData.getTokenMintingFeeRequest(tokenMintingServiceData, mintingFeeQuoteData);
        TokenMintingFeeBaseTransactionData tokenMFBT = TokenUtilities.getTokenMintingFee(tokenMintingFeeRequest, financialUrl);
        BigDecimal mintFeeAmount = tokenMFBT.getAmount();
        FullNodeFeeResponse fullNodeFeeResponse = FullNodeFee.createFullNodeFee(userPrivateKey, userHash, mintFeeAmount, false, nativeCurrencyHash, fullNodeAddress);
        String transactionDescription = "Mint Token: " + tokenName;
        WalletDetails transactionDetails = new WalletDetails(seed, trustScoreAddress, fullNodeAddress, walletAddressIndex, nativeCurrencyHash);
        AddTransactionRequest request = new AddTransactionRequest(TransactionUtilities.createTokenTransactionByType(tokenMFBT, fullNodeFeeResponse, transactionDescription, TransactionType.TokenMinting, transactionDetails));
        //Send Token Minting Transaction
        Hash transactionTx = TransactionUtils.sendTransaction(request, fullNodeAddress);

        assertThat(transactionTx).isNotNull();
        assertThat(isTokenDetailsCorrect(tokenSymbol, currencyHash, fullNodeAddress)).isTrue();
        assertThat(isTokenDetailsUpdated(currencyHash, mintingAmount, fullNodeAddress)).isTrue();

        int attempts = 40;
        while (attempts > 0) {
            if (checkTokenBalance(receiverAddress, currencyHash, fullNodeAddress)) {
                break;
            } else {
                attempts--;
                Thread.sleep(1000);
            }
        }
        assertThat(checkTokenBalance(receiverAddress, currencyHash, fullNodeAddress)).isTrue();
    }

    private Hash getValidCurrencyHash(GetUserTokensResponse getUserTokensResponse, String tokenName, String tokenSymbol, BigDecimal mintingAmount) {
        for (TokenResponseData tokenData : getUserTokensResponse.getUserTokens()) {
            if (tokenData.getCurrencyName().equals(tokenName) && tokenData.getCurrencySymbol().equals(tokenSymbol) &&
                    tokenData.isConfirmed() && tokenData.getMintableAmount().compareTo(mintingAmount) == 1 &&
                    mintingAmount.compareTo(tokenData.getMintableAmount().subtract(tokenData.getMintedAmount())) != 1
            ) {
                return new Hash(tokenData.getCurrencyHash());
            }
        }
        return null;
    }

    private boolean isTokenDetailsCorrect(String tokenSymbol, Hash currencyHash, String fullNodeAddress) {
        GetTokenSymbolDetailsRequest request = new GetTokenSymbolDetailsRequest();
        request.setSymbol(tokenSymbol);
        GetTokenDetailsResponse getTokenDetailsResponse = TokenUtilities.getTokenDetailsBySymbol(request, fullNodeAddress);
        if (new Hash(getTokenDetailsResponse.getToken().getCurrencyHash()).equals(currencyHash)) {
            System.out.println("Token Details by symbol " + tokenSymbol + " :");
            System.out.println("Currency Name = " + getTokenDetailsResponse.getToken().getCurrencyName() + ";");
            System.out.println("Currency Hash = " + getTokenDetailsResponse.getToken().getCurrencyHash() + ";");
            System.out.println("Currency Mintable Amount = " + getTokenDetailsResponse.getToken().getMintableAmount() + ";");
            System.out.println("Currency Minted Amount = " + getTokenDetailsResponse.getToken().getMintedAmount() + ";");
            System.out.println("Currency Total Supply = " + getTokenDetailsResponse.getToken().getTotalSupply() + ";");
            return true;
        }
        return false;
    }

    private boolean isTokenDetailsUpdated(Hash currencyHash, BigDecimal mintingAmount, String fullNodeAddress) {
        GetTokenDetailsRequest request = new GetTokenDetailsRequest();
        request.setCurrencyHash(currencyHash);
        GetTokenDetailsResponse getTokenDetailsResponse = TokenUtilities.getTokenDetails(request, fullNodeAddress);
        if (getTokenDetailsResponse.getToken().getMintedAmount().compareTo(mintingAmount) >= 0) {
            System.out.println("Token Details by hash " + currencyHash + " :");
            System.out.println("Currency Name = " + getTokenDetailsResponse.getToken().getCurrencyName() + ";");
            System.out.println("Currency Symbol = " + getTokenDetailsResponse.getToken().getCurrencySymbol() + ";");
            System.out.println("Currency Mintable Amount = " + getTokenDetailsResponse.getToken().getMintableAmount() + ";");
            System.out.println("Currency Minted Amount = " + getTokenDetailsResponse.getToken().getMintedAmount() + ";");
            System.out.println("Currency Total Supply = " + getTokenDetailsResponse.getToken().getTotalSupply() + ";");
            return true;
        }
        return false;
    }

    private boolean checkTokenBalance(Hash receiverAddress, Hash currencyHash, String fullNodeAddress) {
        GetTokenBalancesRequest tokenBalancesRequest = new GetTokenBalancesRequest();
        tokenBalancesRequest.setAddresses(Collections.singletonList(receiverAddress));
        GetTokenBalancesResponse response = TokenUtilities.getTokenBalances(tokenBalancesRequest, fullNodeAddress);
        Map<Hash, AddressBalanceData> addressTokenBalanceDataMap = response.getTokenBalances().get(receiverAddress);

        if (addressTokenBalanceDataMap == null || addressTokenBalanceDataMap.isEmpty()) {
            return false;
        }

        AddressBalanceData addressTokenBalance = addressTokenBalanceDataMap.get(currencyHash);
        if (addressTokenBalance != null) {
            System.out.println("Token Balance for currency hash " + currencyHash + " :");
            System.out.println("Token Balance: " + addressTokenBalance.getAddressBalance());
            System.out.println("Token PreBalance: " + addressTokenBalance.getAddressPreBalance());
            return true;
        }
        return false;
    }
}
