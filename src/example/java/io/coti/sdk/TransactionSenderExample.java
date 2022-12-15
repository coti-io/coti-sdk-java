package io.coti.sdk;

import io.coti.basenode.crypto.NodeCryptoHelper;
import io.coti.basenode.crypto.OriginatorCurrencyCrypto;
import io.coti.basenode.data.Hash;
import io.coti.basenode.http.CustomHttpComponentsClientHttpRequestFactory;
import io.coti.sdk.http.AddTransactionRequest;
import io.coti.sdk.http.AddTransactionResponse;
import io.coti.sdk.utils.CryptoUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.MethodInvokingBean;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import java.math.BigDecimal;

@Slf4j
@Service
@PropertySource("classpath:application.properties")
public class TransactionSenderExample {

    @Value("${seed}")
    private String seed;
    @Value("${user.hash}")
    private String userHash;
    @Value("${full.node.backend.address}")
    private String fullNodeAddress;
    @Value("${trust.score.backend.address}")
    private String trustScoreAddress;
    @Value("${native.currency.symbol:COTI}")
    private String nativeCurrencySymbol;
    private Hash nativeCurrencyHash;
    @Value("${transaction.description:}")
    private String description;
    @Value("${transfer.amount}")
    private String amountString;
    @Value("${source.address.index}")
    private int walletAddressIndex;
    private TransactionCreation transactionCreation;


    @PostConstruct
    private void init() {
        nativeCurrencyHash = OriginatorCurrencyCrypto.calculateHash(nativeCurrencySymbol);
        String userPrivateKey = CryptoUtils.getPrivateKeyFromSeed((new Hash(seed)).getBytes()).toHexString();
        setNodePrivateKey(userPrivateKey);
        transactionCreation = new TransactionCreation(seed, userHash, trustScoreAddress, fullNodeAddress, walletAddressIndex, nativeCurrencyHash);
        sendTransaction(new BigDecimal(amountString), description);
    }

    public void sendTransaction(BigDecimal amount, String transactionDescription) {
        RestTemplate restTemplate = new RestTemplate(new CustomHttpComponentsClientHttpRequestFactory());
        AddTransactionRequest request = null;
        AddTransactionResponse response = null;
        try {
            request = new AddTransactionRequest(transactionCreation.createTransactionData(amount, transactionDescription));
            HttpEntity<AddTransactionRequest> entity = new HttpEntity<>(request);
            response = restTemplate.exchange(fullNodeAddress + "/transaction", HttpMethod.PUT, entity, AddTransactionResponse.class).getBody();
        } catch (Exception e) {
            log.error("Exception for Send new Transaction: ", e);
        }
        if (request != null && response != null && response.getStatus().equals("Success")) {
            log.info("####################################################################");
            log.info("#################      {}      ####################", response.getMessage());
            log.info("# {} #", request.getHash());
            log.info("####################################################################");
        } else {
            log.error("Adding new Transaction failed!");
        }
    }

    private void setNodePrivateKey(String userPrivateKey) {
        MethodInvokingBean methodInvokingBean = new MethodInvokingBean();
        methodInvokingBean.setStaticMethod(NodeCryptoHelper.class.getName() + ".nodePrivateKey");
        methodInvokingBean.setArguments(userPrivateKey);
        try {
            methodInvokingBean.afterPropertiesSet();
        } catch (Exception e) {
            log.error("Exception: ", e);
        }
    }

}