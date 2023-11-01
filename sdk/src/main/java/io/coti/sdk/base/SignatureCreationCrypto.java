package io.coti.sdk.base;



public interface SignatureCreationCrypto<T extends ISignable> {

    byte[] getSignatureMessage(T signable);

    default void signMessage(T signable) {
        signable.setSignerHash(nodeIdentityService.getNodeHash());
        signable.setSignature(nodeIdentityService.signMessage(this.getSignatureMessage(signable)));
    }

    default void signMessage(T signable, Hash publicKey, String privateKey) {
        signable.setSignerHash(publicKey);
        signable.setSignature(CryptoHelper.signBytes(this.getSignatureMessage(signable), privateKey));
    }

    static INodeIdentityService nodeIdentityService = null;
}
