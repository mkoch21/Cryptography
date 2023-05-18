package org.example;

import javax.crypto.*;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Scanner;

public class PasswordBasedEncryption {

    public static String JAVA = "Java";

    public static void main(String[] args) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException,
            IllegalBlockSizeException, BadPaddingException, IOException, InvalidKeySpecException {

        Scanner scanner = new Scanner(System.in);
        System.out.println("Введите пароль:");
        String password = null;
        if (scanner.hasNextLine()) {
            password = scanner.nextLine();
        }
        scanner.close();

        KeySpec keySpec = new PBEKeySpec(password.toCharArray(), "saltExample".getBytes(StandardCharsets.UTF_8), 42, 256);
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        SecretKey key = new SecretKeySpec(keyFactory.generateSecret(keySpec).getEncoded(), "AES");

        Cipher encryptCipher = Cipher.getInstance("AES");
        encryptCipher.init(Cipher.ENCRYPT_MODE, key);
        byte[] encryptedWord = encryptCipher.doFinal(JAVA.getBytes(StandardCharsets.UTF_8));

        FileOutputStream fileOutputStream = new FileOutputStream("data.bin");
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
        objectOutputStream.writeObject(key);
        objectOutputStream.writeObject(encryptedWord);
        objectOutputStream.close();

    }
}
