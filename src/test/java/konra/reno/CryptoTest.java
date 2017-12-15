package konra.reno;

import org.junit.Before;
import static org.junit.Assert.*;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.UnsupportedEncodingException;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

@SpringBootTest
public class CryptoTest {

    private static final Logger log = LoggerFactory.getLogger(CryptoTest.class);

    @Test
    public void testAESBase64(){

        String secretMessage = "secretMsgsecretMsg";
        String key = "16letterString00";

        String encoded = Crypto.encAESBase64(key, secretMessage);
        String decoded = Crypto.decAESBase64(key, encoded);

        log.info(encoded);

        assertEquals(secretMessage, decoded);
    }

    @Test
    public void testRSA() throws Exception {

        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        KeyPair keys512 = Crypto.keyPair(512);

        String publicKey = Base64.getEncoder().encodeToString(keys512.getPublic().getEncoded());
        String privateKey = Base64.getEncoder().encodeToString(keys512.getPrivate().getEncoded());

        byte[] publicBytes = Base64.getDecoder().decode(publicKey);
        X509EncodedKeySpec pubSpec = new X509EncodedKeySpec(publicBytes);
        PublicKey pubKey = keyFactory.generatePublic(pubSpec);

        byte[] privateBytes = Base64.getDecoder().decode(privateKey);
        PKCS8EncodedKeySpec prvSpec = new PKCS8EncodedKeySpec(privateBytes);
        PrivateKey prvKey = keyFactory.generatePrivate(prvSpec);

        String message = "message";
        byte[] encrypted = Crypto.encryptRSA(pubKey, message);
        String decrypted = Crypto.decryptRSA(prvKey, encrypted);

        assertEquals(message, decrypted);
    }
}
