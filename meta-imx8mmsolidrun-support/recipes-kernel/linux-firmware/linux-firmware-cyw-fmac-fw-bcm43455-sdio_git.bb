SUMMARY = "Linux kernel firmware files from Cypress"
DESCRIPTION = "Cypress' WLAN firmware with customized CLM Blob"
HOMEPAGE = "https://github.com/murata-wireless/cyw-fmac-fw"
SECTION = "kernel"
LICENSE = "Proprietary"
LIC_FILES_CHKSUM = "file://LICENCE;md5=cbc5f665d04f741f1e006d2096236ba7"

PACKAGE_ARCH = "${MACHINE_ARCH}"

SRC_URI = "git://github.com/murata-wireless/cyw-fmac-fw;protocol=https;branch=master"
SRCREV = "ba140e42c3320262fc52e185c3af93eeb10117df"
PV = "20210112"

S = "${WORKDIR}/git"

CYPRESS_PART ?= "43455-sdio"
CYPRESS_MODEL ?= "1MW"

do_install() {
    install -d ${D}${nonarch_base_libdir}/firmware/
    install -d ${D}${nonarch_base_libdir}/firmware/brcm

    install -m 0644 ${S}/LICENCE ${D}${nonarch_base_libdir}/firmware/LICENCE.cyw-fmac-fw
    install -m 0644 ${S}/cyfmac${CYPRESS_PART}.bin ${D}${nonarch_base_libdir}/firmware/brcm/brcmfmac${CYPRESS_PART}.bin
    install -m 0644 ${S}/cyfmac${CYPRESS_PART}.${CYPRESS_MODEL}.clm_blob ${D}${nonarch_base_libdir}/firmware/brcm/brcmfmac${CYPRESS_PART}.clm_blob
}

FILES:${PN} = " \
    ${nonarch_base_libdir}/firmware/LICENCE.cyw-fmac-fw \
    ${nonarch_base_libdir}/firmware/brcm/brcmfmac${CYPRESS_PART}.bin \
    ${nonarch_base_libdir}/firmware/brcm/brcmfmac${CYPRESS_PART}.clm_blob \
"

COMPATIBLE_MACHINE_imx8mmsolidrun = ".*"
