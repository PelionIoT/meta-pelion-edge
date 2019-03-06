DESCRIPTION="mbed-edge-examples"

LICENSE="Apache-2.0"
LIC_FILES_CHKSUM = "file://${WORKDIR}/git/LICENSE;md5=1dece7821bf3fd70fe1309eaa37d52a2"

# Patches for quilt goes to files directory
FILESEXTRAPATHS_prepend := "${THISDIR}/files:"

SRC_URI = "git://git@github.com/ARMmbed/mbed-edge-examples.git;protocol=ssh; \
           file://pt-example \
           file://blept-example \
           file://blept-devices.json \
           file://0001-disable-doxygen.patch \
           file://0002-fix-libevent-build-with-CMake-in-Yocto.patch \
           file://0003-examples-0.8.0-should-use-mbed-edge-0.8.0.patch \
           file://0004-fix-CMake-test-Build-Type-Release.patch \
           "

SRCREV = "0.8.0"

# Installed packages
PACKAGES = "${PN} ${PN}-dbg"
FILES_${PN} += "/wigwag \
                /wigwag/mbed \
                /wigwag/mbed/pt-example \
                /wigwag/mbed/blept-example \
                /wigwag/mbed/blept-devices.json"

FILES_${PN}-dbg += "/wigwag/mbed/.debug \
                    /usr/src/debug/mbed-edge-examples"

S = "${WORKDIR}/git"

DEPENDS = " libcap mosquitto glib-2.0"
RDEPENDS_${PN} = " procps start-stop-daemon bash bluez5 virtual/mbed-edge-core"

EXTRA_OECMAKE += " \
    -DTARGET_TOOLCHAIN=yocto \
    -DCMAKE_BUILD_TYPE=Release \
    ${MBED_EDGE_CUSTOM_CMAKE_ARGUMENTS} "
inherit cmake

do_configure_prepend() {
    cd ${S}
    git submodule update --init --recursive
}

do_install() {
    install -d "${D}/wigwag/mbed"
    install "${WORKDIR}/build/bin/pt-example" "${D}/wigwag/mbed"
    install "${WORKDIR}/build/bin/blept-example" "${D}/wigwag/mbed"
    install "${WORKDIR}/blept-devices.json" "${D}/wigwag/mbed"
    install "${WORKDIR}/build/bin/mqttpt-example" "${D}/wigwag/mbed"
    install "${WORKDIR}/git/mqttpt-example/mqttgw_sim/mqtt_ep.sh" "${D}/wigwag/mbed"
    install "${WORKDIR}/git/mqttpt-example/mqttgw_sim/mqtt_gw.sh" "${D}/wigwag/mbed"

    install -d "${D}${sysconfdir}/logrotate.d"
    install -m 644 "${WORKDIR}/pt-example" "${D}${sysconfdir}/logrotate.d"
    install -m 644 "${WORKDIR}/blept-example" "${D}${sysconfdir}/logrotate.d"
}
