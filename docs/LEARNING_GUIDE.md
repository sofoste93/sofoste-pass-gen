# Learning guide: from click to password

Aurora Vault deliberately keeps its security engine independent from its interfaces. The Java desktop app and the browser app follow the same four-step pipeline.

## 1. Describe the policy

`PasswordPolicy` records the requested length and character groups. Its constructor rejects impossible input early. An immutable policy is useful here because generation should not change halfway through a call.

The browser uses an ordinary object for the same job. `characterGroups()` validates it and returns only the enabled alphabets.

## 2. Ask the operating system for randomness

Java's `SecureRandom` and the browser's `crypto.getRandomValues()` are cryptographically secure pseudorandom number generators. Their seeds and internal state come from the operating system. `Math.random()` is intentionally absent because it is designed for simulations rather than secrets.

The browser engine uses rejection sampling. Mapping a 32-bit value with `% poolLength` directly would make a few indexes slightly more likely whenever the pool length does not divide 2³². Values outside the largest divisible range are discarded and sampled again.

## 3. Guarantee the selected groups

Aurora first chooses one character from each enabled group. It fills the remaining positions from the combined pool. This makes the controls truthful: selecting numbers guarantees at least one number.

Those first positions would be predictable, so the generator applies a Fisher-Yates shuffle. Every permutation receives the same probability when its random indexes are unbiased.

## 4. Explain strength without storing the secret

The estimate is:

```text
entropy bits = password length × log₂(character pool size)
```

It describes the size of the theoretical search space. The generated password is rendered directly and is never written to preferences, local storage, logs or analytics. Only harmless interface choices such as language and theme persist.

## Exercises

1. Add a hexadecimal-only policy and its test.
2. Add a pronounceable mode, then document why it has a smaller search space.
3. Write a statistical test that counts character frequencies over one million samples.
4. Add a French translation without touching the security engine.
5. Compare rejection sampling with a naïve modulo implementation.
