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
import io.coti.sdk.utils.Mapper;
import lombok.experimental.UtilityClass;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;

import static io.coti.sdk.utils.Constants.*;

@UtilityClass
public class TokenUtilities {

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper mapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            .setSerializationInclusion(JsonInclude.Include.NON_NULL);

    public GetTransactionResponse getConfirmedMultiDagEvent(String fullNodeUrl) {
        ResponseEntity<GetTransactionResponse> response = restTemplate.getForEntity(fullNodeUrl + EVENT_MULTI_DAG_CONFIRMED, GetTransactionResponse.class);
        if (response.getStatusCode().equals(HttpStatus.OK)) {
            return response.getBody();
        } else {
            throw new CotiRunTimeException("The call to get Confirmed Multi-Dag Event failed");
        }
    }

    public BaseTransactionData getTokenGenerationFeeBT(GenerateTokenFeeRequest generateTokenFeeRequest, String financialUrl) {
        ResponseEntity<String> response = restTemplate.postForEntity(financialUrl + TOKEN_GENERATE, generateTokenFeeRequest, String.class);
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
                postForEntity(fullNodeUrl + USER_TOKENS, getUserTokensRequest, GetUserTokensResponse.class);
        if (response.getStatusCode().equals(HttpStatus.OK) && response.getBody() != null) {
            return response.getBody();
        } else {
            throw new CotiRunTimeException("The call to get User Tokens failed");
        }
    }

    public GetTokenDetailsResponse getTokenDetails(GetTokenDetailsRequest getTokenDetailsRequest, String fullNodeUrl) {
        ResponseEntity<GetTokenDetailsResponse> response = restTemplate.
                postForEntity(fullNodeUrl + TOKEN_DETAILS, getTokenDetailsRequest, GetTokenDetailsResponse.class);
        if (response.getStatusCode().equals(HttpStatus.OK) && response.getBody() != null) {
            return response.getBody();
        } else {
            throw new CotiRunTimeException("The call to get Token details failed");
        }
    }

    public GetTokenDetailsResponse getTokenDetailsBySymbol(GetTokenSymbolDetailsRequest tokenSymbolDetailsRequest, String fullNodeUrl) {
        ResponseEntity<GetTokenDetailsResponse> response = restTemplate.
                postForEntity(fullNodeUrl + TOKEN_SYMBOL_DETAILS, tokenSymbolDetailsRequest, GetTokenDetailsResponse.class);
        if (response.getStatusCode().equals(HttpStatus.OK) && response.getBody() != null) {
            return response.getBody();
        } else {
            throw new CotiRunTimeException("The call to get Token details by symbol failed");
        }
    }

    public GetTokenHistoryResponse getTokenHistory(GetTokenHistoryRequest getTokenHistoryRequest, String fullNodeUrl) {
        ResponseEntity<GetTokenHistoryResponse> response = restTemplate.
                postForEntity(fullNodeUrl + TOKEN_HISTORY, getTokenHistoryRequest, GetTokenHistoryResponse.class);
        if (response.getStatusCode().equals(HttpStatus.OK) && response.getBody() != null) {
            return response.getBody();
        } else {
            throw new CotiRunTimeException("The call to get Token balances failed");
        }
    }

    public GetTokenBalancesResponse getTokenBalances(GetTokenBalancesRequest getTokenBalancesRequest, String fullNodeUrl) {
        ResponseEntity<GetTokenBalancesResponse> response = restTemplate.
                postForEntity(fullNodeUrl + TOKEN_BALANCES, getTokenBalancesRequest, GetTokenBalancesResponse.class);
        if (response.getStatusCode().equals(HttpStatus.OK) && response.getBody() != null) {
            return response.getBody();
        } else {
            throw new CotiRunTimeException("The call to get Token balances failed");
        }
    }

    public MintingFeeQuoteData getTokenMintingFeeQuote(GetTokenMintingFeeQuoteRequest getTokenMintingFeeQuoteRequest, String financialUrl) {
        ResponseEntity<String> response = restTemplate.
                postForEntity(financialUrl + TOKEN_MINT_QUOTE, getTokenMintingFeeQuoteRequest, String.class);
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
        ResponseEntity<String> response = restTemplate.postForEntity(financialUrl + TOKEN_MINT_FEE, tokenMintingFeeRequest, String.class);
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