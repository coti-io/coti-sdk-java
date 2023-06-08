package io.coti.sdk.utils;

import io.coti.basenode.http.CustomHttpComponentsClientHttpRequestFactory;
import lombok.experimental.UtilityClass;
import org.springframework.web.client.RestTemplate;

@UtilityClass
public class Constants {

    public static final RestTemplate restTemplate = new RestTemplate(new CustomHttpComponentsClientHttpRequestFactory());
    public static final String NATIVE_CURRENCY_SYMBOL = "COTI";
    public static final String BALANCE = "/balance";
    public static final String NETWORK_FEE = "/networkFee";
    public static final String FULL_NODE_FEE = "/fee";
    public static final String NODES = "/nodes";
    public static final String TRANSACTION = "/transaction";
    public static final String TRANSACTION_NON_INDEXED = "/transaction/none-indexed";
    public static final String ADDRESS_TRANSACTIONS_BATCH = "/transaction/addressTransactions/batch";
    public static final String ADDRESS_TRANSACTIONS_TIMESTAMP_BATCH = "/transaction/addressTransactions/timestamp/batch";
    public static final String TRANSACTION_TRUST_SCORE = "/transactiontrustscore";
    public static final String USER_TRUST_SCORE = "/usertrustscore";
    public static final String ADDRESS = "/address";
    public static final String EVENT_MULTI_DAG_CONFIRMED = "/event/multi-dag/confirmed";
    public static final String TOKEN_GENERATE = "/admin/token/generate";
    public static final String USER_TOKENS = "/currencies/token/user";
    public static final String TOKEN_DETAILS = "/currencies/token/details";
    public static final String TOKEN_SYMBOL_DETAILS = "/currencies/token/symbol/details";
    public static final String TOKEN_HISTORY = "/currencies/token/history";
    public static final String TOKEN_BALANCES = "/balance/tokens";
    public static final String TOKEN_MINT_QUOTE = "/admin/token/mint/quote";
    public static final String TOKEN_MINT_FEE = "/admin/token/mint/fee";
    public static final String INSUFFICIENT_FUNDS_MESSAGE = "Address balance (%s) is insufficient (%s) !";
    public static final String INSUFFICIENT_TRUST_SCORE_MESSAGE = "Trust score for this user is insufficient!";
    public static final int ZERO = 0;
    public static final int BASE_TRANSACTION_HASH_SIZE = 32;
    public static final String ERROR = "Error";
}
