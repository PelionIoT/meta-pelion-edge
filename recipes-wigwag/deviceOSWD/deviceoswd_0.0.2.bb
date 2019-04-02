DESCRIPTION = "Watchdog controller for edgeOS"

LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=4336ad26bb93846e47581adc44c4514d"


RDEPENDS_${PN}+="bash"

inherit pkgconfig gitpkgv update-rc.d

INITSCRIPT_NAME = "deviceOS-watchdog"
INITSCRIPT_PARAMS = "defaults 60 40"

PV = "1.0+git${SRCPV}"
PKGV = "1.0+git${GITPKGV}"
PR = "r4"

SRCREV = "${AUTOREV}"
SRC_URI = "git://git@github.com/WigWagCo/deviceOSWD.git;protocol=ssh;branch=master \
file://deviceOS-watchdog \
"

S="${WORKDIR}/git"
WSYS="${D}/wigwag/system"

INHIBIT_PACKAGE_DEBUG_SPLIT = "1"
INHIBIT_PACKAGE_STRIP = "1"

FILES_${PN} += "/wigwag/system/bin/* ${INIT_D_DIR}/*"




do_compile() {
    cd ${S}

    cd ${S}/deps
    TOOLCHAIN=`echo $AR | sed 's/.\{3\}$//'` ./install-deps.sh

    cd ${S}
    make clean
    make deviceOSWD-dummy
    cp deviceOSWD deviceOSWD_dummy

    make clean
    make deviceOSWD-dummy-debug
    cp deviceOSWD deviceOSWD_dummy_debug

    make clean
    make deviceOSWD-a10-debug
    cp deviceOSWD deviceOSWD_a10_debug

    make clean
    make deviceOSWD-a10

    make clean
    make deviceOSWD-a10-relay
    cp deviceOSWD deviceOSWD_a10_relay

    make clean
    make deviceOSWD-a10-tiny841
    cp deviceOSWD deviceOSWD_a10_tiny841

    make clean
    make deviceOSWD-rpi-3bplus
    cp deviceOSWD deviceOSWD_rpi_3bplus
}

do_install() {
    cd ${S}
    install -d ${D}${INIT_D_DIR}
    install -d ${D}/wigwag/system/bin
    install -m 755 ${S}/deviceOSWD ${WSYS}/bin/
    install -m 755 ${S}/deviceOSWD_dummy ${WSYS}/bin/
    install -m 755 ${S}/deviceOSWD_a10_debug ${WSYS}/bin/
    install -m 755 ${S}/deviceOSWD_a10_relay ${WSYS}/bin/
    install -m 755 ${S}/deviceOSWD_dummy_debug ${WSYS}/bin/
    install -m 755 ${S}/deviceOSWD_a10_tiny841 ${WSYS}/bin/
    install -m 755 ${S}/../deviceOS-watchdog ${D}${INIT_D_DIR}/deviceOS-watchdog

}

