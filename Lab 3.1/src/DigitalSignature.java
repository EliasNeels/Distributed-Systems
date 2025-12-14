import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.util.Base64;

public class DigitalSignature {

    private static final String SIGNATURE_ALGORITHM = "SHA256withRSA";

    public static byte[] sign(byte[] data, PrivateKey privateKey) {
        try {
            Signature signature = Signature.getInstance(SIGNATURE_ALGORITHM);
            signature.initSign(privateKey);
            signature.update(data);
            return signature.sign();
        } catch (Exception e) {
            throw new RuntimeException("Signing failed: " + e.getLocalizedMessage());
        }
    }

    public static boolean verify(byte[] data, byte[] signatureToVerify, PublicKey publicKey) {
        try {
            Signature signature = Signature.getInstance(SIGNATURE_ALGORITHM);
            signature.initVerify(publicKey);
            signature.update(data);
            return signature.verify(signatureToVerify);
        } catch (Exception e) {
            throw new RuntimeException("Verifying failed: " + e.getLocalizedMessage());
        }
    }

    public static String bytesToBase64(byte[] bytes) {
        return Base64.getEncoder().encodeToString(bytes);
    }
}