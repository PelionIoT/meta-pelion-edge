DESCRIPTION="mbed-edge-core"

LICENSE="Apache-2.0"
LIC_FILES_CHKSUM = "file://${WORKDIR}/git/LICENSE;md5=1dece7821bf3fd70fe1309eaa37d52a2"
SCRIPT_DIR = "${WORKDIR}/git/lib/mbed-cloud-client/update-client-hub/modules/pal-linux/scripts"

MBED_EDGE_CORE_CONFIG_TRACE_LEVEL ?= "DEBUG"
MBED_EDGE_CORE_CONFIG_FIRMWARE_UPDATE ?= "OFF"
MBED_EDGE_CORE_CONFIG_FACTORY_MODE ?= "OFF"

# Patches for quilt goes to files directory
FILESEXTRAPATHS_prepend := "${THISDIR}/files:"

SRC_URI = "git://git@github.com/ARMmbed/mbed-edge.git;protocol=ssh; \
           file://edge-core.init \
           file://edge-core.logrotate \
           file://toolchain.cmake \
           file://0001-disable-Doxygen.patch \
           file://0002-fix-libevent-build-with-CMake-in-Yocto.patch \
           file://0003-fix-compile-error-when-building-for-Release.patch"
SRC_URI += "\
    ${@bb.utils.contains('MBED_EDGE_CORE_CONFIG_FACTORY_MODE','OFF','file://mbed_cloud_dev_credentials.c','',d)} \
"

SRCREV = "0.8.0"

RDEPENDS_${PN} = " procps start-stop-daemon bash tar bzip2"

# Installed packages
PACKAGES = "${PN} ${PN}-dbg"
FILES_${PN} += "/wigwag \
                /wigwag/mbed \
                /wigwag/mbed/edge-core"

FILES_${PN}-dbg += "/wigwag/mbed/.debug \
                    /usr/src/debug/mbed-edge"

S = "${WORKDIR}/git"

EXTRA_OECMAKE += " \
    -DCMAKE_BUILD_TYPE=Release \
    -DTARGET_TOOLCHAIN=yocto \
    -DTARGET_CONFIG_ROOT=${WORKDIR} \
    -DTRACE_LEVEL=${MBED_EDGE_CORE_CONFIG_TRACE_LEVEL} \
    -DFIRMWARE_UPDATE=${MBED_EDGE_CORE_CONFIG_FIRMWARE_UPDATE} \
    -DFACTORY_MODE=${MBED_EDGE_CORE_CONFIG_FACTORY_MODE} \
    ${MBED_EDGE_CORE_CUSTOM_CMAKE_ARGUMENTS}"

inherit cmake update-rc.d

INITSCRIPT_NAME = "edge-core.sh"
INITSCRIPT_PARAMS = "defaults 85 15"

do_configure_prepend() {
    cd ${S}
    git submodule update --init --recursive
    [ -f "${WORKDIR}/mbed_cloud_dev_credentials.c" ] && {
        mv "${WORKDIR}/mbed_cloud_dev_credentials.c" config/
    }
    cd ${WORKDIR}/build
}

do_install() {
    install -d "${D}/etc/init.d/"
    install -m 755 "${WORKDIR}/edge-core.init" "${D}/etc/init.d/${INITSCRIPT_NAME}"

    install -d "${D}/wigwag/mbed/"
    install "${WORKDIR}/build/bin/edge-core" "${D}/wigwag/mbed/"

    install -d "${D}${sysconfdir}/logrotate.d/"
    install -m 644 "${WORKDIR}/edge-core.logrotate" "${D}${sysconfdir}/logrotate.d"
}

