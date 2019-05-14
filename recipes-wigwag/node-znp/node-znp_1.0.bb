DESCRIPTION = "node module for Zigbee Network Protocol HA profile"

LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://README.md;md5=4336ad26bb93846e47581adc44c4514d"

inherit pkgconfig gitpkgv

PV = "1.0+git${SRCPV}"
PKGV = "1.0+git${GITPKGV}"
PR = "r1"

SRCREV = "3f287272d965595c4d840ecbce59aab641599f73"
SRC_URI="git://git@github.com/armPelionEdge/node-znp.git;protocol=ssh"

S = "${WORKDIR}/git"

BBCLASSEXTEND = "native"

INHIBIT_PACKAGE_STRIP = "1"  

FILES_${PN} = "/wigwag/devicejs/core/utils/node-znp"


do_compile() {
    cd ${S}
    export ARCH=`echo $AR | awk -F '-' '{print $1}'`
    export PLATFORM=`echo $AR | awk -F '-' '{print $3}'`
    export npm_config_arch=$ARCH
    export GYPFLAGS="-Dv8_can_use_fpu_instructions=false -Darm_version=7 -Darm_float_abi=hardfp"
    NGYP_OPTIONS="--without-snapshot --dest-cpu=arm --dest-os=linux --with-arm-float-abi=hardfp"
    CONFIG_OPTIONS="--host=arm-poky-linux-gnueabihf --target=arm-poky-linux-gnueabihf"

    npm install nan --production
    node-gyp configure
    node-gyp build
    npm install --production

}
do_package_qa() {
 echo "done"
}


do_install() {
    install -d ${D}/wigwag
    install -d ${D}/wigwag/devicejs
    install -d ${D}/wigwag/devicejs/core
    install -d ${D}/wigwag/devicejs/core/utils
    install -d ${D}/wigwag/devicejs/core/utils/node-znp
    cp -r ${S}/* ${D}/wigwag/devicejs/core/utils/node-znp
}

