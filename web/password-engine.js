const GROUPS = Object.freeze({
  uppercase: 'ABCDEFGHIJKLMNOPQRSTUVWXYZ',
  lowercase: 'abcdefghijklmnopqrstuvwxyz',
  numbers: '0123456789',
  symbols: '!@#$%^&*()-_=+[]{};:,.?'
});
const AMBIGUOUS = new Set('Il1O0o|`\'"');

function secureIndex(limit, cryptoProvider = globalThis.crypto) {
  if (!cryptoProvider?.getRandomValues) throw new Error('Web Crypto is unavailable.');
  // Rejection sampling prevents the modulo bias introduced by random % limit.
  const range = 0x1_0000_0000;
  const ceiling = Math.floor(range / limit) * limit;
  const value = new Uint32Array(1);
  do cryptoProvider.getRandomValues(value); while (value[0] >= ceiling);
  return value[0] % limit;
}

export function characterGroups(policy) {
  const selected = Object.entries(GROUPS)
    .filter(([name]) => policy[name])
    .map(([, characters]) => policy.excludeAmbiguous
      ? [...characters].filter(character => !AMBIGUOUS.has(character)).join('')
      : characters);
  if (!selected.length) throw new Error('Select at least one character group.');
  return selected;
}

export function generatePassword(policy, cryptoProvider = globalThis.crypto) {
  if (!Number.isInteger(policy.length) || policy.length < 8 || policy.length > 128) {
    throw new Error('Length must be between 8 and 128.');
  }
  const groups = characterGroups(policy);
  const pool = groups.join('');
  const password = groups.map(group => group[secureIndex(group.length, cryptoProvider)]);
  while (password.length < policy.length) password.push(pool[secureIndex(pool.length, cryptoProvider)]);

  // Fisher-Yates hides the guaranteed group characters among the random ones.
  for (let index = password.length - 1; index > 0; index -= 1) {
    const swapIndex = secureIndex(index + 1, cryptoProvider);
    [password[index], password[swapIndex]] = [password[swapIndex], password[index]];
  }
  return password.join('');
}

export function estimateStrength(policy) {
  const bits = policy.length * Math.log2(characterGroups(policy).join('').length);
  const level = bits < 50 ? 'weak' : bits < 70 ? 'fair' : bits < 90 ? 'strong' : 'exceptional';
  return { bits, level };
}

export { GROUPS };
