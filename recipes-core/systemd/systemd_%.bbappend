SUMMARY = "adds gcrypt (libgcrypt) to systemd binary for forward secure sealing (ffs) journald"
PACKAGECONFIG_append = " gcrypt"

# Disable systemd-resolved service
PACKAGECONFIG_remove = " resolved nss-resolve"
