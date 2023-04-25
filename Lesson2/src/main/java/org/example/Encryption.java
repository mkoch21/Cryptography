package org.example;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Scanner;

public class Encryption {
    public static void main(String[] args) throws InvalidKeyException, NoSuchPaddingException, NoSuchAlgorithmException, IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException {

        Scanner scanner = new Scanner(System.in);
        System.out.println("Введите слово:");
        String word = null;
        if (scanner.hasNextLine()) {
            word = scanner.nextLine();
        }
        scanner.close();

        MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
        byte[] hashSumSHA256 = messageDigest.digest(word.getBytes(StandardCharsets.UTF_8));
        StringBuilder stringBuilder = new StringBuilder();
        for (byte letter : hashSumSHA256) {
            stringBuilder.append(String.format("%02X", letter));
        }
        System.out.println("Хэш-сумма слова:");
        System.out.println(stringBuilder);

        KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
        keyGenerator.init(256);
        SecretKey key = keyGenerator.generateKey();

        Cipher encryptCipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        encryptCipher.init(Cipher.ENCRYPT_MODE, key, new IvParameterSpec(new byte[16]));
        byte[] encryptedWord = encryptCipher.doFinal(word.getBytes(StandardCharsets.UTF_8));
        StringBuilder stringBuilder1 = new StringBuilder();
        for (byte letter : encryptedWord) {
            stringBuilder1.append(String.format("%02X", letter));
        }
        System.out.println("Зашифрованное слово:");
        System.out.println(stringBuilder1);

        Cipher decryptCipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        decryptCipher.init(Cipher.DECRYPT_MODE, key, new IvParameterSpec(new byte[16]));
        byte[] decryptedWord = decryptCipher.doFinal(encryptedWord);
        StringBuilder stringBuilder2 = new StringBuilder();
        for (byte letter : decryptedWord) {
            stringBuilder2.append((char) letter);
        }
        System.out.println("Расшифрованное слово:");
        System.out.println(stringBuilder2);
    }
}
