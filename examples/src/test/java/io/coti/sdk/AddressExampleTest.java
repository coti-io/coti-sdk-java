package io.coti.sdk;

import io.coti.basenode.crypto.CryptoHelper;
import io.coti.basenode.data.Hash;
import io.coti.basenode.http.AddressesExistsResponse;
import io.coti.basenode.http.data.AddressStatus;
import io.coti.sdk.http.AddAddressResponse;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

public class AddressExampleTest {

    private static String seed;
    private static String fullNodeUrl;

    public static void main(String[] args) throws Exception {
        AddressExampleTest addressExampleTest = new AddressExampleTest();
        addressExampleTest.init();
        addressExampleTest.addAddressToNodeTest();
        addressExampleTest.checkAddressExistsTest();
    }

    @BeforeAll
    static void init() throws Exception {
        //Loading properties from the properties file
        PropertiesConfiguration config = new PropertiesConfiguration();
        InputStream transferInput = TransferExampleTest.class.getClassLoader().getResourceAsStream("transfer.properties");
        config.load(transferInput);
        seed = config.getString("seed");
        if (seed == null || seed.equals("")) {
            seed = System.getenv("TESTNET_SEED");
            if (seed == null || seed.equals("")) {
                throw new Exception("seed needed");
            }
        }
        fullNodeUrl = config.getString("full.node.backend.address");
    }

    @Test
    void checkAddressExistsTest() {
        Hash addressHash1 = CryptoHelper.generateAddress(seed, 1);
        Hash addressHash2 = CryptoHelper.generateAddress(seed, 2);
        List<Hash> addressHashes = Arrays.asList(addressHash1, addressHash2);
        //Check if provided addresses exists
        AddressesExistsResponse addressResponse = AddressUtilities.checkAddressExists(addressHashes, fullNodeUrl);
        addressResponse.getAddresses().forEach((address, status) -> Assertions.assertEquals(true, status));
    }

    @Test
    void addAddressToNodeTest() {
        Hash addressHash = CryptoHelper.generateAddress(seed, 1);
        //Create address if not exists.
        AddAddressResponse addressResponse = AddressUtilities.addAddressToNode(addressHash, fullNodeUrl);
        Assertions.assertEquals(AddressStatus.EXISTS, addressResponse.getAddressStatus());
    }
}
