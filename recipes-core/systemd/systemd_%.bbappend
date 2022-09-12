SUMMARY = "adds gcrypt (libgcrypt) to systemd binary for forward secure sealing (ffs) journald"
PACKAGECONFIG:append = " gcrypt"

# Disable systemd-resolved service
PACKAGECONFIG:remove = " resolved nss-resolve"
