package de.tle;

import java.security.SecureRandom;

public class Utils {

    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*()-_=+";
    private static final SecureRandom RANDOM = new SecureRandom();

    public String generateRandomKey(int length) throws InterruptedException {
        StringBuilder password = new StringBuilder(length);
        for (int i = 0; i <= 20; i++) {
            if (i < 20) {
                int index = RANDOM.nextInt(CHARACTERS.length());
                if (password.length() < length) {
                    password.append(CHARACTERS.charAt(index));
                }
            }
            displayProgress(i, 20);
            Thread.sleep(250); // Simulate some delay in generation
        }
        return password.toString();
    }

    private void displayProgress(int current, int total) {
        int percent = (int) ((current / (double) total) * 100);
        StringBuilder progress = new StringBuilder("[");

        for (int i = 0; i < total; i++) {
            if (i < current) {
                progress.append("#");
            } else {
                progress.append(" ");
            }
        }
        progress.append("] ").append(percent).append("% ");

        System.out.print("\r" + progress.toString());
    }
}
