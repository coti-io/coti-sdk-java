package io.coti.sdk.utils;

import lombok.experimental.UtilityClass;

@UtilityClass
public class Constants {

    public static final String NATIVE_CURRENCY_SYMBOL = "COTI";

    public static final String BALANCE = "/balance";
    public static final String NETWORK_FEE = "/networkFee";
    public static final String FULL_NODE_FEE = "/fee";
    public static final String NODES = "/nodes";
    public static final String TRANSACTION_TRUST_SCORE = "/transactiontrustscore";

    public static final String INSUFFICIENT_FUNDS_MESSAGE = "Balance for address is insufficient!";
    public static final String INSUFFICIENT_TRUST_SCORE_MESSAGE = "Trust score for this user is insufficient!";

    public static final int ZERO = 0;
    public static final int BASE_TRANSACTION_HASH_SIZE = 32;
}
