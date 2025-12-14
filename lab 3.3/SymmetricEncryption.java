import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;


public class SymmetricEncryption {
    private static final String ALGORITHM = "AES";
    private static final String TRANSFORMATION = "AES/ECB/PKCS5Padding";
    private static final int KEY_SIZE = 128;

    private final SecretKey key;


    public SymmetricEncryption() {
        try {
            KeyGenerator kg = KeyGenerator.getInstance(ALGORITHM);
            SecureRandom srng = new SecureRandom();
            kg.init(KEY_SIZE, srng);
            this.key = kg.generateKey();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("AES algorithm not supported: " + e.getLocalizedMessage());
        }
    }

    public SymmetricEncryption(SecretKey key) {
        this.key = key;
    }

    public byte[] SymmetricEncrypt(byte[] plaintext) {
        try {
            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            cipher.init(Cipher.ENCRYPT_MODE, key);
            byte[] ciphertext = cipher.doFinal(plaintext);
            return ciphertext;
        } catch (Exception e) {
            throw new RuntimeException("Encryption failed: " + e.getLocalizedMessage());
        }
    }

    public String SymmetricDecrypt(byte[] ciphertext) {
        try {
            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            cipher.init(Cipher.DECRYPT_MODE, key);
            byte[] decryptedBytes = cipher.doFinal(ciphertext);
            return new String(decryptedBytes, StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new RuntimeException("Decryption failed: " + e.getLocalizedMessage());
        }
    }

    public byte[] SymmetricDecryptBytes(byte[] ciphertext) {
        try {
            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            cipher.init(Cipher.DECRYPT_MODE, key);
            byte[] decryptedBytes = cipher.doFinal(ciphertext);
            return decryptedBytes;
        } catch (Exception e) {
            throw new RuntimeException("Decryption failed: " + e.getLocalizedMessage());
        }
    }

    public byte[] getKeyBytes() {
        return key.getEncoded();
    }
}
