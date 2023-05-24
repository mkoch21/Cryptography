package org.example;

import javax.crypto.*;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.security.*;
import java.security.cert.CertificateException;

public class KeytoolDecryption {

    public static void main(String[] args) throws IOException, NoSuchAlgorithmException, ClassNotFoundException,
            NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException, KeyStoreException,
            CertificateException, UnrecoverableKeyException {

        FileInputStream fileInputStream = new FileInputStream("data.bin");
        ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
        String fileName = (String) objectInputStream.readObject();
        String keyName = (String) objectInputStream.readObject();
        byte[] encryptedWord = (byte[]) objectInputStream.readObject();
        objectInputStream.close();

        KeyStore keyStore = KeyStore.getInstance("JKS");
        keyStore.load(new FileInputStream(fileName + ".jks"), "password".toCharArray());
        Key key = keyStore.getKey(keyName, "superSecretPassword".toCharArray());

        Cipher decryptCipher = Cipher.getInstance("RSA");
        decryptCipher.init(Cipher.DECRYPT_MODE, key);
        byte[] decryptedWord = decryptCipher.doFinal(encryptedWord);

        StringBuilder stringBuilder = new StringBuilder();
        for (byte letter : decryptedWord) {
            stringBuilder.append((char) letter);
        }
        System.out.println("Зашифрованное слово:");
        System.out.println(stringBuilder);

    }
}
