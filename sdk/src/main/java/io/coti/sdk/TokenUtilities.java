package io.coti.sdk;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.coti.sdk.http.TokenMintingFeeRequest;
import io.coti.sdk.utils.Mapper;
import lombok.experimental.UtilityClass;
import org.springframework.http.ResponseEntity;
import io.coti.sdk.base.*;
import java.io.IOException;
import com.fasterxml.jackson.datatype.jsr310.*;
import com.fasterxml.jackson.core.*;

import static io.coti.sdk.utils.Constants.*;

@UtilityClass
public class TokenUtilities {

    private final ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule()).configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false).setSerializationInclusion(JsonInclude.Include.NON_NULL);

    public GetTransactionResponse getConfirmedMultiDagEvent(String fullNodeUrl) {
        ResponseEntity<GetTransactionResponse> response = (ResponseEntity<GetTransactionResponse>) Utilities.getRequest(fullNodeUrl + EVENT_MULTI_DAG_CONFIRMED, GetTransactionResponse.class);
        return response.getBody();
    }

    public BaseTransactionData getTokenGenerationFeeBT(GenerateTokenFeeRequest generateTokenFeeRequest, String financialUrl) {
        ResponseEntity<String> response = (ResponseEntity<String>) Utilities.postRequest(financialUrl + TOKEN_GENERATE, generateTokenFeeRequest, String.class);
        return mapResponseToTGFBT(response);
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
        ResponseEntity<GetUserTokensResponse> response = (ResponseEntity<GetUserTokensResponse>) Utilities.postRequest(fullNodeUrl + USER_TOKENS, getUserTokensRequest, GetUserTokensResponse.class);
        return response.getBody();
    }

    public GetTokenDetailsResponse getTokenDetails(GetTokenDetailsRequest getTokenDetailsRequest, String fullNodeUrl) {
        ResponseEntity<GetTokenDetailsResponse> response = (ResponseEntity<GetTokenDetailsResponse>) Utilities.postRequest(fullNodeUrl + TOKEN_DETAILS, getTokenDetailsRequest, GetTokenDetailsResponse.class);
        return response.getBody();
    }

    public GetTokenDetailsResponse getTokenDetailsBySymbol(GetTokenSymbolDetailsRequest tokenSymbolDetailsRequest, String fullNodeUrl) {
        ResponseEntity<GetTokenDetailsResponse> response = (ResponseEntity<GetTokenDetailsResponse>) Utilities.postRequest(fullNodeUrl + TOKEN_SYMBOL_DETAILS, tokenSymbolDetailsRequest, GetTokenDetailsResponse.class);
        return response.getBody();
    }

    public GetTokenHistoryResponse getTokenHistory(GetTokenHistoryRequest getTokenHistoryRequest, String fullNodeUrl) {
        ResponseEntity<GetTokenHistoryResponse> response = (ResponseEntity<GetTokenHistoryResponse>) Utilities.postRequest(fullNodeUrl + TOKEN_HISTORY, getTokenHistoryRequest, GetTokenHistoryResponse.class);
        return response.getBody();
    }

    public GetTokenBalancesResponse getTokenBalances(GetTokenBalancesRequest getTokenBalancesRequest, String fullNodeUrl) {
        ResponseEntity<GetTokenBalancesResponse> response = (ResponseEntity<GetTokenBalancesResponse>) Utilities.postRequest(fullNodeUrl + TOKEN_BALANCES, getTokenBalancesRequest, GetTokenBalancesResponse.class);
        return response.getBody();
    }

    public MintingFeeQuoteData getTokenMintingFeeQuote(GetTokenMintingFeeQuoteRequest getTokenMintingFeeQuoteRequest, String financialUrl) {
        ResponseEntity<String> response = (ResponseEntity<String>) Utilities.postRequest(financialUrl + TOKEN_MINT_QUOTE, getTokenMintingFeeQuoteRequest, String.class);
        return mapResponseToMintingFeeQuoteData(response);
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
        ResponseEntity<String> response = (ResponseEntity<String>) Utilities.postRequest(financialUrl + TOKEN_MINT_FEE, tokenMintingFeeRequest, String.class);
        return mapResponseToMFBT(response);
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