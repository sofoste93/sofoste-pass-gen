package de.sofoste.passgen;

import java.util.ArrayList;
import java.util.List;

/** Immutable description of the characters allowed in a generated password. */
public record PasswordPolicy(int length, boolean uppercase, boolean lowercase,
                             boolean numbers, boolean symbols, boolean excludeAmbiguous) {
    public static final String UPPERCASE = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    public static final String LOWERCASE = "abcdefghijklmnopqrstuvwxyz";
    public static final String NUMBERS = "0123456789";
    public static final String SYMBOLS = "!@#$%^&*()-_=+[]{};:,.?";
    private static final String AMBIGUOUS = "Il1O0o|`'\"";

    public PasswordPolicy {
        if (length < 8 || length > 128) throw new IllegalArgumentException("Length must be between 8 and 128.");
        if (!uppercase && !lowercase && !numbers && !symbols) {
            throw new IllegalArgumentException("Select at least one character group.");
        }
    }

    /** Returns groups separately so the generator can guarantee that every selected kind appears. */
    public List<String> groups() {
        List<String> groups = new ArrayList<>();
        if (uppercase) groups.add(clean(UPPERCASE));
        if (lowercase) groups.add(clean(LOWERCASE));
        if (numbers) groups.add(clean(NUMBERS));
        if (symbols) groups.add(clean(SYMBOLS));
        return List.copyOf(groups);
    }

    public String characterPool() {
        return String.join("", groups());
    }

    private String clean(String characters) {
        if (!excludeAmbiguous) return characters;
        return characters.chars().filter(character -> AMBIGUOUS.indexOf(character) < 0)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append).toString();
    }
}
