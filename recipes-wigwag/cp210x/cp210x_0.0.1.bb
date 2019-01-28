SUMMARY = "cp210x kernel module for working with the cp2108"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://CP210x_VCP_Linux_3.13.x_Release_Notes.txt;md5=629c14bf6b69dcab9c5d6d9766bb071c"

inherit module

PR = "r1"
PV = "0.1"

SRC_URI = "file://Makefile \
		file://CP210x_VCP_Linux_3.13.x_Release_Notes.txt \
          file://cp210x.c \
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