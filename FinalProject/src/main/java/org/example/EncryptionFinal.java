package org.example;

import sun.security.tools.keytool.CertAndKeyGen;
import sun.security.x509.X500Name;
import javax.crypto.Cipher;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.cert.X509Certificate;
import java.util.*;

public class EncryptionFinal {

    public static long VALIDITY = (long)365*24*3600;
    public static String KEYSTORE_PATH = "keystore.jks";
    public static List<String> KEYSTORE_TYPE_OPTIONS = new ArrayList<>(Arrays.asList("JKS", "JCEKS"));

    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);
        String algorithm = getData(scanner, "Введите способ выбора типа keystore: basic или secure");
        String keyAndKeystorePassword = getData(scanner, "Введите пароль для keystore и ключа:");
        String wordToEncrypt = getData(scanner, "Введите слово для шифрования");
        scanner.close();
        if ((algorithm == null) || (keyAndKeystorePassword == null) || (wordToEncrypt == null)) {
            System.out.println("Ошибка: не получены входные данные");
            return;
        }

        String keystoreType = chooseKeystoreType(algorithm);
        if (keystoreType == null)
            return;
        System.out.println("Выбран тип keystore: " + keystoreType);

        try {
            KeyStore keyStore = KeyStore.getInstance(keystoreType);
            keyStore.load(null, null);
            CertAndKeyGen generator = new CertAndKeyGen("RSA", "SHA1WithRSA");
            generator.generate(2048);
            Key key = generator.getPrivateKey();
            X509Certificate cert = generator.getSelfCertificate(new X500Name("CN=ROOT"), VALIDITY);
            X509Certificate[] chain = new X509Certificate[1];
            chain[0] = cert;
            String keyName = "mykey";
            System.out.println("Имя ключа: " + keyName);
            keyStore.setKeyEntry(keyName, key, keyAndKeystorePassword.toCharArray(), chain);
            keyStore.store(new FileOutputStream(KEYSTORE_PATH), keyAndKeystorePassword.toCharArray());

            Cipher encryptCipher = Cipher.getInstance("RSA");
            encryptCipher.init(Cipher.ENCRYPT_MODE, generator.getPublicKey());
            byte[] encryptedWord = encryptCipher.doFinal(wordToEncrypt.getBytes(StandardCharsets.UTF_8));
            printByteArray(encryptedWord, "Зашифрованное слово: ");

            Signature signature = Signature.getInstance("SHA256withRSA");
            signature.initSign((PrivateKey) key);
            signature.update(encryptedWord);
            byte[] digitalSignature = signature.sign();
            printByteArray(digitalSignature, "Электронная подпись: ");

            saveToFile(keyAndKeystorePassword, encryptedWord, keyName, digitalSignature, keystoreType);
        } catch (Exception e) {
            System.out.println("Что-то пошло не так");
        }
    }

    public static String getData(Scanner scanner, String message) {
        System.out.println(message);
        String data = null;
        if (scanner.hasNextLine()) {
            data = scanner.nextLine();
        }
        return data;
    }

    public static void printByteArray(byte[] byteArray, String message) {
        StringBuilder stringBuilder = new StringBuilder();
        for (byte letter : byteArray) {
            stringBuilder.append(String.format("%02X", letter));
        }
        System.out.println(message);
        System.out.println(stringBuilder);
    }

    public static String chooseKeystoreType(String algorithm) {
        String keystoreType = null;

        if ("Basic".equalsIgnoreCase(algorithm)) {
            Random rand = new Random(System.nanoTime());
            keystoreType = KEYSTORE_TYPE_OPTIONS.get(rand.nextInt(KEYSTORE_TYPE_OPTIONS.size()));
        } else if ("Secure".equalsIgnoreCase(algorithm)) {
            SecureRandom secRand = new SecureRandom(BigInteger.valueOf(System.nanoTime()).toByteArray());
            keystoreType = KEYSTORE_TYPE_OPTIONS.get(secRand.nextInt(KEYSTORE_TYPE_OPTIONS.size()));
        } else {
            System.out.println("Ошибка входных параметров! Неверно указан способ получения прогноза (Basic/Secure)");
        }
        return keystoreType;
    }

    public static void saveToFile(String keyAndKeystorePassword, byte[] encryptedWord, String keyName,
                                  byte[] digitalSignature, String keystoreType) {
        try {
            FileOutputStream fileOutputStream = new FileOutputStream("data.bin");
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
            objectOutputStream.writeObject(KEYSTORE_PATH);
            objectOutputStream.writeObject(keyAndKeystorePassword);
            objectOutputStream.writeObject(encryptedWord);
            objectOutputStream.writeObject(keyName);
            objectOutputStream.writeObject(digitalSignature);
            objectOutputStream.writeObject(keystoreType);
            objectOutputStream.close();
        } catch (IOException e) {
            System.out.println("Не удалось сохранить данные в файл");
            e.printStackTrace();
        }
    }

}
