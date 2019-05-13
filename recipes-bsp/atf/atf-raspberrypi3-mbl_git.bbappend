FILESEXTRAPATHS_prepend := "${THISDIR}/files:"

SRC_URI += "file://rot_key.pem"

MBL_ROT_KEY ?= "${WORKDIR}/rot_key.pem"
