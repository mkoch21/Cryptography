package org.example;

import javax.crypto.Cipher;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.security.*;

public class DecryptionFinal {

    public static void main(String[] args) {

        try {
            FileInputStream fileInputStream = new FileInputStream("data.bin");
            ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
            String keystorePath = (String) objectInputStream.readObject();
            String keyAndKeystorePassword = (String) objectInputStream.readObject();
            byte[] encryptedWord = (byte[]) objectInputStream.readObject();
            String keyName = (String) objectInputStream.readObject();
            byte[] digitalSignature = (byte[]) objectInputStream.readObject();
            String keystoreType = (String) objectInputStream.readObject();
            objectInputStream.close();

            KeyStore keyStore = KeyStore.getInstance(keystoreType);
            keyStore.load(new FileInputStream(keystorePath), keyAndKeystorePassword.toCharArray());
            Key key = keyStore.getKey(keyName, keyAndKeystorePassword.toCharArray());

            PublicKey publicKey = keyStore.getCertificate(keyName).getPublicKey();
            Signature signature = Signature.getInstance("SHA256withRSA");
            signature.initVerify(publicKey);
            signature.update(encryptedWord);
            boolean isSignatureCorrect = signature.verify(digitalSignature);
            if (isSignatureCorrect) {
                System.out.println("Подпись верна");
            } else {
                System.out.println("Подпись неверна!");
            }

            Cipher decryptCipher = Cipher.getInstance("RSA");
            decryptCipher.init(Cipher.DECRYPT_MODE, key);
            byte[] decryptedWord = decryptCipher.doFinal(encryptedWord);

            StringBuilder stringBuilder = new StringBuilder();
            for (byte letter : decryptedWord) {
                stringBuilder.append((char) letter);
            }
            System.out.println("Зашифрованное слово:");
            System.out.println(stringBuilder);

        } catch (Exception e) {
            System.out.println("Что-то пошло не так");
            e.printStackTrace();
        }
    }

}