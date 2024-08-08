package de.tle;

public class PassGenApp {

    public static Utils newPass = new Utils();

    public static void main(String[] args) throws InterruptedException {

        String genKey = newPass.generateRandomKey(12);
        String genKey14 = newPass.generateRandomKey(14);
        String genKey16 = newPass.generateRandomKey(16);

        System.out.println("\nYour new Pass: \n"
                + "12 Char: " + genKey
                + "\n14 Char: " + genKey14
                + "\n16 Char: "  + genKey16);
    }
}
