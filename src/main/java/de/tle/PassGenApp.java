package de.tle;

public class PassGenApp {

    public static Utils newPass = new Utils();

    public static void main(String[] args) throws InterruptedException {

        String genKey = newPass.generateRandomKey(12);

        System.out.println("\nYour new Pass: " + genKey);
    }
}
