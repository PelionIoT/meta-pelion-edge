FILESEXTRAPATHS_prepend_uz := "${THISDIR}/files:"

SRC_URI_append_uz = " \
        file://Read-EMAC-from-EEPROM-on-uz3eg-iocc.patch;patchdir=..;striplevel=8 \
"

COMPATIBLE_MACHINE_uz = ".*"
