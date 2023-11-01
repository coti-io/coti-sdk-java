package io.coti.sdk.base;


public class BalanceException extends CotiRunTimeException {

    public BalanceException(String message) {
        super(message);
    }

    public BalanceException(String message, Throwable cause) {
        super(message, cause);
    }
}
