package konra.reno.util;

import lombok.SneakyThrows;
import org.apache.commons.codec.binary.Base64;
import org.springframework.stereotype.Service;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;
import java.nio.charset.StandardCharsets;
import java.security.*;

@Service
public class Crypto {

    private static String initVector = "24cd5de2a352a857";

    public static String encAESHex(String key, String value) {

        try {

            IvParameterSpec iv = new IvParameterSpec(initVector.getBytes("UTF-8"));
            SecretKeySpec keySpec = new SecretKeySpec(key.getBytes("UTF-8"), "AES");

            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
            cipher.init(Cipher.ENCRYPT_MODE, keySpec, iv);

            byte[] encrypted = cipher.doFinal(value.getBytes());

            return DatatypeConverter.printHexBinary(encrypted);

        } catch (Exception ex) {

            ex.printStackTrace();
        }

        return null;
    }

    public static String decAESHex(String key, String encrypted) {

        try {

            IvParameterSpec iv = new IvParameterSpec(initVector.getBytes("UTF-8"));
            SecretKeySpec keySpec = new SecretKeySpec(key.getBytes("UTF-8"), "AES");

            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
            cipher.init(Cipher.DECRYPT_MODE, keySpec, iv);

            byte[] original = cipher.doFinal(DatatypeConverter.parseHexBinary(encrypted));

            return new String(original);

        } catch (Exception ex) {

            ex.printStackTrace();
        }

        return null;
    }

    public static KeyPair keyPair(){

        return keyPair(512);
    }

    @SneakyThrows
    public static KeyPair keyPair(int keySize) {

        KeyPairGenerator keyPairGenerator = null;

        keyPairGenerator = KeyPairGenerator.getInstance("RSA");

        keyPairGenerator.initialize(keySize);
        return keyPairGenerator.genKeyPair();
    }

    public static byte[] encryptRSA(PublicKey publicKey, String message) throws Exception {

        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);

        return cipher.doFinal(message.getBytes());
    }

    public static String decryptRSA(PrivateKey privateKey, byte [] encrypted) throws Exception {

        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.DECRYPT_MODE, privateKey);

        return new String(cipher.doFinal(encrypted));
    }

    public String signB64(byte[] hash){

        return "a";

    }

    @SneakyThrows
    public static byte[] hash(String original){

        MessageDigest digest = null;
        digest = MessageDigest.getInstance("SHA-256");
        return digest.digest(original.getBytes(StandardCharsets.UTF_8));
    }

    public static String hashHex(String original){

        return DatatypeConverter.printHexBinary(hash(original));
    }

    public static String hashString(String original){

        return new String(hash(original));
    }


}
