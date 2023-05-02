package org.example;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.nio.charset.StandardCharsets;
import java.security.*;

public class AsymmetricEncription {

    public static String JAVA = "Java";

    public static void main(String[] args) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException,
            IllegalBlockSizeException, BadPaddingException, IOException, SignatureException {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        keyPairGenerator.initialize(2048);
        KeyPair keyPair = keyPairGenerator.generateKeyPair();

        Cipher encryptCipher = Cipher.getInstance("RSA");
        encryptCipher.init(Cipher.ENCRYPT_MODE, keyPair.getPublic());
        byte[] encryptedWord = encryptCipher.doFinal(JAVA.getBytes(StandardCharsets.UTF_8));

        Signature signature = Signature.getInstance("SHA256withRSA");
        signature.initSign(keyPair.getPrivate());
        signature.update(encryptedWord);
        byte[] digitalSignature = signature.sign();

        FileOutputStream fileOutputStream = new FileOutputStream("data.bin");
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
        objectOutputStream.writeObject(keyPair.getPublic());
        objectOutputStream.writeObject(keyPair.getPrivate());
        objectOutputStream.writeObject(digitalSignature);
        objectOutputStream.writeObject(encryptedWord);
        objectOutputStream.close();
    }
}
