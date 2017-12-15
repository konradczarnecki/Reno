package konra.reno;

import java.security.KeyPair;

public class KeysDto {

    private String publicKey;
    private String privateKey;

    public KeysDto() {
    }

    public KeysDto(String publicKey, String privateKey){

        this.publicKey = publicKey;
        this.privateKey = privateKey;
    }

    public KeysDto(KeyPair keys){

        try {

            this.publicKey = new String(keys.getPublic().getEncoded(), "UTF-8");
            this.privateKey = new String(keys.getPrivate().getEncoded(), "UTF-8");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(String publicKey) {
        this.publicKey = publicKey;
    }

    public String getPrivateKey() {
        return privateKey;
    }

    public void setPrivateKey(String privateKey) {
        this.privateKey = privateKey;
    }
}
