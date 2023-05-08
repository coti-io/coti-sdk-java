package io.coti.sdk.data;

import io.coti.basenode.crypto.CryptoHelper;
import io.coti.basenode.data.Hash;
import io.coti.sdk.utils.CryptoUtils;
import lombok.Data;

@Data
public class WalletDetails {

    private String fullNodeAddress;
    private String trustScoreAddress;
    private int walletAddressIndex;
    private Hash nativeCurrencyHash;
    private Hash userPrivateKey;
    private Hash senderHash;
    private Hash addressHash;
    private String seed;

    public WalletDetails(String seed, String trustScoreAddress, String fullNodeAddress, int walletAddressIndex, Hash nativeCurrencyHash) {
        this.seed = seed;
        this.trustScoreAddress = trustScoreAddress;
        this.fullNodeAddress = fullNodeAddress;
        this.walletAddressIndex = walletAddressIndex;
        this.nativeCurrencyHash = nativeCurrencyHash;
        this.addressHash = CryptoHelper.generateAddress(seed, walletAddressIndex);
        this.userPrivateKey = CryptoUtils.getPrivateKeyFromSeed((new Hash(seed).getBytes()));
        this.senderHash = new Hash(CryptoHelper.getPublicKeyFromPrivateKey(userPrivateKey.toHexString()));
    }
}
