package io.coti.sdk.base;

public interface INodeIdentityService {

    void init();

    Hash getNodeHash();

    SignatureData signMessage(byte[] signatureMessage);

    SignatureData signMessage(byte[] message, Integer index);

    Hash generateAddress(Integer index);

    Hash generateAddress(String seed, Integer index);

    void setSeed(String seed);

}
