package konra.reno.crypto;

import konra.reno.util.KeysDto;
import lombok.AccessLevel;
import lombok.Data;
import lombok.SneakyThrows;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

/**
 * Basic cryptography implementation. AES, RSA, SHA256, SHA256withRSA
 */
@Service
@FieldDefaults(level = AccessLevel.PRIVATE)
@Slf4j
public class SimpleCryptoEngine implements CryptoEngine {

    private static String initVector = "24cd5de2a352a857";

    CryptoConfig config;

    @Autowired
    public SimpleCryptoEngine(CryptoConfig config) {
        this.config = config;
    }

    @Override
    @SneakyThrows
    public String encryptHexSymmetric(String key, String value) {

        IvParameterSpec iv = new IvParameterSpec(initVector.getBytes("UTF-8"));
        SecretKeySpec keySpec = new SecretKeySpec(key.getBytes("UTF-8"), "AES");

        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
        cipher.init(Cipher.ENCRYPT_MODE, keySpec, iv);
        byte[] encrypted = cipher.doFinal(value.getBytes());

        return DatatypeConverter.printHexBinary(encrypted);
    }

    @Override
    @SneakyThrows
    public String decryptHexSymmetric(String key, String encrypted) {

        IvParameterSpec iv = new IvParameterSpec(initVector.getBytes("UTF-8"));
        SecretKeySpec keySpec = new SecretKeySpec(key.getBytes("UTF-8"), "AES");

        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
        cipher.init(Cipher.DECRYPT_MODE, keySpec, iv);
        byte[] original = cipher.doFinal(DatatypeConverter.parseHexBinary(encrypted));

        return new String(original);
    }

    @Override
    @SneakyThrows
    public KeysDto keyPair() {

        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        keyPairGenerator.initialize(config.getKeySize());
        KeyPair pair = keyPairGenerator.generateKeyPair();

        KeysDto keys = new KeysDto();
        keys.setPublicKey(DatatypeConverter.printHexBinary(pair.getPublic().getEncoded()));
        keys.setPrivateKey(DatatypeConverter.printHexBinary(pair.getPrivate().getEncoded()));
        return keys;
    }

    @Override
    public boolean testKeys(String publicKey, String privateKey) {

        String testSign = sign(privateKey, "test");
        return verifySignature(testSign, publicKey, "test");
    }

    @Override
    @SneakyThrows
    public String encryptHexAsymmetric(String publicKey, String message) {

        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.ENCRYPT_MODE, getPublicKey(publicKey));
        return DatatypeConverter.printHexBinary(cipher.doFinal(message.getBytes()));
    }

    @Override
    @SneakyThrows
    public String decryptHexAsymmetric(String privateKey, String encrypted) {

        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.DECRYPT_MODE, getPrivateKey(privateKey));
        return new String(cipher.doFinal(DatatypeConverter.parseHexBinary(encrypted)));
    }

    @Override
    @SneakyThrows
    public String hash(String raw) {

        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        return DatatypeConverter.printHexBinary(digest.digest(raw.getBytes(StandardCharsets.UTF_8)));
    }

    @Override
    @SneakyThrows
    public String sign(String privateKey, String hash) {

        Signature sig = Signature.getInstance("SHA256withRSA");
        sig.initSign(getPrivateKey(privateKey));
        sig.update(hash.getBytes());
        return DatatypeConverter.printHexBinary(sig.sign());
    }

    @Override
    @SneakyThrows
    public boolean verifySignature(String signature, String publicKey, String hash) {

        Signature sig = Signature.getInstance("SHA256withRSA");
        sig.initVerify(getPublicKey(publicKey));
        sig.update(hash.getBytes());
        return sig.verify(DatatypeConverter.parseHexBinary(signature));
    }

    @SneakyThrows
    private PublicKey getPublicKey(String hexKey) {

        KeyFactory kf = KeyFactory.getInstance("RSA");
        byte[] encoded = DatatypeConverter.parseHexBinary(hexKey);
        X509EncodedKeySpec keySpecPb = new X509EncodedKeySpec(encoded);
        return kf.generatePublic(keySpecPb);
    }

    @SneakyThrows
    private PrivateKey getPrivateKey(String hexKey) {

        KeyFactory kf = KeyFactory.getInstance("RSA");
        byte[] encoded = DatatypeConverter.parseHexBinary(hexKey);
        PKCS8EncodedKeySpec keySpecPv = new PKCS8EncodedKeySpec(encoded);
        return kf.generatePrivate(keySpecPv);
    }
}
