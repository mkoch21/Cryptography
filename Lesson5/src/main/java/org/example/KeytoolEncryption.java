package org.example;

import javax.crypto.*;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Scanner;
import sun.security.x509.X500Name;
import sun.security.tools.keytool.CertAndKeyGen;

public class KeytoolEncryption {

    public static String JAVA = "Java";
    public static long VALIDITY = (long)365*24*3600;

    public static void main(String[] args) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException,
            IllegalBlockSizeException, BadPaddingException, IOException, KeyStoreException, CertificateException,
            SignatureException, NoSuchProviderException {

        Scanner scanner = new Scanner(System.in);
        System.out.println("Введите имя файла хранилища (keystore): ");
        String fileName = null;
        if (scanner.hasNextLine()) {
            fileName = scanner.nextLine();
        }

        System.out.println("Введите длину ключа RSA: ");
        int keySize = 0;
        if (scanner.hasNextInt()) {
            keySize = scanner.nextInt();
        }
        scanner.close();

        KeyStore keyStore = KeyStore.getInstance("JKS");
        keyStore.load(null, null);
        CertAndKeyGen generator = new CertAndKeyGen("RSA","SHA1WithRSA");
        generator.generate(keySize);
        Key key=generator.getPrivateKey();
        X509Certificate cert=generator.getSelfCertificate(new X500Name("CN=ROOT"), VALIDITY);
        X509Certificate[] chain = new X509Certificate[1];
        chain[0]=cert;
        String keyName = "mykey";
        keyStore.setKeyEntry(keyName, key, "superSecretPassword".toCharArray(), chain);
        keyStore.store(new FileOutputStream(fileName + ".jks"), "password".toCharArray());

        Cipher encryptCipher = Cipher.getInstance("RSA");
        encryptCipher.init(Cipher.ENCRYPT_MODE, generator.getPublicKey());
        byte[] encryptedWord = encryptCipher.doFinal(JAVA.getBytes(StandardCharsets.UTF_8));

        StringBuilder stringBuilder = new StringBuilder();
        for (byte letter : encryptedWord) {
            stringBuilder.append(String.format("%02X", letter));
        }
        System.out.println("Зашифрованное слово: ");
        System.out.println(stringBuilder);

        FileOutputStream fileOutputStream = new FileOutputStream("data.bin");
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
        objectOutputStream.writeObject(fileName);
        objectOutputStream.writeObject(keyName);
        objectOutputStream.writeObject(encryptedWord);
        objectOutputStream.close();

    }

}
