package io.coti.sdk;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.coti.basenode.exceptions.CotiRunTimeException;
import io.coti.basenode.http.*;
import io.coti.basenode.http.data.TransactionResponseData;
import lombok.experimental.UtilityClass;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.List;

import static io.coti.sdk.utils.Constants.*;

@UtilityClass
public class TransactionUtilities {

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper mapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            .setSerializationInclusion(JsonInclude.Include.NON_NULL);

    public GetTransactionResponse getTransaction(GetTransactionRequest getTransactionRequest, String fullNodeUrl) {
        ResponseEntity<GetTransactionResponse> response = restTemplate.
                postForEntity(fullNodeUrl + TRANSACTION, getTransactionRequest, GetTransactionResponse.class);
        if (response.getStatusCode().equals(HttpStatus.OK) && response.getBody() != null) {
            return response.getBody();
        } else {
            throw new CotiRunTimeException("The call to get Transaction Details failed");
        }
    }

    public GetTransactionsResponse getNoneIndexedTransactions(String fullNodeUrl) {
        ResponseEntity<GetTransactionsResponse> response = restTemplate.
                getForEntity(fullNodeUrl + TRANSACTION_NON_INDEXED, GetTransactionsResponse.class);
        if (response.getStatusCode().equals(HttpStatus.OK) && response.getBody() != null) {
            return response.getBody();
        } else {
            throw new CotiRunTimeException("The call to get None-Indexed Transactions failed");
        }
    }

    public List<TransactionResponseData> getAddressTransactionBatch(GetAddressTransactionBatchRequest getAddressTransactionBatchRequest, String fullNodeUrl) throws JsonProcessingException {
        ResponseEntity<String> response = restTemplate.
                postForEntity(fullNodeUrl + ADDRESS_TRANSACTIONS_BATCH, getAddressTransactionBatchRequest, String.class);
        if (response.getStatusCode().equals(HttpStatus.OK) && response.getBody() != null) {
            return mapper.readValue(response.getBody(), new TypeReference<List<TransactionResponseData>>() {
            });
        } else {
            throw new CotiRunTimeException("The call to get Address Transaction Batch failed");
        }
    }

    public List<TransactionResponseData> getTransactionsHistoryByTimeStamp(GetAddressTransactionBatchByTimestampRequest getAddressTransactionBatchByTimestampRequest, String fullNodeUrl) throws JsonProcessingException {
        ResponseEntity<String> response = restTemplate.
                postForEntity(fullNodeUrl + ADDRESS_TRANSACTIONS_TIMESTAMP_BATCH, getAddressTransactionBatchByTimestampRequest, String.class);
        if (response.getStatusCode().equals(HttpStatus.OK) && response.getBody() != null) {
            return mapper.readValue(response.getBody(), new TypeReference<List<TransactionResponseData>>() {
            });
        } else {
            throw new CotiRunTimeException("The call to get Transactions History By Time Stamp failed");
        }
    }
}
