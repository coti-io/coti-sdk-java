package io.coti.sdk.base;


public class TransactionException extends RuntimeException {

    public TransactionException(Exception e) {
        super(e);
    }

    public TransactionException(String message) {
        super(message);
    }
}

