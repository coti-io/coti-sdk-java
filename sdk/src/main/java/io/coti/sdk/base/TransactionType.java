package io.coti.sdk.base;


@SuppressWarnings("java:S115")
public enum TransactionType {
    Initial,
    Payment,
    Transfer,
    ZeroSpend,
    Chargeback,
    TokenGeneration,
    TokenMinting,
    EventHardFork
}
