SUMMARY = "cp210x test program"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://CP210x_VCP_Linux_3.13.x_Release_Notes.txt;md5=629c14bf6b69dcab9c5d6d9766bb071c"

inherit module

PR = "r1"
PV = "0.1"

SRC_URI = "file://Makefile \
file://CP210x_VCP_Linux_3.13.x_Release_Notes.txt \
file://cp210x_gpio_example.cpp \
"
WSYSS= "${D}/wigwag/system/share"
WSYSL= "${D}/wigwag/system/lib"
WSYSB= "${D}/wigwag/system/bin"
WSYSO= "${D}/wigwag/system/other"

INHIBIT_PACKAGE_DEBUG_SPLIT = "1"
INHIBIT_PACKAGE_STRIP = "1"

S = "${WORKDIR}"

FILES_${PN} += "/wigwag/system/other/*" 
#FILES_${PN}-dbg += "/wigwag/system/other/.debug" 

do_package_qa () {
  echo "done"
}

do_compile(){
	make
}
# The inherit of module.bbclass will automatically name module packages with
# "kernel-module-" prefix as required by the oe-core build environment.
do_install() {
	cd ${S}
    	install -d ${WSYSO}
	install -m 755 ${S}/cp210xtest ${WSYSO}
}