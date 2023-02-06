package io.coti.sdk;

import io.coti.basenode.crypto.CryptoHelper;
import io.coti.basenode.data.Hash;
import io.coti.basenode.services.BaseNodeSecretManagerService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Random;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@ContextConfiguration(classes = {BaseNodeSecretManagerService.class})
@TestPropertySource(locations = "classpath:encrypt_decrypt.properties")
@SpringBootTest
public class EncryptSecretTest {

    @Autowired
    private BaseNodeSecretManagerService secretManagerService;
    @Value("${secret.private.key.file.name}")
    private String privateKeyFileName;
    @Value("${seed}")
    private String seed;
    @Value("${secret.algorithm}")
    private String algorithm;

    public static void main(String[] args) throws NoSuchPaddingException, IllegalBlockSizeException, IOException, NoSuchAlgorithmException, InvalidKeySpecException, BadPaddingException, InvalidKeyException {

        /*
         * To generate supported private and public keys you can execute:
         * openssl genrsa -out keypair.pem 2048
         * openssl rsa -in keypair.pem -outform DER -pubout -out public.der
         * openssl pkcs8 -topk8 -nocrypt -in keypair.pem -outform DER -out private.der
         */

        String secret = args[0];
        String publicKeyPath = args[1];

        byte[] publicKey = Files.readAllBytes(Paths.get(publicKeyPath));
        String encryptedSecret = CryptoHelper.encryptString(secret, publicKey, "RSA");

        System.out.println(encryptedSecret);
    }

    @Test
    void encryptDecryptTest() throws NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, InvalidKeySpecException, BadPaddingException, InvalidKeyException, IOException {

        Random r = new Random();
        StringBuilder secret = new StringBuilder();
        for (int i = 0; i < 20; i++) {
            char c = (char) (r.nextInt(26) + 'a');
            secret.append(c);
        }

        Hash seed = CryptoHelper.cryptoHash(secret.toString().getBytes());

        KeyPair keyPair = generateKeys(algorithm);

        byte[] publicKey = keyPair.getPublic().getEncoded();
        byte[] privateKey = keyPair.getPrivate().getEncoded();
        try (FileOutputStream fos = new FileOutputStream(privateKeyFileName)) {
            fos.write(privateKey);
        }

        String encryptedSecret = CryptoHelper.encryptString(seed.toHexString(), publicKey, algorithm);
        String decryptedSecret = secretManagerService.decrypt(encryptedSecret);

        assertThat(seed.toString()).contains(decryptedSecret);
    }

    private KeyPair generateKeys(String algorithm) throws NoSuchAlgorithmException {
        KeyPairGenerator keyGen = KeyPairGenerator.getInstance(algorithm);
        keyGen.initialize(2048);
        return keyGen.genKeyPair();
    }

}
