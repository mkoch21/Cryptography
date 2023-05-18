package org.example;

import javax.crypto.*;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.security.*;

public class PasswordBasedDecryption {

    public static void main(String[] args) throws IOException, NoSuchAlgorithmException, ClassNotFoundException,
            NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {

        FileInputStream fileInputStream = new FileInputStream("data.bin");
        ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
        SecretKey key = (SecretKey) objectInputStream.readObject();
        byte[] encryptedWord = (byte[]) objectInputStream.readObject();
        objectInputStream.close();

        Cipher encryptCipher = Cipher.getInstance("AES");
        encryptCipher.init(Cipher.DECRYPT_MODE, key);
        byte[] decryptedWord = encryptCipher.doFinal(encryptedWord);
        StringBuilder stringBuilder = new StringBuilder();
        for (byte letter : decryptedWord) {
            stringBuilder.append((char) letter);
        }
        System.out.println("Зашифрованное слово:");
        System.out.println(stringBuilder);

    }
}
