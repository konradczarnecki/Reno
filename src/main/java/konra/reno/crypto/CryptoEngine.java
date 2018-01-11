package konra.reno.crypto;

import konra.reno.util.KeysDto;

public interface CryptoEngine {

    String encryptHexSymetric(String key, String value);
    String decryptHexSymetric(String key, String encrypted);
    KeysDto keyPair();
    String encryptHexAsymetric(String publicKey, String value);
    String decryptHexAsymetric(String privateKey, String encrypted);
    String hash(String raw);
    String sign(String privateKey, String hash);
    boolean checkSignature(String signature, String publicKey);
}
