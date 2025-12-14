import java.io.FileInputStream;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.Certificate;
import javax.crypto.Cipher;

public class LabKeyStoring {

    public static void main(String[] args) {
        try {
            String pathStore1 = "src/store1.jks";
            String pathStore2 = "src/store2.jks";

            char[] store1Pass = "Werner".toCharArray();
            char[] store2Pass = null;
            char[] keyPass = "".toCharArray();


            String aliasFreya = "freya"; // Alias used for Freya's key/cert

            System.out.println("Encryption Phase");

            //Load Keystore 1
            KeyStore ks1 = KeyStore.getInstance("JKS");
            FileInputStream fis1 = new FileInputStream(pathStore1);
            ks1.load(fis1, store1Pass);
            fis1.close();

            //Get Freya's Certificate from store1
            Certificate freyaCert = ks1.getCertificate(aliasFreya);
            PublicKey freyaPublic = freyaCert.getPublicKey();
            System.out.println("Retrieved Public Key: " + freyaPublic.getAlgorithm());

            //Encrypt a message with Freya's Public Key
            String originalMessage = "Hello Freya, this is a secret from Werner!";
            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.ENCRYPT_MODE, freyaPublic);
            byte[] encryptedBytes = cipher.doFinal(originalMessage.getBytes());

            System.out.println("Encrypted Data: " + bytesToHex(encryptedBytes));

            //Decryption
            System.out.println("\nDecryption Phase");

            //Load Keystore 2
            KeyStore ks2 = KeyStore.getInstance("JKS");
            FileInputStream fis2 = new FileInputStream(pathStore2);
            ks2.load(fis2, store2Pass);
            fis2.close();

            //Get Freya's Private Key from store2
            PrivateKey freyaPrivate = (PrivateKey) ks2.getKey(aliasFreya, keyPass);

            // 6. Decrypt the message with Freya's Private Key
            cipher.init(Cipher.DECRYPT_MODE, freyaPrivate);
            byte[] decryptedBytes = cipher.doFinal(encryptedBytes);

            String decryptedMessage = new String(decryptedBytes);
            System.out.println("Decrypted Message: " + decryptedMessage);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Helper method to make byte arrays readable
    private static String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02X", b));
        }
        return sb.toString();
    }
}

