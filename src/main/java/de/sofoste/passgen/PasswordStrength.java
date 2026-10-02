package de.sofoste.passgen;

/** Estimates theoretical entropy from password length and character-pool size. */
public final class PasswordStrength {
    private PasswordStrength() { }

    public static Result estimate(PasswordPolicy policy) {
        double bits = policy.length() * (Math.log(policy.characterPool().length()) / Math.log(2));
        Level level = bits < 50 ? Level.WEAK : bits < 70 ? Level.FAIR : bits < 90 ? Level.STRONG : Level.EXCEPTIONAL;
        return new Result(bits, level);
    }

    public enum Level { WEAK, FAIR, STRONG, EXCEPTIONAL }
    public record Result(double bits, Level level) { }
}
