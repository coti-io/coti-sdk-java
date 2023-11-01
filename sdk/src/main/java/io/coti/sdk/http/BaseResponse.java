package io.coti.sdk.http;

import io.coti.sdk.base.*;
import lombok.Data;

import java.io.Serializable;

@Data
public class BaseResponse implements Serializable {

    protected String status;

    protected BaseResponse() {
        this.status = BaseNodeHttpStringConstants.STATUS_SUCCESS;
    }

    protected BaseResponse(String status) {
        this.status = status;
    }
}
