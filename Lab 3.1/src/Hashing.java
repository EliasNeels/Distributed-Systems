import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Hashing {

    static byte[] hash(byte[] data) {
        byte[] hash = null;
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            hash = digest.digest(data);
        } catch (NoSuchAlgorithmException e) {
            System.err.println(e.getLocalizedMessage());
        }
        return hash;
    }

    static String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder(bytes.length * 2);
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }
}
