import { generatePassword, estimateStrength } from './password-engine.js';

const $ = selector => document.querySelector(selector);
const controls = {
  length: $('#length'), uppercase: $('#uppercase'), lowercase: $('#lowercase'),
  numbers: $('#numbers'), symbols: $('#symbols'), excludeAmbiguous: $('#excludeAmbiguous')
};
const settings = JSON.parse(localStorage.getItem('aurora-settings') || '{}');
let language = settings.language || (navigator.language.startsWith('de') ? 'de' : 'en');
let installPrompt;

const translations = {
  en: {
    edition: 'PRIVATE PASSWORD STUDIO · 2.0', install: 'Install app', help: 'Help', settings: 'Settings',
    eyebrow: 'ZERO-KNOWLEDGE GENERATOR', title: 'Strong passwords.<br><em>Quietly generated.</em>',
    intro: 'Build unique credentials on your device. Nothing is sent, saved or observed.', local: 'On device',
    secure: 'Secure random', tracking: 'Tracking', offline: 'Ready offline after the first visit',
    password: 'YOUR NEW PASSWORD', generator: 'Generator', private: 'Private', copy: 'Copy', copied: 'Copied',
    length: 'Length', characters: 'CHARACTERS', uppercase: 'Uppercase', lowercase: 'Lowercase', numbers: 'Numbers',
    symbols: 'Symbols', ambiguous: 'Avoid similar characters such as 0/O and 1/l', strength: 'ESTIMATED STRENGTH',
    generate: 'Generate secure password', shortcut: 'Press Space to generate again', footer: 'Generated locally with Web Crypto',
    preferences: 'PREFERENCES', language: 'Language', appearance: 'Appearance', system: 'System', dark: 'Aurora night',
    light: 'Polar day', motion: 'Motion', motionHelp: 'Animate the background aurora', save: 'Save settings',
    helpTitle: 'How it works', helpOneTitle: 'Choose a length', helpOneText: 'Twenty characters is a strong default for most accounts.',
    helpTwoTitle: 'Select character groups', helpTwoText: 'Aurora guarantees at least one character from each selected group.',
    helpThreeTitle: 'Copy and store safely', helpThreeText: 'Use a password manager. Aurora never saves generated values.',
    offlineTitle: 'Works offline', offlineText: 'Install Aurora Vault from your browser menu to use it like an app on phone or desktop.',
    weak: 'Weak', fair: 'Fair', strong: 'Strong', exceptional: 'Exceptional', selectOne: 'Keep at least one character group.'
  },
  de: {
    edition: 'PRIVATES PASSWORT-STUDIO · 2.0', install: 'App installieren', help: 'Hilfe', settings: 'Einstellungen',
    eyebrow: 'LOKALER SICHERHEITSGENERATOR', title: 'Starke Passwörter.<br><em>Diskret erzeugt.</em>',
    intro: 'Erstelle einzigartige Zugangsdaten auf deinem Gerät. Nichts wird gesendet oder gespeichert.', local: 'Auf dem Gerät',
    secure: 'Sicherer Zufall', tracking: 'Tracking', offline: 'Nach dem ersten Besuch offline bereit',
    password: 'DEIN NEUES PASSWORT', generator: 'Generator', private: 'Privat', copy: 'Kopieren', copied: 'Kopiert',
    length: 'Länge', characters: 'ZEICHEN', uppercase: 'Großbuchstaben', lowercase: 'Kleinbuchstaben', numbers: 'Zahlen',
    symbols: 'Sonderzeichen', ambiguous: 'Ähnliche Zeichen wie 0/O und 1/l vermeiden', strength: 'GESCHÄTZTE STÄRKE',
    generate: 'Sicheres Passwort erzeugen', shortcut: 'Leertaste drücken, um neu zu erzeugen', footer: 'Lokal mit Web Crypto erzeugt',
    preferences: 'EINSTELLUNGEN', language: 'Sprache', appearance: 'Darstellung', system: 'System', dark: 'Aurora-Nacht',
    light: 'Polartag', motion: 'Bewegung', motionHelp: 'Hintergrund-Aurora animieren', save: 'Einstellungen speichern',
    helpTitle: 'So funktioniert es', helpOneTitle: 'Länge wählen', helpOneText: 'Zwanzig Zeichen sind für die meisten Konten ein starker Standard.',
    helpTwoTitle: 'Zeichengruppen wählen', helpTwoText: 'Aurora verwendet garantiert mindestens ein Zeichen jeder gewählten Gruppe.',
    helpThreeTitle: 'Sicher kopieren und speichern', helpThreeText: 'Nutze einen Passwortmanager. Aurora speichert keine erzeugten Werte.',
    offlineTitle: 'Funktioniert offline', offlineText: 'Installiere Aurora Vault über das Browsermenü wie eine App auf Handy oder Desktop.',
    weak: 'Schwach', fair: 'Solide', strong: 'Stark', exceptional: 'Außergewöhnlich', selectOne: 'Mindestens eine Zeichengruppe behalten.'
  }
};

