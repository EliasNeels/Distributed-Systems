import java.io.FileInputStream;
import java.lang.classfile.Signature;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import javax.crypto.Cipher;



void main() {

        String message = "This is a top secret message.";
        String pathStoreA = "src/storeA.jks";
        String pathStoreB = "src/storeB.jks";

        char[] storeAPass = "A".toCharArray();
        char[] storeBPass = "B".toCharArray();
        char[] keyPassA = "A".toCharArray();
        char[] keyPassB = "B".toCharArray();

        String aliasA = "a";
        String aliasB = "b";

        byte[] AliasA = aliasA.getBytes();
        byte[] AliasB = aliasB.getBytes();
//Sender Side:
        System.out.println("--- Sender Side ---\n");
//Step 1:
// Hash the message
    try {
        byte[] messageBytes = message.getBytes();
        byte[] hm = Hashing.hash(messageBytes);
        //To see if it actually works, print the hash in hex
        System.out.println("Original Message: " + message);
        System.out.println("Hashed Message (hm): " + Hashing.bytesToHex(hm));

//Sign the hash with A's private key
        KeyStore ksA = KeyStore.getInstance("JKS");
        KeyStore ksB = KeyStore.getInstance("JKS");
        try (FileInputStream fisA = new FileInputStream(pathStoreA);
             FileInputStream fisB = new FileInputStream(pathStoreB)) {
            ksA.load(fisA, storeAPass);
            ksB.load(fisB, storeBPass);
        }

        PrivateKey privateKeyA = (PrivateKey) ksA.getKey(aliasA, keyPassA);
        if (privateKeyA == null) {
            throw new IllegalStateException("Private key for alias " + aliasA + " not found in " + "`" + pathStoreA + "`");
        }
        byte[] sig = DigitalSignature.sign(hm, privateKeyA);
        //To see if it actually works, print the signature in Base64
        System.out.println("Digital Signature: " + DigitalSignature.bytesToBase64(sig));


//Step 2:
// Secure random number generator for K
    SymmetricEncryption senderSym = new SymmetricEncryption();
    //To see if it actually works, get the key bytes and print them
    byte[] K = senderSym.getKeyBytes();
    System.out.println("Session Key K: " + Hashing.bytesToHex(K));

//Step 3:
    // Encrypt the message, signature and key information with K
        System.out.println("Message Bytes: " + Hashing.bytesToHex(messageBytes));
        System.out.println("Signature Bytes: " + Hashing.bytesToHex(sig));
        System.out.println("Sender Alias: " + aliasA);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (DataOutputStream dos = new DataOutputStream(baos)) {
            dos.writeInt(messageBytes.length);
            dos.write(messageBytes);

            dos.writeInt(sig.length);
            dos.write(sig);

            dos.writeInt(AliasA.length);
            dos.write(AliasA);

            dos.flush();
            dos.close();
        }
        byte[] plaintextPart1 = baos.toByteArray();
        baos.close();
        byte[] part1 = senderSym.SymmetricEncrypt(plaintextPart1);

        System.out.println("PART1 (encrypted with K): " + Hashing.bytesToHex(part1));

//Step 4:
    // Encrypt K with B's public key
        Certificate certB = ksA.getCertificate(aliasB);
        if (certB == null) {
            throw new IllegalStateException("Certificate for alias " + aliasB + " not found in " + "`" + pathStoreB + "`");
        }
        PublicKey publicKeyB = certB.getPublicKey();
        AsymmetricEncryption senderAsym = new AsymmetricEncryption(privateKeyA, publicKeyB);
        byte[] part2 = senderAsym.AsymmetricEncrypt(K);
        System.out.println("PART2 (K encrypted with B's public key): " + Hashing.bytesToHex(part2));

//Receiver Side:
        System.out.println("\n--- Receiver Side ---\n");
//Step 1:
    // Decrypt K with B's private key
        Certificate certA = ksB.getCertificate(aliasA);
        PublicKey publicKeyA = certA.getPublicKey();
        PrivateKey privateKeyB = (PrivateKey) ksB.getKey(aliasB, keyPassB);
        if (privateKeyB == null) {
            throw new IllegalStateException("Private key for alias " + aliasB + " not found in " + "`" + pathStoreB + "`");
        }
        AsymmetricEncryption receiverAsym = new AsymmetricEncryption(privateKeyB, publicKeyA);
        byte[] decryptedK = receiverAsym.AsymmetricDecryptBytes(part2);
        System.out.println("Decrypted Session Key K: " + Hashing.bytesToHex(decryptedK));

//Step 2:
    // Decrypt the message, signature and key information with K
        SecretKey decryptedKeyK = new SecretKeySpec(decryptedK, "AES");
        SymmetricEncryption receiverSym = new SymmetricEncryption(decryptedKeyK);
        byte[] decryptedPart1 = receiverSym.SymmetricDecryptBytes(part1);

        ByteArrayInputStream bais = new ByteArrayInputStream(decryptedPart1);
        DataInputStream dis = new DataInputStream(bais);
        //Read message
        int messageLength = dis.readInt();
        byte[] receivedMessageBytes = new byte[messageLength];
        dis.readFully(receivedMessageBytes);
        //Read signature
        int sigLength = dis.readInt();
        byte[] receivedSig = new byte[sigLength];
        dis.readFully(receivedSig);
        //Read alias
        int aliasLength = dis.readInt();
        byte[] receivedAliasBytes = new byte[aliasLength];
        dis.readFully(receivedAliasBytes);

        String receivedAlias = new String(receivedAliasBytes);
        dis.close();
        System.out.println("Received Message Bytes: " + Hashing.bytesToHex(receivedMessageBytes));
        System.out.println("Received Signature Bytes: " + Hashing.bytesToHex(receivedSig));
        System.out.println("Received Alias: " + receivedAlias);

//Step 3:
    // Check if the message and signiture matches
        System.out.println("Received Signature: " + DigitalSignature.bytesToBase64(receivedSig));
        byte[] receivedHash = Hashing.hash(receivedMessageBytes);
        System.out.println("Received Hashed Message: " + Hashing.bytesToHex(receivedHash));
        System.out.println("Received Message: " + new String(receivedMessageBytes));

//Step 4:
    // Verify the signature with A's public key
        boolean isVerified = DigitalSignature.verify(receivedHash, receivedSig, publicKeyA);
        System.out.println("Signature Verified: " + isVerified);



    } catch (UnrecoverableKeyException e) {
        System.err.println("Failed to retrieve a key: invalid key password.");
        e.printStackTrace();
    } catch (CertificateException e) {
        System.err.println("Certificate problem while loading keystore.");
        e.printStackTrace();
    } catch (IOException e) {
        System.err.println("I/O error (file not found or unreadable): " + e.getMessage());
        e.printStackTrace();
    } catch (IllegalStateException e) {
        System.err.println("Configuration error: " + e.getMessage());
        e.printStackTrace();
    } catch (Exception e) {
        System.err.println("Unexpected error:");
        e.printStackTrace();
    }
}