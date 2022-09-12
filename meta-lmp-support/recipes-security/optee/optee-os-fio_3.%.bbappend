SUMMARY = "OP-TEE Trusted OS"
DESCRIPTION = "Open Portable Trusted Execution Environment - Trusted side of the TEE"
HOMEPAGE = "https://www.op-tee.org/"

LICENSE = "BSD-2-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=c1f21c4f72f372ef38a5a4aee55ec173"
FILESEXTRAPATHS:prepend_mx8 := "${THISDIR}/files:"
SRC_URI:append_mx8 = " file://0001-optee.patch"


# Disable trace messages
EXTRA_OEMAKE:remove = "CFG_TEE_CORE_LOG_LEVEL=2"
EXTRA_OEMAKE:remove = "CFG_TEE_TA_LOG_LEVEL=2"

