package de.sofoste.passgen;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;

/** Generates passwords with a cryptographically secure random number generator. */
public final class PasswordGenerator {
    private final SecureRandom random;

    public PasswordGenerator() { this(new SecureRandom()); }
    PasswordGenerator(SecureRandom random) { this.random = random; }

    public String generate(PasswordPolicy policy) {
        List<Character> result = new ArrayList<>(policy.length());

        // Seed with one character from every selected group. Users therefore never
        // receive a password without a digit or symbol when that option is enabled.
        for (String group : policy.groups()) result.add(randomCharacter(group));

        String pool = policy.characterPool();
        while (result.size() < policy.length()) result.add(randomCharacter(pool));

        // Fisher-Yates removes the predictable group order without weakening entropy.
        for (int index = result.size() - 1; index > 0; index--) {
            int swapIndex = random.nextInt(index + 1);
            Character temporary = result.get(index);
            result.set(index, result.get(swapIndex));
            result.set(swapIndex, temporary);
        }

        StringBuilder password = new StringBuilder(result.size());
        result.forEach(password::append);
        return password.toString();
    }

    private char randomCharacter(String source) {
        return source.charAt(random.nextInt(source.length()));
    }
}
