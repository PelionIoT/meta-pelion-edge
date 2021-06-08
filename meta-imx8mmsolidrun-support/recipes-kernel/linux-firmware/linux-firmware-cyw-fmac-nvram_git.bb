SUMMARY = "Linux kernel firmware files from Cypress"
DESCRIPTION = "Cypress' WLAN NVRAM files"
HOMEPAGE = "https://github.com/murata-wireless/cyw-fmac-nvram"
SECTION = "kernel"
LICENSE = "Proprietary"
LIC_FILES_CHKSUM = "file://LICENCE.cypress;md5=cbc5f665d04f741f1e006d2096236ba7"

PACKAGE_ARCH = "${MACHINE_ARCH}"

SRC_URI = "git://github.com/murata-wireless/cyw-fmac-nvram;protocol=https"
SRCREV = "8710e74e79470f666912c3ccadf1e354d6fb209c"
PV = "20210112"

S = "${WORKDIR}/git"

CYPRESS_PART ?= "43455-sdio"
CYPRESS_MODEL ?= "1MW"

do_install() {
    install -d ${D}${nonarch_base_libdir}/firmware/
    install -d ${D}${nonarch_base_libdir}/firmware/brcm

    install -m 0644 ${S}/LICENCE.cypress ${D}${nonarch_base_libdir}/firmware/LICENCE.cyw-fmac-nvram
    install -m 0644 ${S}/cyfmac${CYPRESS_PART}.${CYPRESS_MODEL}.txt ${D}${nonarch_base_libdir}/firmware/brcm/brcmfmac${CYPRESS_PART}.txt
}

FILES_${PN} = " \
    ${nonarch_base_libdir}/firmware/LICENCE.cyw-fmac-nvram \
    ${nonarch_base_libdir}/firmware/brcm/brcmfmac${CYPRESS_PART}.txt \
"
