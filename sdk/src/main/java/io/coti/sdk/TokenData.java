package io.coti.sdk;

import io.coti.basenode.data.*;
import io.coti.basenode.http.GetTokenMintingFeeQuoteRequest;
import io.coti.sdk.http.TokenMintingFeeRequest;
import io.coti.sdk.utils.CryptoUtils;
import io.coti.sdk.data.TokenMintingServiceData;
import lombok.experimental.UtilityClass;

import java.math.BigDecimal;
import java.time.Instant;

@UtilityClass
public class TokenData {

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
}
