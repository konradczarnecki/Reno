package konra.reno.crypto;

import konra.reno.util.KeysDto;

public interface CryptoEngine {

    String encryptHexSymmetric(String key, String value);
    String decryptHexSymmetric(String key, String encrypted);
    KeysDto keyPair();
    boolean testKeys(String publicKey, String privateKey);
    String encryptHexAsymmetric(String publicKey, String value);
    String decryptHexAsymmetric(String privateKey, String encrypted);
    String hash(String raw);
    String sign(String privateKey, String hash);
    boolean verifySignature(String signature, String publicKey, String hash);
}
