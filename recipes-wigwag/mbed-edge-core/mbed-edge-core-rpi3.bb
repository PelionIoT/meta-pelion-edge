require mbed-edge-core.inc

COMPATIBLE_MACHINE = "raspberrypi3"

PROVIDES += " virtual/mbed-edge-core virtual/mbed-edge-core-dbg "
RPROVIDES_${PN} += " virtual/mbed-edge-core virtual/mbed-edge-core-dbg "

FILESEXTRAPATHS_prepend := "${THISDIR}/files/rpi3:"
SRC_URI += "file://target.cmake \
            file://sotp_fs_rpi3_yocto.h"

do_install_append() {
    install -m 755 "${SCRIPT_DIR}/arm_update_cmdline.sh"                  "${D}/wigwag/mbed"
    install -m 755 "${SCRIPT_DIR}/yocto_rpi/arm_update_activate.sh"       "${D}/wigwag/mbed"
    install -m 755 "${SCRIPT_DIR}/yocto_rpi/arm_update_active_details.sh" "${D}/wigwag/mbed"
}
