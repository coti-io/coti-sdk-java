package io.coti.sdk;

import io.coti.basenode.crypto.TransactionCrypto;
import io.coti.basenode.http.CustomHttpComponentsClientHttpRequestFactory;
import io.coti.sdk.config.NodeConfig;
import io.coti.sdk.http.AddTransactionRequest;
import io.coti.sdk.http.AddTransactionResponse;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;

@TestPropertySource(locations = "classpath:application.properties")
@ContextConfiguration(classes = {TransactionCreation.class, TransactionCryptoCreator.class, TransactionCrypto.class,
        BaseTransactionCreation.class, TrustScoreData.class, AccountBalance.class, NodeConfig.class, FullNodeFee.class,
        NetworkFee.class
})

@SpringBootTest
@RunWith(SpringRunner.class)
@Slf4j
public class CotiSdkJavaTest {

    @Autowired
    private TransactionCreation transactionCreation;
    @Autowired
    private TransactionCryptoCreator transactionCryptoCreator;
    @Autowired
    private TransactionCrypto transactionCrypto;
    @Autowired
    private BaseTransactionCreation baseTransactionCreation;
    @Autowired
    private TrustScoreData trustScoreData;
    @Autowired
    private AccountBalance accountBalance;
    @Autowired
    private FullNodeFee fullNodeFee;
    @Autowired
    private NetworkFee networkFee;
    @Value("${full.node.backend.address}")
    private String fullNodeAddress;

    @Test
    public void sendTransaction() {
        RestTemplate restTemplate = new RestTemplate(new CustomHttpComponentsClientHttpRequestFactory());
        AddTransactionRequest request = null;
        AddTransactionResponse response = null;
        try {
            request = transactionCreation.createAddTransactionRequest();
            HttpEntity<AddTransactionRequest> entity = new HttpEntity<>(request);
            response = restTemplate.exchange(fullNodeAddress + "/transaction", HttpMethod.PUT, entity, AddTransactionResponse.class).getBody();
        } catch (Exception e) {
            log.error("Exception for Send new Transaction: ", e);
        }
        if (request != null && response != null && response.getStatus().equals("Success")) {
            Assert.assertEquals("Success", response.getStatus());
            log.info("####################################################################");
            log.info("#################      {}      ####################", response.getMessage());
            log.info("# {} #", request.getHash());
            log.info("####################################################################");
        } else {
            log.error("Adding new Transaction failed!");
        }
    }

}