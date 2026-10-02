package de.sofoste.passgen;

import de.sofoste.passgen.ui.AuroraVaultFrame;
import javax.swing.SwingUtilities;
import java.awt.GraphicsEnvironment;
import java.util.Arrays;

/** Entry point shared by the desktop interface and the learning-friendly CLI. */
public final class PassGenApp {
    private static final String VERSION = "2.0.0";
    private PassGenApp() { }

    public static void main(String[] args) {
        if (contains(args, "--diagnostics")) { runDiagnostics(); return; }
        if (contains(args, "--help")) { printHelp(); return; }
        if (contains(args, "--cli") || GraphicsEnvironment.isHeadless()) { runCli(args); return; }
        SwingUtilities.invokeLater(() -> new AuroraVaultFrame().setVisible(true));
    }

    private static void runCli(String[] args) {
        int length = integerArgument(args, "--length", 20);
        int count = integerArgument(args, "--count", 1);
        PasswordPolicy policy = new PasswordPolicy(length, true, true, true,
                !contains(args, "--no-symbols"), contains(args, "--exclude-ambiguous"));
        PasswordGenerator generator = new PasswordGenerator();
        PasswordStrength.Result strength = PasswordStrength.estimate(policy);
        System.out.printf("AURORA VAULT %s // %.0f bits // %s%n", VERSION, strength.bits(), strength.level());
        for (int index = 0; index < count; index++) System.out.println(generator.generate(policy));
    }

    private static void runDiagnostics() {
        PasswordPolicy policy = new PasswordPolicy(24, true, true, true, true, true);
        String password = new PasswordGenerator().generate(policy);
        if (password.length() != policy.length()) throw new IllegalStateException("Invalid generated length.");
        System.out.printf("Aurora Vault %s - secure generator nominal%n", VERSION);
    }

    private static int integerArgument(String[] args, String name, int fallback) {
        for (int index = 0; index < args.length - 1; index++) {
            if (args[index].equalsIgnoreCase(name)) {
                try { return Integer.parseInt(args[index + 1]); }
                catch (NumberFormatException exception) { throw new IllegalArgumentException(name + " requires a number."); }
            }
        }
        return fallback;
    }

    private static boolean contains(String[] args, String value) {
        return Arrays.stream(args).anyMatch(value::equalsIgnoreCase);
    }

    private static void printHelp() {
        System.out.println("""
                Aurora Vault 2.0.0
                Desktop: java -jar aurora-vault.jar
                CLI:     java -jar aurora-vault.jar --cli [options]

                --length N            Password length (8-128, default 20)
                --count N             Number of passwords (default 1)
                --no-symbols          Omit symbols
                --exclude-ambiguous   Omit characters such as 0/O and 1/l
                --diagnostics         Verify the secure generator
                """);
    }
}
