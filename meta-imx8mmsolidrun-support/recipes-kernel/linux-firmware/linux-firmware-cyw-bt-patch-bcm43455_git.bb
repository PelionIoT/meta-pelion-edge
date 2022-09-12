SUMMARY = "Linux kernel firmware files from Cypress"
DESCRIPTION = "Cypress' Bluetoothpatchfile that is required by hciattach and Bluez"
HOMEPAGE = "https://github.com/murata-wireless/cyw-fmac-fw"
SECTION = "kernel"
LICENSE = "Proprietary"
LIC_FILES_CHKSUM = "file://LICENCE.cypress;md5=cbc5f665d04f741f1e006d2096236ba7"

PACKAGE_ARCH = "${MACHINE_ARCH}"

SRC_URI = "git://github.com/murata-wireless/cyw-bt-patch;protocol=https;branch=master"
SRCREV = "580abcb5b5f06c9ccfb1438b1f52d8bccdff57e6"
PV = "20201214"

S = "${WORKDIR}/git"

CYPRESS_CONFLICT_PACKAGE ?= "bcm43455"
CYPRESS_PART_BT ?= "4345C0"
CYPRESS_MODEL ?= "1MW"

do_compile[noexec] = "1"

do_install() {
    install -d ${D}${nonarch_base_libdir}/firmware/
    install -d ${D}${nonarch_base_libdir}/firmware/brcm

    install -m 0644 ${S}/LICENCE.cypress ${D}${nonarch_base_libdir}/firmware/LICENCE.cyw-bt-patch
    # 003.001.025.0144.0266
    install -m 0644 ${S}/BCM${CYPRESS_PART_BT}_*.${CYPRESS_MODEL}.hcd ${D}${nonarch_base_libdir}/firmware/brcm/BCM${CYPRESS_PART_BT}.hcd

    install -d ${D}/${sysconfdir}
    cd ${D}/${sysconfdir}
    ln -s ..${nonarch_base_libdir}/firmware firmware
}

FILES:${PN} = " \
    ${nonarch_base_libdir}/firmware/LICENCE.cyw-bt-patch \
    ${nonarch_base_libdir}/firmware/brcm/BCM${CYPRESS_PART_BT}.hcd \
    ${nonarch_base_libdir}/firmware/brcm/BCM${CYPRESS_PART_BT}.${CYPRESS_MODEL}.hcd \
    ${sysconfdir}/firmware \
"

RCONFLICTS:${PN} = "\
    linux-firmware-${CYPRESS_CONFLICT_PACKAGE} \
    linux-firmware-raspbian-${CYPRESS_CONFLICT_PACKAGE} \
"
RREPLACES:${PN} = "\
    linux-firmware-${CYPRESS_CONFLICT_PACKAGE} \
    linux-firmware-raspbian-${CYPRESS_CONFLICT_PACKAGE} \
"

COMPATIBLE_MACHINE_imx8mmsolidrun = ".*"
