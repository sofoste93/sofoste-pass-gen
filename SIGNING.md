# Windows signing

The release workflow signs `AuroraVault.exe` when these GitHub Actions secrets are configured:

- `WINDOWS_CERTIFICATE_BASE64`: base64-encoded Authenticode `.pfx` certificate
- `WINDOWS_CERTIFICATE_PASSWORD`: certificate password

A publicly trusted code-signing certificate is required for Windows to display a verified publisher. The workflow timestamps signatures through DigiCert so they remain valid after certificate expiry.
