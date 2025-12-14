import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


public class Main {
    public static void main(String[] args) {

        Person person1 = new Person("John Doe", "New York", "123-456-7890");
        Person person2 = new Person("Jane Smith", "Los Angeles", "098-765-4321");
        Person person1Fake = new Person("John Do", "New York", "123-456-7890");

        //Zet om in serial
        byte[] serialPerson1 = person1.getBytes();
        byte[] serialPerson2 = person2.getBytes();
        byte[] serialPerson1Fake = person1Fake.getBytes();

//Hashing

        byte[] hashPerson1 = Hashing.hash(serialPerson1);
        byte[] hashPerson2 = Hashing.hash(serialPerson2);
        byte[] hashPerson1Fake = Hashing.hash(serialPerson1Fake);
        System.out.println("\nHashing:");
        System.out.println("Hash person1: " + Hashing.bytesToHex(hashPerson1));
        System.out.println("Hash person2: " + Hashing.bytesToHex(hashPerson2));
        System.out.println("Hash person1Fake: " + Hashing.bytesToHex(hashPerson1Fake));
//-------------------------------------------------------------------------------------------------------------------------------------------------------
        //Print de hashes van de byte array als een String object op het scherm en vergelijk ze.
        //Wat merk je op?

        //We kunnen zien dat zelfs een kleine verandering in de gegevens van person1 (zoals het verwijderen van een letter in de naam)
        //resulteert in een volledig andere hashwaarde voor person1Fake. Dit toont de gevoeligheid van hashfuncties voor kleine wijzigingen in de invoer.
        //Ook in vergelijking met persoon2, die volledig andere gegevens heeft, is de hashwaarde totaal verschillend.
//-------------------------------------------------------------------------------------------------------------------------------------------------------


//Symetrische encryptie
//-------------------------------------------------------------------------------------------------------------------------------------------------------
        //Wat zijn de mogelijke sleutellengtes voor het AES en het tripleDES algoritme?

        //AES ondersteunt sleutellengtes van 128, 192 en 256 bits.
        //TripleDES ondersteunt sleutellengtes van 112 en 168 bits.
//-------------------------------------------------------------------------------------------------------------------------------------------------------
        System.out.println("\nSymmetric Encryption:");
        SymmetricEncryption se = new SymmetricEncryption();
        String plaintext = "Hello world! This is a secret message.";

        long startTimeSymmetricEncrypt = System.nanoTime();
        byte[] ciphertextSymmetric = se.SymmetricEncrypt(plaintext);
        long endTimeSymmetricEncrypt = System.nanoTime();
        long durationSymmetricEncrypt = endTimeSymmetricEncrypt - startTimeSymmetricEncrypt;

        long startTimeSymmetricDecrypt = System.nanoTime();
        String decryptedSymmetric = se.SymmetricDecrypt(ciphertextSymmetric);
        long endTimeSymmetricDecrypt = System.nanoTime();
        long durationSymmetricDecrypt = endTimeSymmetricDecrypt - startTimeSymmetricDecrypt;

        System.out.println("Key:              " + se.getKeyBytes());
        System.out.println("Plaintext:        " + plaintext);
        System.out.println("Ciphertext:       " + ciphertextSymmetric);
        System.out.println("Decrypted Text:   " + decryptedSymmetric);
        System.out.println("Equal?   " + plaintext.equals(decryptedSymmetric));
        System.out.printf("Encryptie Tijd:   %d nanoseconden\n", durationSymmetricEncrypt);
        System.out.printf("Decryptie Tijd:   %d nanoseconden\n", durationSymmetricDecrypt);
        System.out.printf("Totale Tijd:      %d nanoseconden\n", durationSymmetricEncrypt + durationSymmetricDecrypt);


//Asymetrische encryptie
// -------------------------------------------------------------------------------------------------------------------------------------------------------
        //Wat zijn de mogelijke sleutellengtes voor het RSA en het DSA algoritme? Vergelijk met de symmetrische sleutels.

        //RSA wordt meestal gegenereerd met lengtes van 2048 of 4096 bits. Langere sleutels bieden verbeterde beveiliging, maar vereisen meer rekenkracht.
        //DSA stanaard sleutellengte is 1024; langere sleutels zijn mogelijk maar niet algemeen in gebruik.
        //In vergelijking met symmetrische sleutels zijn asymmetrische sleutels over het algemeen langer om een
        //vergelijkbaar beveiligingsniveau te bieden, omdat asymmetrische algoritmen complexer zijn
        //en meer rekenkracht vereisen om dezelfde mate van beveiliging te bereiken. Dit komt doordat
        //asymmetrische algoritmen werken met een paar sleutels (openbaar en privé) in plaats van één enkele sleutel.

//------------------------------------------------------------------------------------------------------------------------------------------------------------------
        System.out.println("\nAsymmetric Encryption:");
        AsymmetricEncryption ae = new AsymmetricEncryption();

        System.out.println("Public Key:");
        System.out.println(ae.getPublicKeyPem());

        long startTimeAsymmetricEncrypt = System.nanoTime();
        byte[] ciphertextAsymmetric = ae.AsymmetricEncrypt(plaintext);
        long endTimeAsymmetricEncrypt = System.nanoTime();
        long durationAsymmetricEncrypt = endTimeAsymmetricEncrypt - startTimeAsymmetricEncrypt;

        long startTimeAsymmetricDecrypt = System.nanoTime();
        String decryptedAsymmetric = ae.AsymmetricDecrypt(ciphertextAsymmetric);
        long endTimeAsymmetricDecrypt = System.nanoTime();
        long durationAsymmetricDecrypt = endTimeAsymmetricDecrypt - startTimeAsymmetricDecrypt;

        System.out.println("Plaintext:        " + plaintext);
        System.out.println("Ciphertext:       " + ciphertextAsymmetric);
        System.out.println("Decrypted Text:        " + decryptedAsymmetric);
        System.out.println("Equal?   " + plaintext.equals(decryptedAsymmetric));
        System.out.printf("Encryptie Tijd:   %d nanoseconden\n", durationAsymmetricEncrypt);
        System.out.printf("Decryptie Tijd:   %d nanoseconden\n", durationAsymmetricDecrypt);
        System.out.printf("Totale Tijd:      %d nanoseconden\n", durationAsymmetricEncrypt + durationAsymmetricDecrypt);

// Vergelijking van uitvoeringstijden
        System.out.println("\nVergelijking van Uitvoeringstijden:");
        System.out.printf("Symmetrische Encryptie:   %d ns\n", durationSymmetricEncrypt);
        System.out.printf("Asymmetrische Encryptie:  %d ns\n", durationAsymmetricEncrypt);
        System.out.printf("Symmetrische Decryptie:   %d ns\n", durationSymmetricDecrypt);
        System.out.printf("Asymmetrische Decryptie:  %d ns\n", durationAsymmetricDecrypt);
        System.out.printf("Totale Tijd Symmetrisch:        %d ns\n", durationSymmetricEncrypt + durationSymmetricDecrypt);
        System.out.printf("Totale Tijd Asymmetrisch:       %d ns\n", durationAsymmetricEncrypt + durationAsymmetricDecrypt);


        // Opmerking over Prestaties
        // Over het algemeen is symmetrische encryptie veel sneller dan asymmetrische encryptie.
        // Dit komt doordat symmetrische algoritmen minder complexe wiskundige bewerkingen vereisen
        // in vergelijking met asymmetrische algoritmen. Dit komt doordat asymmetrische algoritmen
        // werken met twee verschillende sleutels (openbaar en privé) en gebruik maken van
        // complexe wiskundige concepten zoals priemfactorisatie of elliptische krommen.
        // Daarnaast zijn hun key-sizes meestal groter om een vergelijkbaar beveiligingsniveau te bieden.
        // Dit resulteert in langere verwerkingstijden voor zowel encryptie als decryptie.
        // Daarom worden symmetrische algoritme vaak gebruikt voor het versleutelen van grote hoeveelheden gegevens,
        // terwijl asymmetrische algoritmen meestal worden gebruikt voor het veilig uitwisselen van sleutels of kleine hoeveelheden gegevens.

//Digitale handtekening

        System.out.println("\nDigitale Handtekening:");

        byte[] digitalSignature = DigitalSignature.sign(serialPerson2, ae.getPrivateKey());

        System.out.println("Handtekening: " + DigitalSignature.bytesToBase64(digitalSignature));

        boolean isVerified = DigitalSignature.verify(serialPerson2, digitalSignature, ae.getPublicKey());

        System.out.println("Handtekening is geldig: " + isVerified);

    }
}




