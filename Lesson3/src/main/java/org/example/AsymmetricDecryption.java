package org.example;

import javax.crypto.*;
import java.io.*;
import java.security.*;

public class AsymmetricDecryption {

    public static void main(String[] args) throws IOException, NoSuchAlgorithmException, ClassNotFoundException,
            NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException, SignatureException {

        FileInputStream fileInputStream = new FileInputStream("data.bin");
        ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
        PublicKey publicKey = (PublicKey) objectInputStream.readObject();
        PrivateKey privateKey = (PrivateKey) objectInputStream.readObject();
        byte[] digitalSignature = (byte[]) objectInputStream.readObject();
        byte[] encryptedWord = (byte[]) objectInputStream.readObject();
        objectInputStream.close();

        Cipher encryptCipher = Cipher.getInstance("RSA");
        encryptCipher.init(Cipher.DECRYPT_MODE, privateKey);
        byte[] decryptedWord = encryptCipher.doFinal(encryptedWord);
        StringBuilder stringBuilder = new StringBuilder();
        for (byte letter : decryptedWord) {
            stringBuilder.append((char) letter);
        }
        System.out.println("Зашифрованное слово:");
        System.out.println(stringBuilder);

        Signature signature = Signature.getInstance("SHA256withRSA");
        signature.initVerify(publicKey);
        signature.update(encryptedWord);
        boolean isSignatureCorrect = signature.verify(digitalSignature);
        if (isSignatureCorrect) {
            System.out.println("Sign is OK");
        } else {
            System.out.println("Sign check failed");
        }
    }
}
