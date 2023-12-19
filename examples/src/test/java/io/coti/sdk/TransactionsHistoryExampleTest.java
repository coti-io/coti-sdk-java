package io.coti.sdk;

import io.coti.sdk.base.*;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.InputStream;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.awaitility.Awaitility.await;

public class TransactionsHistoryExampleTest {

    private static Hash receiverAddress;
    private static String fullNodeAddress;
    private static String symbol;

    public static void main(String[] args) throws Exception {
        TransactionsHistoryExampleTest transactionsHistoryExampleTest = new TransactionsHistoryExampleTest();
        transactionsHistoryExampleTest.init();
        transactionsHistoryExampleTest.getAddressTransactionBatchTest();
        transactionsHistoryExampleTest.getTransactionsHistoryByTimeStampTest();
        transactionsHistoryExampleTest.getTokenHistoryTest();
    }

    @BeforeAll
    static void init() throws ConfigurationException {
        PropertiesConfiguration config = new PropertiesConfiguration();
        InputStream transferInput = TransferExampleTest.class.getClassLoader().getResourceAsStream("transfer.properties");
        config.load(transferInput);
        String receiverAddressString = config.getString("receiver.address");
        receiverAddress = new Hash(receiverAddressString);
        fullNodeAddress = config.getString("full.node.backend.address");
        PropertiesConfiguration tokenConfig = new PropertiesConfiguration();
        InputStream tokenInput = TransferExampleTest.class.getClassLoader().getResourceAsStream("token.properties");
        tokenConfig.load(tokenInput);
        symbol = tokenConfig.getString("symbol");
    }

    @Test
    void getAddressTransactionBatchTest() throws Exception {
        GetAddressTransactionBatchRequest addressTransactionBatchRequest = new GetAddressTransactionBatchRequest();
        addressTransactionBatchRequest.setAddresses(Collections.singletonList(receiverAddress));
        addressTransactionBatchRequest.setExtended(true);
        addressTransactionBatchRequest.setIncludeRuntimeTrustScore(true);
        List<TransactionResponseData> transactionsResponseData = TransactionUtilities.getAddressTransactionBatch(addressTransactionBatchRequest, fullNodeAddress);

        assertThat(transactionsResponseData).isNotNull();
        System.out.println("Transaction Response Data List has " + transactionsResponseData.size() + " Transaction Data elements.");
        assertThat(transactionsResponseData.size()).isNotZero();
    }

    @Test
    void getTransactionsHistoryByTimeStampTest() throws Exception {
        Set<Hash> addresses = new HashSet<>();
        addresses.add(receiverAddress);
        GetAddressTransactionBatchByTimestampRequest addressTransactionBatchRequest = new GetAddressTransactionBatchByTimestampRequest(
                addresses,
                Instant.now().minus(120, ChronoUnit.DAYS),
                null,
                50,
                TimeOrder.DESC,
                true
        );
        List<TransactionResponseData> transactionsResponseData = TransactionUtilities.getTransactionsHistoryByTimeStamp(addressTransactionBatchRequest, fullNodeAddress);

        assertThat(transactionsResponseData).isNotNull();
        System.out.println("Transaction Response Data List has " + transactionsResponseData.size() + " Transaction Data elements.");
        assertThat(transactionsResponseData.size()).isNotZero();
    }

    @Test
    void getTokenHistoryTest() {
        GetTokenHistoryRequest getTokenHistoryRequest = new GetTokenHistoryRequest();
        getTokenHistoryRequest.setCurrencyHash(OriginatorCurrencyCrypto.calculateHash(symbol));

        AtomicReference<GetTokenHistoryResponse> response = new AtomicReference<>();
        await().atMost(90, TimeUnit.SECONDS).until(() -> {
            try {
                response.set(TokenUtilities.getTokenHistory(getTokenHistoryRequest, fullNodeAddress));
                if (response.get() != null) {
                    return true;
                }
            } catch (Exception e) {
                System.out.println("Exception while getting token history");
            }
            return false;
        });
        System.out.println("Token History Data List has " + response.get().getTransactions().size() + " Transaction Data elements.");
        assertThat(response.get().getTransactions().size()).isNotZero();
    }
}
