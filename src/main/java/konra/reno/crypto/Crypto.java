package konra.reno.crypto;

import konra.reno.util.KeysDto;
import lombok.SneakyThrows;
import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;
import java.nio.charset.StandardCharsets;
import java.security.*;

@Service
public class Crypto {

    private static CryptoEngine engine;

    @Autowired
    public Crypto(CryptoEngine engine) {
        Crypto.engine = engine;
    }

    public static String encryptHexSymetric(String key, String value) {
        return engine.encryptHexSymetric(key, value);
    }

    public static String decryptHexSymetric(String key, String encrypted) {
        return engine.decryptHexSymetric(key, encrypted);
    }

    public static KeysDto keyPair() {
        return engine.keyPair();
    }

    public static String encryptHexAsymetric(String publicKey, String value) {
        return engine.encryptHexAsymetric(publicKey, value);
    }

    public static String decryptHexAsymetric(String privateKey, String encrypted) {
        return engine.decryptHexAsymetric(privateKey, encrypted);
    }

    public String sign(String privateKey, String hash){
        return engine.sign(privateKey, hash);
    }

    public static String hash(String raw){
        return engine.hash(raw);
    }

}
