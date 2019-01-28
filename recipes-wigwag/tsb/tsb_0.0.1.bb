DESCRIPTION = "tiny safe boot control program"

LICENSE = "GPLv3+"
LIC_FILES_CHKSUM = "file://COPYING;md5=d32239bcb673463ab874e80d47fae504"

inherit autotools pkgconfig gitpkgv



PV = "1.0+git${SRCPV}"
PKGV = "1.0+git${GITPKGV}"
PR = "r3"

SRCREV = "${AUTOREV}"
#SRC_URI = "git://git@github.com/WigWagCo/deviceOSWD.git;protocol=ssh"
SRC_URI = "git://git@github.com/WigWagCo/tsb-c.git;protocol=ssh"

S = "${WORKDIR}/git"

BBCLASSEXTEND = "native"

INHIBIT_PACKAGE_STRIP = "1"  


WSYSS= "${D}/wigwag/system/share"
WSYSL= "${D}/wigwag/system/lib"
WSYSB= "${D}/wigwag/system/bin"
WSYSO= "${D}/wigwag/system/other"

INHIBIT_PACKAGE_DEBUG_SPLIT = "1"
INHIBIT_PACKAGE_STRIP = "1"



FILES_${PN} += "/wigwag/system/bin/*" 
#FILES_${PN}-dbg += "/wigwag/system/other/.debug" 

do_configure() {
    cd ${S}
    autoreconf --install
    autoconf
    automake
    ./configure CFLAGS=-std=c11 --host=arm
}

do_compile(){
  cd ${S}/src/
  make
}
# The inherit of module.bbclass will automatically name module packages with
# "kernel-module-" prefix as required by the oe-core build environment.
do_install() {
  cd ${S}
  install -d ${WSYSB}
  install -m 755 ${S}/src/tsb ${WSYSB}/
}
