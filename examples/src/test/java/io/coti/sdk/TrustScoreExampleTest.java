package io.coti.sdk;

import io.coti.basenode.crypto.CryptoHelper;
import io.coti.basenode.data.Hash;
import io.coti.basenode.http.GetUserTrustScoreResponse;
import io.coti.sdk.utils.CryptoUtils;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.junit.jupiter.api.Test;

import java.io.InputStream;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class TrustScoreExampleTest {

    public static void main(String[] args) throws Exception {
        TrustScoreExampleTest trustScoreExampleTest = new TrustScoreExampleTest();
        trustScoreExampleTest.userTrustScoreTest();
    }

    @Test
    void userTrustScoreTest() throws Exception {
        //Loading properties from the properties file
        PropertiesConfiguration config = new PropertiesConfiguration();
        InputStream transferInput = TransferExampleTest.class.getClassLoader().getResourceAsStream("transfer.properties");
        config.load(transferInput);
        String seed = config.getString("seed");
        if (seed == null || seed.equals("")) {
            seed = System.getenv("TESTNET_SEED");
            if (seed == null || seed.equals("")) {
                throw new Exception("seed needed");
            }
        }
        String userPrivateKey = CryptoUtils.getPrivateKeyFromSeed((new Hash(seed).getBytes())).toHexString();
        Hash userHash = new Hash(CryptoHelper.getPublicKeyFromPrivateKey(userPrivateKey));
        String trustScoreAddress = config.getString("trust.score.backend.address");

        TrustScoreUtilities trustScoreData = new TrustScoreUtilities(trustScoreAddress);
        GetUserTrustScoreResponse response = trustScoreData.getUserTrustScore(userHash);

        System.out.println("User Hash: " + response.getUserHash());
        System.out.println("User type: " + response.getUserType());
        System.out.println("User trust score = " + response.getTrustScore());

        assertThat(response.getTrustScore()).isGreaterThan(10);
    }
}
