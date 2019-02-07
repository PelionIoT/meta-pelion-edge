DESCRIPTION = "High Performance Logging library for c"

#LICENSE = "CLOSED"
#LIC_FILES_CHKSUM = "file://index.js;md5=19b7b10a212c4a56cd7de36f5b13b889"
LICENSE = "DEVICEOS-1"
LICENSE_FLAGS="WigWagCommericalDeviceOS"
LIC_FILES_CHKSUM = "file://LICENSE;md5=543feeb21d5afbbe88012f44261f5217"

# libuv is built into the the installer deps, so we don't want yocto's
#DEPENDS = "libuv"
#RDEPENDS_${PN}+="libuv"

inherit pkgconfig gitpkgv
# autotools 

#INITSCRIPT_NAME = "deviceOS-watchdog"
#INITSCRIPT_PARAMS = "defaults" 
#"start 39 S . stop 31 0 6 ."

PV = "1.0+git${SRCPV}"
PKGV = "1.0+git${GITPKGV}"
PR = "r3"

SRCREV = "${AUTOREV}"
#SRCREV = "2dfe013c7b862ad1a6add97688ff89c65e2acb8d"
#SRCREV = "36b08685b19fc574aaf29933264377233cf454af"
SRC_URI = "git://git@github.com/WigWagCo/deviceOSWD.git;protocol=ssh;branch=master"

# ;tag=v1.2.6"

#DEBUG_OPTIONS = "-rdynamic -D_TW_TASK_DEBUG_THREADS_"
#GLIBCFLAG = "-D_USING_GLIBC_"
#CFLAGS = "${DEBUG_OPTIONS} ${GLIBCFLAG} -D_TW_DEBUG -I./include  -D__DEBUG"


S = "${WORKDIR}/git"
WSYS= "${D}/wigwag/system"

INHIBIT_PACKAGE_DEBUG_SPLIT = "1"
INHIBIT_PACKAGE_STRIP = "1"
#INSANE_SKIP_${PN} += "dev-so"
#INSANE_SKIP_${PN} += "dev-so staticdev"

#FILES_lib = "${D}${libdir}/*" 
#FILES_inc = "${D}${includedir}/TW/*"
#FILES_${PN} += "/wigwag/system/bin/deviceOSWD /wigwag/system/bin/deviceOSWD_dummy"
FILES_${PN} += "/wigwag/system/bin/*" 
#FILES_${PN}-staticdev += "/wigwag/system/*"
#FILES_${PN}-dbg += "/wigwag/system/bin/*"
#FILES_${PN}-dev += "/wigwag/system/bin/*"




do_configure () {
}

do_compile() {
    cd ${S}

        cd ${S}/deps
    # get the --host value from the AR var
    # so  AR=arm-poky-linux-gnueabi-ar
    # then TOOLCHAIN=arm-poky-linux-gnueabi
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
}

do_install() {
    cd ${S}
    install -d ${D}/wigwag/system/bin
    install -m 755 ${S}/deviceOSWD ${WSYS}/bin/
    install -m 755 ${S}/deviceOSWD_dummy ${WSYS}/bin/
    install -m 755 ${S}/deviceOSWD_a10_debug ${WSYS}/bin/
    install -m 755 ${S}/deviceOSWD_a10_relay ${WSYS}/bin/
    install -m 755 ${S}/deviceOSWD_dummy_debug ${WSYS}/bin/
    install -m 755 ${S}/deviceOSWD_a10_tiny841 ${WSYS}/bin/

}

