import test from 'node:test';
import assert from 'node:assert/strict';
import { generatePassword, estimateStrength, GROUPS } from '../web/password-engine.js';

const policy = { length: 32, uppercase: true, lowercase: true, numbers: true, symbols: true, excludeAmbiguous: true };

test('web generator honors length and selected groups', () => {
  const password = generatePassword(policy);
  assert.equal(password.length, 32);
  assert.match(password, /[A-Z]/);
  assert.match(password, /[a-z]/);
  assert.match(password, /[0-9]/);
  assert.ok([...password].some(character => GROUPS.symbols.includes(character)));
});

test('web generator removes ambiguous characters', () => {
  const password = generatePassword({ ...policy, length: 128 });
  assert.doesNotMatch(password, /[Il1O0o|`'"]/);
});

test('strength grows with length', () => {
  const short = estimateStrength({ ...policy, length: 12 });
  const long = estimateStrength({ ...policy, length: 32 });
  assert.ok(long.bits > short.bits);
  assert.equal(long.level, 'exceptional');
});
