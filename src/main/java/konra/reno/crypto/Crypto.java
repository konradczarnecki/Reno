package konra.reno.crypto;

import konra.reno.util.KeysDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Static wrapper for cryptographic engine.
 */
@Service
public class Crypto {

    private static CryptoEngine engine;

    @Autowired
    public Crypto(CryptoEngine engine) {
        Crypto.engine = engine;
    }

    public static String encryptHexSymmetric(String key, String value) {
        return engine.encryptHexSymmetric(key, value);
    }

    public static String decryptHexSymmetric(String key, String encrypted) {
        return engine.decryptHexSymmetric(key, encrypted);
    }

    public static KeysDto keyPair() {
        return engine.keyPair();
    }

    public static boolean testKeys(String publicKey, String privateKey) {
        return engine.testKeys(publicKey, privateKey);
    }

    public static String encryptHexAsymmetric(String publicKey, String value) {
        return engine.encryptHexAsymmetric(publicKey, value);
    }

    public static String decryptHexAsymmetric(String privateKey, String encrypted) {
        return engine.decryptHexAsymmetric(privateKey, encrypted);
    }

    public static String sign(String privateKey, String hash) {
        return engine.sign(privateKey, hash);
    }

    public static boolean verifySignature(String signature, String publicKey, String hash) {
        return engine.verifySignature(signature, publicKey, hash);
    }

    public static String hash(String raw) {
        return engine.hash(raw);
    }

}
