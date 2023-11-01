package io.coti.sdk.base;



import java.math.BigDecimal;

public interface ITokenFeeData extends IEntity {

    String toString();

    BigDecimal getFeeAmount(BigDecimal amount);

    boolean valid();
}
