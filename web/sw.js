const CACHE = 'aurora-vault-v2';
const ASSETS = ['./', './index.html', './styles.css', './app.js', './password-engine.js',
  './manifest.webmanifest', './assets/aurora-vault.svg', './assets/aurora-vault-192.png', './assets/aurora-vault-512.png'];

self.addEventListener('install', event => event.waitUntil(caches.open(CACHE).then(cache => cache.addAll(ASSETS))));
self.addEventListener('activate', event => event.waitUntil(
  caches.keys().then(keys => Promise.all(keys.filter(key => key !== CACHE).map(key => caches.delete(key))))));
self.addEventListener('fetch', event => {
  if (event.request.method !== 'GET') return;
  event.respondWith(caches.match(event.request).then(cached => cached || fetch(event.request)));
});
