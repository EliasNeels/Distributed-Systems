import javax.crypto.Cipher;
import java.security.*;
import java.util.Base64;

public class AsymmetricEncryption {

    private static final String ALGORITHM = "RSA";
    private static final String TRANSFORMATION = "RSA/ECB/PKCS1Padding";
    private static final int KEY_SIZE = 2048;

    private PrivateKey privateKey;
    private PublicKey publicKey;

    public AsymmetricEncryption() {
        try {
            KeyPairGenerator kg = KeyPairGenerator.getInstance(ALGORITHM);
            kg.initialize(KEY_SIZE);
            KeyPair pair = kg.generateKeyPair();

            this.privateKey = pair.getPrivate();
            this.publicKey = pair.getPublic();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("RSA algorithm not supported: " + e.getLocalizedMessage());
        }
    }

    public AsymmetricEncryption(PrivateKey privateKey, PublicKey publicKey) {
        this.privateKey = privateKey;
        this.publicKey = publicKey;
    }

    public PublicKey getPublicKey() {
        return publicKey;
    }

    public PrivateKey getPrivateKey() {
        return privateKey;
    }

    public String getPublicKeyPem() {
        String base64 = Base64.getEncoder().encodeToString(publicKey.getEncoded());
        return "-----BEGIN PUBLIC KEY-----\n"
                + base64.replaceAll("(.{64})", "$1\n")
                + "\n-----END PUBLIC KEY-----";
    }

    // Encrypteer met publieke sleutel
    public byte[] AsymmetricEncrypt(String plaintext) {
        try {
            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);
            return cipher.doFinal(plaintext.getBytes());
        } catch (Exception e) {
            throw new RuntimeException("Encryption failed: " + e.getLocalizedMessage());
        }
    }

    public byte[] AsymmetricEncrypt(byte[] plaintext) {
        try {
            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);
            return cipher.doFinal(plaintext);
        } catch (Exception e) {
            throw new RuntimeException("Encryption failed: " + e.getLocalizedMessage());
        }
    }


    public String AsymmetricDecrypt(byte[] ciphertext) {
        try {
            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            cipher.init(Cipher.DECRYPT_MODE, privateKey);
            byte[] decryptedBytes = cipher.doFinal(ciphertext);
            return new String(decryptedBytes);
        } catch (Exception e) {
            throw new RuntimeException("Decryption failed: " + e.getLocalizedMessage());
        }
    }

    public byte[] AsymmetricDecryptBytes(byte[] ciphertext) {
        try {
            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            cipher.init(Cipher.DECRYPT_MODE, privateKey);
            byte[] decryptedBytes = cipher.doFinal(ciphertext);
            return decryptedBytes;
        } catch (Exception e) {
            throw new RuntimeException("Decryption failed: " + e.getLocalizedMessage());
        }
    }


}
