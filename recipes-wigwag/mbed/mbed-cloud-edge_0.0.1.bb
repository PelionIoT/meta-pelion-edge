SUMMARY = "mbed cloud edge"
SECTION = "examples" 
LICENSE = "CLOSED" 

inherit pkgconfig gitpkgv



# --------START: Lets use a branch --------------------------------------------------
#next 3 vars are required for a branch
#PV = "1.0+git${SRCPV}"
#PKGV = "1.0+git${GITPKGV}"
#SRCREV = "${AUTOREV}"
#lets use development branch
#SRC_URI = "git://git@github.com/WigWagCo/mbed-cloud-edge-confidential-w.git;protocol=ssh;branch=fcc_kcm_patch_1.2.4"
# --------END: Lets use a branch ------------------------------------------------

#--------START: Lets use a tag --------------------------------------------------
# search for TRM if your making a change here
#for when you want tags (comment out PKGV and SRCREV)
#SRC_URI="git://git@github.com/WigWagCo/devicejs-ng.git;protocol=ssh;branch=development;tag=v0.2.0-rc29"
#SRC_URI = "git://git@github.com/WigWagCo/mbed-cloud-edge-confidential-w.git;protocol=ssh;tag=CR-0.4.2-EA"
#SRC_URI = "git://git@github.com/WigWagCo/mbed-edge.git;protocol=ssh;tag=R0.4.3"
#SRC_URI = "git://git@github.com/WigWagCo/mbed-edge.git;protocol=ssh;tag=R0.4.3fixrot"

#SRC_URI = "git://git@github.com/WigWagCo/mbed-edge.git;protocol=ssh;nobranch=1;tag=R0.4.4fixrot \
#file://mbed-edge-core"

# use gitsm:// to clone repo and all its submodules. Arm has decided to use submodules on this
#SRC_URI = "gitsm://git@github.com/WigWagCo/mbed-edge.git;protocol=ssh;branch=master \
#file://mbed-edge-core"

BUILDTAG="0.6.0-RC3"
SRC_URI = "gitsm://git@github.com/WigWagCo/mbed-edge-sources-internal.git;protocol=ssh;tag=${BUILDTAG} \
file://mbed-edge-core"
PV = "1.0+git${SRCPV}"
PKGV = "1.0+git${GITPKGV}"
SRCREV = "${BUILDTAG}"
# use this for branch
#SRCREV = "${AUTOREV}"


#because we cannot get past python2 getting flagged even though we RDEPENDS python
INSANE_SKIP_${PN} += " build-deps file-rdeps"


# --------END: Lets use a tag ----------------------------------------------------
PR = "r3"

DEPENDS="update-rc.d-native jansson libevent"
RDEPENDS_${PN}="python bash libevent jansson perl"


S = "${WORKDIR}/git"
WSYS= "${D}/wigwag/system"

mbed_USER  ?= "developer"
mbed_GROUP ?= "developer"

FILES_${PN} += "/wigwag/mbed /wigwag/mbed/* /etc/init.d /etc/init.d/*" 

#because we are just dumping everything, we have to disable some qa
INSANE_SKIP_${PN}+="debug-files staticdev ldflags"


inherit pkgconfig cmake

do_configure() {
#	pwd >> /tmp/thepwd.pwd
#	cd ../git
#	./build_mbed_edge.sh
    cd ${S}
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
	echo $WORKSPACE > /tmp/yocto.out
    install -d ${D}/etc/init.d/
    install -m 755 ${S}/../mbed-edge-core ${D}/etc/init.d/
    update-rc.d -r ${D} mbed-edge-core defaults 85 15
    install -d ${D}/wigwag/mbed
 #   install -o=1202 -d ${D}/wigwag/mbed/edge-core
    install -d ${D}/wigwag/mbed/edge-core
  #  chown ${mbed_USER}:${mbed_GROUP} -R ${D}/wigwag/mbed/edge-core
    echo "${D}/wigwag/mbed/edge-core/" > /tmp/yo.out
    cp -r "${S}/." "${D}/wigwag/mbed/edge-core/"
}
#inherit useradd-exampleUSERADD_PACKAGES = "${PN}"