function policy() {
  return { length: Number(controls.length.value), uppercase: controls.uppercase.checked,
    lowercase: controls.lowercase.checked, numbers: controls.numbers.checked,
    symbols: controls.symbols.checked, excludeAmbiguous: controls.excludeAmbiguous.checked };
}

function generate() {
  const activePolicy = policy();
  $('#passwordOutput').textContent = generatePassword(activePolicy);
  const strength = estimateStrength(activePolicy);
  $('#strengthLabel').textContent = translations[language][strength.level];
  $('#entropyLabel').textContent = `${Math.round(strength.bits)} bits`;
  $('#strengthBar').style.width = `${Math.min(100, strength.bits / 1.28)}%`;
}

function translate() {
  document.documentElement.lang = language;
  document.querySelectorAll('[data-i18n]').forEach(element => {
    const value = translations[language][element.dataset.i18n];
    if (value) element[element.dataset.i18n === 'title' ? 'innerHTML' : 'textContent'] = value;
  });
  generate();
}

function applyAppearance() {
  const selected = settings.theme || 'system';
  const resolved = selected === 'system'
    ? (matchMedia('(prefers-color-scheme: light)').matches ? 'light' : 'dark') : selected;
  document.documentElement.dataset.theme = resolved;
  document.documentElement.dataset.motion = settings.motion === false ? 'reduced' : 'full';
  $('meta[name="theme-color"]').content = resolved === 'light' ? '#e9f5f1' : '#071921';
}

function toast(message) {
  const element = $('#toast');
  element.textContent = message;
  element.classList.add('visible');
  clearTimeout(toast.timer);
  toast.timer = setTimeout(() => element.classList.remove('visible'), 1700);
}

controls.length.addEventListener('input', () => { $('#lengthValue').textContent = controls.length.value; });
controls.length.addEventListener('change', generate);
Object.entries(controls).filter(([name]) => name !== 'length').forEach(([, control]) => control.addEventListener('change', event => {
  const groupControls = [controls.uppercase, controls.lowercase, controls.numbers, controls.symbols];
  if (!groupControls.some(input => input.checked)) {
    event.target.checked = true;
    toast(translations[language].selectOne);
  }
  generate();
}));
$('#generateButton').addEventListener('click', generate);
$('#copyButton').addEventListener('click', async () => {
  await navigator.clipboard.writeText($('#passwordOutput').textContent);
  toast(`${translations[language].copied} ✓`);
});
document.addEventListener('keydown', event => {
  if (event.code === 'Space' && !['INPUT', 'SELECT', 'BUTTON'].includes(document.activeElement.tagName)) {
    event.preventDefault(); generate();
  }
});

document.querySelectorAll('[data-dialog]').forEach(button => button.addEventListener('click', () => {
  if (button.dataset.dialog === 'settingsDialog') {
    $('#language').value = language;
    $('#theme').value = settings.theme || 'system';
    $('#motion').checked = settings.motion !== false;
  }
  document.getElementById(button.dataset.dialog).showModal();
}));
$('#saveSettings').addEventListener('click', () => {
  language = $('#language').value;
  settings.language = language;
  settings.theme = $('#theme').value;
  settings.motion = $('#motion').checked;
  localStorage.setItem('aurora-settings', JSON.stringify(settings));
  applyAppearance(); translate();
});

window.addEventListener('beforeinstallprompt', event => {
  event.preventDefault(); installPrompt = event; $('#installButton').hidden = false;
});
$('#installButton').addEventListener('click', async () => {
  if (installPrompt) await installPrompt.prompt();
  installPrompt = null; $('#installButton').hidden = true;
});
matchMedia('(prefers-color-scheme: light)').addEventListener('change', applyAppearance);
if ('serviceWorker' in navigator) navigator.serviceWorker.register('./sw.js');

applyAppearance();
translate();
