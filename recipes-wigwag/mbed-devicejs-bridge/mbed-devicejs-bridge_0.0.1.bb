DESCRIPTION = "a better git for igwag"

LICENSE = "CLOSED"
#LIC_FILES_CHKSUM = "file://index.js;md5=19b7b10a212c4a56cd7de36f5b13b889"

inherit autotools pkgconfig gitpkgv npm 

PV = "1.0+git${SRCPV}"
PKGV = "1.0+git${GITPKGV}"
SRCREV = "${AUTOREV}"
#SRC_URI = "git://git@github.com/git/git.git;protocol=ssh;tag=v2.15.0"
SRC_URI = "git://git@github.com/WigWagCo/mbed-devicejs-bridge;protocol=ssh;branch=master"
PR = "r2"


# https://github.com/WigWagCo/mbed-edge-websocket.git
SRC_URI="git://git@github.com/WigWagCo/mbed-devicejs-bridge;protocol=ssh;branch=master;name=bridge;destsuffix=git/bridge \
git://git@github.com/WigWagCo/mbed-edge-websocket.git;protocol=ssh;branch=master;name=edgejs;destsuffix=git/edgejs \
file://config-dev.json"
SRCREV_FORMAT = "bridge-edgejs"
SRCREV_bridge = "${AUTOREV}"
SRCREV_edgejs = "${AUTOREV}"



DEPENDS = "nodejs nodejs-native"
RDEPENDS_${PN} += " nodejs bash"

S = "${WORKDIR}/git"

BBCLASSEXTEND = "native"

INHIBIT_PACKAGE_STRIP = "1"  
#WSYS= "${D}/wigwag/system"

INSANE_SKIP_${PN} += "arch"
FILES_${PN} += "/wigwag/*" 

dbg(){
	echo -e "$1" >> /tmp/devicejs8.dbg
}

do_compile() {
    ARCH=`echo $AR | awk -F '-' '{print $1}'`
    PLATFORM=`echo $AR | awk -F '-' '{print $3}'`
    export npm_config_arch=$ARCH
    rm -rf /tmp/devicejs8.dbg
    dbg "ARCH=$ARCH"
    dbg "PLATFORM=$PLATFORM"
    dbg "npm_config_arch=$npm_config_arch"
    dbg "PATH=$PATH"
    dbg "NPNVERSION=$(npm --version)"
    dbg "NODEVERSION=$(node --version)"
    cd ${S}/bridge
    cp devicejs.json package.json
    oe_runnpm --arch=arm --target_arch=arm --target_platform=linux install 
    rm -rf node_modules/mbed-cloud-sdk/.venv
    cd ${S}/edgejs
    oe_runnpm --arch=arm --target_arch=arm --target_platform=linux install 
}

do_install() {
    cd ${S}/bridge
    install -d ${D}/wigwag
    install -d ${D}/wigwag/mbed
    install -d ${D}/wigwag/mbed/mbed-devicejs-bridge
    cp -r * ${D}/wigwag/mbed/mbed-devicejs-bridge
    cd ${S}/edgejs
    install -d ${D}/wigwag/mbed/mbed-edge-websocket
    cp -r * ${D}/wigwag/mbed/mbed-edge-websocket    
#    install -d ${D}/wigwag/mbed/mbed-cloud-edge-js
#    cp -r * ${D}/wigwag/mbed/mbed-cloud-edge-js    

    # config file for mbed-devicejs-bridge
    cp ${S}/../config-dev.json ${D}/wigwag/mbed/mbed-devicejs-bridge/config.json
}
