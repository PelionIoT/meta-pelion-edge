SUMMARY = "panic module to intentionally crash the kernel"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=12f884d2ae1ff87c09e5b7ccc2c4ca7e"

inherit module

PR = "r1"
PV = "0.1"

SRC_URI = "file://Makefile \
           file://panic.c \
           file://COPYING \
          "

S = "${WORKDIR}"

#FILES_${PN} += "/wigwag/system/other/*" 
#FILES_${PN}-dbg += "/wigwag/system/other/.debug" 

# The inherit of module.bbclass will automatically name module packages with
# "kernel-module-" prefix as required by the oe-core build environment.
#do_install() {
#     cd ${S}
#     install -d ${D}/wigwag/system/other
##shellcheck disable=SC2034     install -m 755 ${S}/panic.ko ${D}/wigwag/system/other/
#}