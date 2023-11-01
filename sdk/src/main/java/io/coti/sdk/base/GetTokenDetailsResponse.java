package io.coti.sdk.base;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class GetTokenDetailsResponse extends BaseResponse {

    private TokenResponseData token;

}
