package de.sofoste.passgen;

import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class PasswordGeneratorTest {
    private final PasswordGenerator generator = new PasswordGenerator();

    @Test
    void generatedPasswordHonorsLengthAndEverySelectedGroup() {
        PasswordPolicy policy = new PasswordPolicy(32, true, true, true, true, false);
        String password = generator.generate(policy);

        assertEquals(32, password.length());
        assertTrue(password.chars().anyMatch(Character::isUpperCase));
        assertTrue(password.chars().anyMatch(Character::isLowerCase));
        assertTrue(password.chars().anyMatch(Character::isDigit));
        assertTrue(password.chars().anyMatch(character -> PasswordPolicy.SYMBOLS.indexOf(character) >= 0));
    }

    @Test
    void ambiguousCharactersCanBeRemoved() {
        PasswordPolicy policy = new PasswordPolicy(128, true, true, true, true, true);
        String password = generator.generate(policy);
        assertTrue(password.chars().noneMatch(character -> "Il1O0o|`'\"".indexOf(character) >= 0));
    }

    @Test
    void successivePasswordsAreNotReused() {
        PasswordPolicy policy = new PasswordPolicy(24, true, true, true, true, false);
        Set<String> passwords = new HashSet<>();
        for (int index = 0; index < 100; index++) passwords.add(generator.generate(policy));
        assertEquals(100, passwords.size());
    }

    @Test
    void invalidPoliciesAreRejected() {
        assertThrows(IllegalArgumentException.class,
                () -> new PasswordPolicy(7, true, true, true, true, false));
        assertThrows(IllegalArgumentException.class,
                () -> new PasswordPolicy(20, false, false, false, false, false));
    }
}
