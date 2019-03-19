# Based on: meta-initramfs/recipes-bsp/initrdscripts/initramfs-debug_1.0.bb
# In open-source project: http://git.openembedded.org/meta-openembedded
#
# Original file: No copyright notice was included
# Modifications: Copyright (c) 2018 Arm Limited and Contributors. All rights reserved.
#
# SPDX-License-Identifier: MIT

SUMMARY = "sample initramfs image init script (to be replaced by wigwag specific)"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"
FILESEXTRAPATHS_prepend := "${THISDIR}/files:"
SRC_URI += "file://initramfs-init-script.sh \
            file://upgradeCA.cert"

S = "${WORKDIR}"

do_install() {
        install -m 0755 ${WORKDIR}/initramfs-init-script.sh ${D}/init
        install -D -m 0444 ${WORKDIR}/upgradeCA.cert ${D}/etc/ssl/certs/upgradeCA.cert
}

inherit allarch

FILES_${PN} += " /init /etc/ssl/certs/upgradeCA.cert "
