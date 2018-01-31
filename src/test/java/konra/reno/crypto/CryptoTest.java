package konra.reno.crypto;

import static org.junit.Assert.*;

import konra.reno.util.KeysDto;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@SpringBootTest
@RunWith(SpringRunner.class)
public class CryptoTest {

    private static final Logger log = LoggerFactory.getLogger(CryptoTest.class);

    @Test
    public void testAES(){

        String secretMessage = "secretMsgsecretMsg";
        String key = "16letterString00";

        String encoded = Crypto.encryptHexSymmetric(key, secretMessage);
        String decoded = Crypto.decryptHexSymmetric(key, encoded);

        assertEquals(secretMessage, decoded);
    }

    @Test
    public void testRSA() {

        KeysDto keys = Crypto.keyPair();

        String message = "message";
        String encrypted = Crypto.encryptHexAsymmetric(keys.getPublicKey(), message);
        String decrypted = Crypto.decryptHexAsymmetric(keys.getPrivateKey(), encrypted);

        assertEquals(message, decrypted);
    }

    @Test
    public void testSignature() {

        KeysDto keys = Crypto.keyPair();

        String message = "msg";
        String signature =  Crypto.sign(keys.getPrivateKey(), message);

        assertTrue(Crypto.verifySignature(signature, keys.getPublicKey(), message));
    }
}
