package io.coti.sdk;

import com.google.gson.Gson;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpStatusCodeException;
import io.coti.sdk.base.*;
import javax.xml.ws.Response;
import java.io.Serializable;

import static io.coti.sdk.utils.Constants.restTemplate;

@UtilityClass
public class Utilities {

    private void responseIsOK(ResponseEntity<?> response) {
        if (!(response != null && (response.getStatusCode().equals(HttpStatus.OK) || response.getStatusCode().equals(HttpStatus.CREATED)) && response.getBody() != null)) {
            throw new CotiRunTimeException("response validation failed");
        }
    }

//    private void printPerformance(String urlEndpointAddress, long duration) {
//        log.debug(String.format("url: %s, response time: %s (millisecond)", urlEndpointAddress, duration));
//    }

    public ResponseEntity<?> postRequest(String urlEndpointAddress, Serializable request, Class responseClass) {
        long startTime = System.currentTimeMillis();
        ResponseEntity<?> responseEntity;
        try {
            responseEntity = restTemplate.postForEntity(urlEndpointAddress, request, responseClass);
        } catch (HttpStatusCodeException e) {
            throw new NetworkException("error: " + new Gson().fromJson(e.getResponseBodyAsString(), Response.class));
        }
        long endTime = System.currentTimeMillis();
        long duration = (endTime - startTime);
//        printPerformance(urlEndpointAddress, duration);
        responseIsOK(responseEntity);
        return responseEntity;
    }

    public ResponseEntity<?> getRequest(String urlEndpointAddress, Class responseClass) {
        long startTime = System.currentTimeMillis();
        ResponseEntity<?> responseEntity;
        try {
            responseEntity = restTemplate.getForEntity(urlEndpointAddress, responseClass);
        } catch (HttpStatusCodeException e) {
            throw new NetworkException("error: " + new Gson().fromJson(e.getResponseBodyAsString(), Response.class));
        }
        long endTime = System.currentTimeMillis();
        long duration = (endTime - startTime);
//        printPerformance(urlEndpointAddress, duration);
        responseIsOK(responseEntity);
        return responseEntity;
    }

    public ResponseEntity<?> putRequest(String urlEndpointAddress, HttpEntity<?> request, Class responseClass) {
        long startTime = System.currentTimeMillis();
        ResponseEntity<?> responseEntity;
        try {
            responseEntity = restTemplate.exchange(urlEndpointAddress, HttpMethod.PUT, request, responseClass);
        } catch (HttpStatusCodeException e) {
            throw new NetworkException("error: " + new Gson().fromJson(e.getResponseBodyAsString(), Response.class));
        }
        long endTime = System.currentTimeMillis();
        long duration = (endTime - startTime);
//        printPerformance(urlEndpointAddress, duration);
        responseIsOK(responseEntity);
        return responseEntity;
    }


}
