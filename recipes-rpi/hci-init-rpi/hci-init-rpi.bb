DESCRIPTION = "init scripts for bringing up the bluetooth device hci0 on Raspberry Pi 3 Boards"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"

SRC_URI = " \
    file://hci-init-rpi.sh \
    "

S = "${WORKDIR}"

inherit update-rc.d

do_install () {
    install -d ${D}/${sysconfdir}/init.d
    install -m 0755 ${WORKDIR}/hci-init-rpi.sh ${D}/${sysconfdir}/init.d/
}

INITSCRIPT_NAME = "hci-init-rpi.sh"
INITSCRIPT_PARAMS = "defaults 25"

FILES:${PN} = "${sysconfdir}/init.d"
