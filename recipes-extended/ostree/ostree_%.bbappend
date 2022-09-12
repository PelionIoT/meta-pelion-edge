FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"

# Disable PTEST for ostree as it requires options that are not enabled when
# building with meta-updater and meta-lmp.

PTEST_ENABLED = "0"

SRC_URI = " \
    gitsm://github.com/ostreedev/ostree;branch=main;protocol=https \
    file://update-default-grub-cfg-header.patch \
"

# gpgme is not required by us, and it brings GPLv3 dependencies
PACKAGECONFIG:remove = "gpgme"
