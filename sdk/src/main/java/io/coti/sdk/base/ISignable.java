package io.coti.sdk.base;


public interface ISignable {
    void setSignerHash(Hash signerHash);

    void setSignature(SignatureData signature);
}
