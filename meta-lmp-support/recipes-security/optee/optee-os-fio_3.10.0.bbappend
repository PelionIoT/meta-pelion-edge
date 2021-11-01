SUMMARY = "OP-TEE Trusted OS"
DESCRIPTION = "Open Portable Trusted Execution Environment - Trusted side of the TEE"
HOMEPAGE = "https://www.op-tee.org/"

LICENSE = "BSD-2-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=c1f21c4f72f372ef38a5a4aee55ec173"

# Disable trace messages
EXTRA_OEMAKE_remove = "CFG_TEE_CORE_LOG_LEVEL=2"
EXTRA_OEMAKE_remove = "CFG_TEE_TA_LOG_LEVEL=2"

# Disable use of crypto driver - it is not compatible with PARSEC/PKCS11
EXTRA_OEMAKE_remove_mx8 = "CFG_CRYPTO_DRIVER=y"
EXTRA_OEMAKE_append_mx8 = "CFG_CRYPTO_DRIVER=n"


