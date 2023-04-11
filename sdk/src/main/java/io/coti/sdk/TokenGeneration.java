package io.coti.sdk;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.coti.basenode.crypto.CryptoHelper;
import io.coti.basenode.crypto.CurrencyTypeRegistrationCrypto;
import io.coti.basenode.crypto.OriginatorCurrencyCrypto;
import io.coti.basenode.data.*;
import io.coti.basenode.exceptions.CotiRunTimeException;
import io.coti.basenode.http.GenerateTokenFeeRequest;
import io.coti.basenode.http.data.TokenGenerationFeeResponseData;
import io.coti.sdk.utils.Mapper;
import lombok.experimental.UtilityClass;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.Instant;

@UtilityClass
public class TokenGeneration {

    public CurrencyTypeRegistrationData getCurrencyTypeRegistrationData(String userPrivateKey, String userHash, String tokenSymbol, String rateSource, String protectionModel) {
        CurrencyTypeData currencyTypeData = new CurrencyTypeData(CurrencyType.REGULAR_CMD_TOKEN, Instant.now());
        currencyTypeData.setCurrencyRateSourceType(CurrencyRateSourceType.ADDRESS);
        currencyTypeData.setRateSource(rateSource);
        currencyTypeData.setProtectionModel(protectionModel);
        currencyTypeData.setSignerHash(new Hash(userHash));
        CurrencyTypeRegistrationData currencyTypeRegistrationData = new CurrencyTypeRegistrationData(tokenSymbol, currencyTypeData);
        CurrencyTypeRegistrationCrypto currencyTypeRegistrationCrypto = new CurrencyTypeRegistrationCrypto();
        currencyTypeRegistrationData.setSignature(CryptoHelper.signBytes(currencyTypeRegistrationCrypto.getSignatureMessage(currencyTypeRegistrationData), userPrivateKey));
        return currencyTypeRegistrationData;
    }

    public OriginatorCurrencyData getOriginatorCurrencyData(String userPrivateKey, String userHash, String tokenName, String tokenSymbol, String tokenDescription, BigDecimal totalSupply, int scale) {
        OriginatorCurrencyData originatorCurrencyData = new CurrencyData();
        originatorCurrencyData.setName(tokenName);
        originatorCurrencyData.setSymbol(tokenSymbol);
        originatorCurrencyData.setDescription(tokenDescription);
        originatorCurrencyData.setTotalSupply(totalSupply);
        originatorCurrencyData.setScale(scale);
        originatorCurrencyData.setOriginatorHash(new Hash(userHash));
        OriginatorCurrencyCrypto originatorCurrencyCrypto = new OriginatorCurrencyCrypto();
        originatorCurrencyData.setSignature(CryptoHelper.signBytes(originatorCurrencyCrypto.getSignatureMessage(originatorCurrencyData), userPrivateKey));
        return originatorCurrencyData;
    }

    public static BaseTransactionData getTokenGenerationFeeBT(GenerateTokenFeeRequest generateTokenFeeRequest, String financialUrl) {
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate.postForEntity(financialUrl + "/admin/token/generate", generateTokenFeeRequest, String.class);
        if (response.getStatusCode().equals(HttpStatus.CREATED)) {
            return mapResponseToTGFBT(response);
        } else {
            throw new CotiRunTimeException("The call to get Token Generation Fee Base Transaction failed");
        }
    }

    private static BaseTransactionData mapResponseToTGFBT(ResponseEntity<String> response) {
        ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule()).configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false).setSerializationInclusion(JsonInclude.Include.NON_NULL);
        try {
            JsonNode root = mapper.readTree(response.getBody());
            JsonNode tokenGenerationFee = root.path("tokenGenerationFee");
            JsonNode serviceData = root.findValue("serviceData");
            JsonNode jasonSignatureData = root.findValue("signatureData");
            TokenGenerationServiceData tokenGenerationServiceData = mapper.readValue(serviceData.traverse(), TokenGenerationServiceData.class);
            TokenGenerationFeeResponseData tokenGenerationFeeResponseData = mapper.readValue(tokenGenerationFee.traverse(), TokenGenerationFeeResponseData.class);
            SignatureData signatureData = mapper.readValue(jasonSignatureData.traverse(), SignatureData.class);
            BaseTransactionData tokenGenerationFeeBT = Mapper.map(tokenGenerationFeeResponseData, tokenGenerationServiceData);
            tokenGenerationFeeBT.setSignature(signatureData);
            return tokenGenerationFeeBT;
        } catch (IOException e) {
            throw new CotiRunTimeException("Mapping the response of get Token Generation Fee Base Transaction failed");
        }
    }
}