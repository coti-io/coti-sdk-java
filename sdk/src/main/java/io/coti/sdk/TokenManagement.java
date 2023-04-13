package io.coti.sdk;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.coti.basenode.data.*;
import io.coti.basenode.exceptions.CotiRunTimeException;
import io.coti.basenode.http.*;
import io.coti.basenode.http.data.TokenGenerationFeeResponseData;
import io.coti.sdk.utils.CryptoUtils;
import io.coti.sdk.utils.Mapper;
import lombok.experimental.UtilityClass;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.Instant;

@UtilityClass
public class TokenManagement {

    RestTemplate restTemplate = new RestTemplate();
    ObjectMapper mapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            .setSerializationInclusion(JsonInclude.Include.NON_NULL);

    public CurrencyTypeRegistrationData getCurrencyTypeRegistrationData(String userPrivateKey, String userHash,
                                                                        String tokenSymbol, String rateSource, String protectionModel) {
        CurrencyTypeData currencyTypeData = new CurrencyTypeData(CurrencyType.REGULAR_CMD_TOKEN, Instant.now());
        currencyTypeData.setCurrencyRateSourceType(CurrencyRateSourceType.ADDRESS);
        currencyTypeData.setRateSource(rateSource);
        currencyTypeData.setProtectionModel(protectionModel);
        currencyTypeData.setSignerHash(new Hash(userHash));
        CurrencyTypeRegistrationData currencyTypeRegistrationData = new CurrencyTypeRegistrationData(tokenSymbol, currencyTypeData);
        currencyTypeRegistrationData.setSignature(CryptoUtils.signCurrencyTypeRegistrationData(currencyTypeRegistrationData, userPrivateKey));
        return currencyTypeRegistrationData;
    }

    public OriginatorCurrencyData getOriginatorCurrencyData(String userPrivateKey, String userHash, String tokenName,
                                                            String tokenSymbol, String tokenDescription, BigDecimal totalSupply, int scale) {
        OriginatorCurrencyData originatorCurrencyData = new CurrencyData();
        originatorCurrencyData.setName(tokenName);
        originatorCurrencyData.setSymbol(tokenSymbol);
        originatorCurrencyData.setDescription(tokenDescription);
        originatorCurrencyData.setTotalSupply(totalSupply);
        originatorCurrencyData.setScale(scale);
        originatorCurrencyData.setOriginatorHash(new Hash(userHash));
        originatorCurrencyData.setSignature(CryptoUtils.signOriginatorCurrencyData(originatorCurrencyData, userPrivateKey));
        return originatorCurrencyData;
    }

    public GetTokenMintingFeeQuoteRequest getTokenMintingFeeQuoteRequest(Hash userHash, Hash currencyHash, BigDecimal mintingAmount, Hash userPrivateKey) {
        GetTokenMintingFeeQuoteRequest getTokenMintingFeeQuoteRequest = new GetTokenMintingFeeQuoteRequest();
        getTokenMintingFeeQuoteRequest.setUserHash(userHash);
        getTokenMintingFeeQuoteRequest.setCurrencyHash(currencyHash);
        getTokenMintingFeeQuoteRequest.setMintingAmount(mintingAmount);
        getTokenMintingFeeQuoteRequest.setCreateTime(Instant.now());
        getTokenMintingFeeQuoteRequest.setSignature(CryptoUtils.signGetTokenMintingFeeQuoteRequest(getTokenMintingFeeQuoteRequest, userPrivateKey));
        return getTokenMintingFeeQuoteRequest;
    }

    public static TokenMintingServiceData getTokenMintingServiceData(Hash currencyHash, BigDecimal mintingAmountResult,
                                                                     BigDecimal mintingFee, Hash userHash, Hash receiverAddress, Hash userPrivateKey) {
        TokenMintingServiceData tokenMintingServiceData = new TokenMintingServiceData();
        tokenMintingServiceData.setMintingAmount(mintingAmountResult);
        tokenMintingServiceData.setMintingCurrencyHash(currencyHash);
        tokenMintingServiceData.setFeeAmount(mintingFee);
        tokenMintingServiceData.setReceiverAddress(receiverAddress);
        tokenMintingServiceData.setSignerHash(userHash);
        tokenMintingServiceData.setCreateTime(Instant.now());
        tokenMintingServiceData.setSignature(CryptoUtils.signTokenMintingServiceData(tokenMintingServiceData, userPrivateKey));
        return tokenMintingServiceData;
    }

    public static TokenMintingFeeRequest getTokenMintingFeeRequest(TokenMintingServiceData tokenMintingServiceData, MintingFeeQuoteData mintingFeeQuoteData) {
        TokenMintingFeeRequest tokenMintingFeeRequest = new TokenMintingFeeRequest();
        tokenMintingFeeRequest.setTokenMintingServiceData(tokenMintingServiceData);
        tokenMintingFeeRequest.setMintingFeeQuoteData(mintingFeeQuoteData);
        return tokenMintingFeeRequest;
    }

