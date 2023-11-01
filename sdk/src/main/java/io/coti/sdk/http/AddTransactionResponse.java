package io.coti.sdk.http;

import io.coti.sdk.base.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NonNull;

import java.time.Instant;

@Data
@EqualsAndHashCode(callSuper = true)
public class AddTransactionResponse extends BaseResponse {

    @NonNull
    private String message;
    @NonNull
    private Instant attachmentTime;

    public AddTransactionResponse() {
        this.message = "";
        this.attachmentTime = Instant.now();
    }
}
