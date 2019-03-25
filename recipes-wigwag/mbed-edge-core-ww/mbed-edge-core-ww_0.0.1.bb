SUMMARY = "mbed cloud edge"
SECTION = "examples" 
LICENSE = "CLOSED" 

inherit pkgconfig gitpkgv

SRC_URI = "git://git@github.com/ARMmbed/mbed-edge.git;protocol=ssh;branch=master \
file://mbed-edge-core"

PV = "1.0+git${SRCPV}"
PKGV = "1.0+git${GITPKGV}"
SRCREV = "${AUTOREV}"


#because we cannot get past python2 getting flagged even though we RDEPENDS python
INSANE_SKIP_${PN} += " build-deps file-rdeps"


PR = "r3"

DEPENDS="update-rc.d-native jansson libevent"
RDEPENDS_${PN}="python bash libevent jansson perl"


S = "${WORKDIR}/git"
WSYS= "${D}/wigwag/system"

mbed_USER  ?= "developer"
mbed_GROUP ?= "developer"

FILES_${PN} += "/wigwag/mbed-ww /wigwag/mbed-ww/* /etc/init.d /etc/init.d/*" 

#because we are just dumping everything, we have to disable some qa
INSANE_SKIP_${PN}+="debug-files staticdev ldflags"


inherit pkgconfig cmake

do_configure() {
    cd ${S}
    git submodule update --init --recursive
    mkdir build
}

do_compile() {
    cd ${S}
    cd build
    # build for first to claim. heavy debug for now
    cmake  -DTRACE_LEVEL=DEBUG -DFIRMWARE_UPDATE=ON -DFACTORY_MODE=ON ..
    make
}

do_install() {
    WORKSPACE=`pwd`/../git/
    install -d ${D}/etc/init.d/
    install -m 755 ${S}/../mbed-edge-core ${D}/etc/init.d/
    update-rc.d -r ${D} mbed-edge-core defaults 95 5
    install -d ${D}/wigwag/mbed-ww
    install -d ${D}/wigwag/mbed-ww/edge-core
    cp -r "${S}/." "${D}/wigwag/mbed-ww/edge-core/"
}