    public BaseTransactionData getTokenGenerationFeeBT(GenerateTokenFeeRequest generateTokenFeeRequest, String financialUrl) {
        ResponseEntity<String> response = restTemplate.postForEntity(financialUrl + "/admin/token/generate", generateTokenFeeRequest, String.class);
        if (response.getStatusCode().equals(HttpStatus.CREATED)) {
            return mapResponseToTGFBT(response);
        } else {
            throw new CotiRunTimeException("The call to get Token Generation Fee Base Transaction failed");
        }
    }

    private BaseTransactionData mapResponseToTGFBT(ResponseEntity<String> response) {
        try {
            JsonNode root = mapper.readTree(response.getBody());
            JsonNode tokenGenerationFee = root.path("tokenGenerationFee");
            JsonNode serviceData = root.findValue("serviceData");
            JsonNode jasonSignatureData = root.findValue("signatureData");
            TokenGenerationServiceData tokenGenerationServiceData = mapper.readValue(serviceData.traverse(), TokenGenerationServiceData.class);
            TokenGenerationFeeResponseData tokenGenerationFeeResponseData = mapper.readValue(tokenGenerationFee.traverse(), TokenGenerationFeeResponseData.class);
            SignatureData signatureData = mapper.readValue(jasonSignatureData.traverse(), SignatureData.class);
            BaseTransactionData tokenGenerationFeeBT = Mapper.map(tokenGenerationFeeResponseData, tokenGenerationServiceData).toTokenGenerationFeeBaseTransactionData();
            tokenGenerationFeeBT.setSignature(signatureData);
            return tokenGenerationFeeBT;
        } catch (IOException e) {
            throw new CotiRunTimeException("Mapping the response of get Token Generation Fee Base Transaction failed");
        }
    }

    public GetUserTokensResponse getUserTokens(GetUserTokensRequest getUserTokensRequest, String fullNodeUrl) {
        ResponseEntity<GetUserTokensResponse> response = restTemplate.
                postForEntity(fullNodeUrl + "/currencies/token/user", getUserTokensRequest, GetUserTokensResponse.class);
        if (response.getStatusCode().equals(HttpStatus.OK) && response.getBody() != null) {
            return response.getBody();
        } else {
            throw new CotiRunTimeException("The call to get User Tokens failed");
        }
    }

    public MintingFeeQuoteData getTokenMintingFeeQuote(GetTokenMintingFeeQuoteRequest getTokenMintingFeeQuoteRequest, String financialUrl) {
        ResponseEntity<String> response = restTemplate.
                postForEntity(financialUrl + "/admin/token/mint/quote", getTokenMintingFeeQuoteRequest, String.class);
        if (response.getStatusCode().equals(HttpStatus.CREATED)) {
            return mapResponseToMintingFeeQuoteData(response);
        } else {
            throw new CotiRunTimeException("The call to get Token Minting Fee Quote failed");
        }
    }

    private MintingFeeQuoteData mapResponseToMintingFeeQuoteData(ResponseEntity<String> response) {
        try {
            JsonNode root = mapper.readTree(response.getBody());
            JsonNode mintingFeeQuote = root.findValue("mintingFeeQuote");
            return mapper.readValue(mintingFeeQuote.traverse(), MintingFeeQuoteData.class);
        } catch (IOException e) {
            throw new CotiRunTimeException("Mapping the response of Minting Fee Quote Data failed");
        }
    }

    public TokenMintingFeeBaseTransactionData getTokenMintingFee(TokenMintingFeeRequest tokenMintingFeeRequest, String financialUrl) {
        ResponseEntity<String> response = restTemplate.postForEntity(financialUrl + "/admin/token/mint/fee", tokenMintingFeeRequest, String.class);
        if (response.getStatusCode().equals(HttpStatus.CREATED)) {
            return mapResponseToMFBT(response);
        } else {
            throw new CotiRunTimeException("The call to get Token Minting Fee Quote failed");
        }
    }

    private static TokenMintingFeeBaseTransactionData mapResponseToMFBT(ResponseEntity<String> response) {
        try {
            JsonNode root = mapper.readTree(response.getBody());
            JsonNode tokenServiceFee = root.findValue("tokenServiceFee");
            return mapper.readValue(tokenServiceFee.traverse(), TokenMintingFeeBaseTransactionData.class);
        } catch (IOException e) {
            throw new CotiRunTimeException("Mapping the response of Mint Fee Base Transaction Data failed");
        }
    }
}