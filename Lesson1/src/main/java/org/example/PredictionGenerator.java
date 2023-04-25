package org.example;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

public class PredictionGenerator {
    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);
        String name = getData(scanner, "Введите имя:");
        String algorithm = getData(scanner, "Введите алгоритм генерации: basic или secure");
        scanner.close();

        List<String> options = new ArrayList<>();
        options.add("У вас сегодня будет удача в делах!");
        options.add("Сегодня хороший день для саморазвития!");

        if (algorithm.equalsIgnoreCase("Basic")) {
            Random rand = new Random(System.nanoTime());
            System.out.println(name + ", " + options.get(rand.nextInt(options.size())));
        } else if (algorithm.equalsIgnoreCase("Secure")) {
            SecureRandom secRand = new SecureRandom(BigInteger.valueOf(System.nanoTime()).toByteArray());
            System.out.println(name + ", " + options.get(secRand.nextInt(options.size())));
        } else {
            System.out.println("Ошибка входных параметров! Требуется: Имя пользователя и Способ получения прогноза (Basic/Secure)");
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

}
