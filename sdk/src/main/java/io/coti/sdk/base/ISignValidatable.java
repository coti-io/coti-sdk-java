package io.coti.sdk.base;


public interface ISignValidatable {
    SignatureData getSignature();

    Hash getSignerHash();
}
