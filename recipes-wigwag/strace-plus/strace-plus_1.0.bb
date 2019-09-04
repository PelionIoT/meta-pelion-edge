DESCRIPTION = "Strace+ "

LICENSE = "GPLv2+"
LIC_FILES_CHKSUM = "file://README;md5=d9a09d5e69d5636169f1c42d643a18a1"

inherit autotools pkgconfig gitpkgv

PV = "1.0+git${SRCPV}"
PKGV = "1.0+git${GITPKGV}"
PR = "r1"

SRCREV = "748ba45eaf506558671d018a18302877c4b237b7"
SRC_URI="git://git@github.com/pgbovine/strace-plus.git;protocol=ssh"


S = "${WORKDIR}/git"

BBCLASSEXTEND = "native"

INHIBIT_PACKAGE_STRIP = "1"  

FILES_${PN} = "/wigwag/devicejs/core/utils/strace-plus"

do_package_qa () {
  echo "done"
}

do_compile() {
    cd ${S}
    # Obtain and export the Architecture for NPM / node-gyp
    ARCH=`echo $AR | awk -F '-' '{print $1}'`
    PLATFORM=`echo $AR | awk -F '-' '{print $3}'`

    autoreconf -f -i
    ./configure --host=arm-poky-linux-gnueabi
    make
}
do_install() {
    install -d ${D}/wigwag
    install -d ${D}/wigwag/devicejs
    install -d ${D}/wigwag/devicejs/core
    install -d ${D}/wigwag/devicejs/core/utils
    install -d ${D}/wigwag/devicejs/core/utils/stracd-plus
  
    cp -r ${S}/* ${D}/wigwag/devicejs/core/utils/strace-plus

}

