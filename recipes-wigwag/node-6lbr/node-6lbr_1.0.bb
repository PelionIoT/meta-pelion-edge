DESCRIPTION = "6LBR for Node"

LICENSE = "DEVICEPROTOCOL-1"
LICENSE_FLAGS = "WigWagCommericalDeviceProtocol"
LIC_FILES_CHKSUM = "file://README.md;md5=a3673749692f7c5f15f3650ee4abea47"

inherit autotools pkgconfig gitpkgv

PV = "1.0+git${SRCPV}"
PKGV = "1.0+git${GITPKGV}"
PR = "r1"

SRCREV = "${AUTOREV}"
SRC_URI="git://git@github.com/WigWagCo/node-6lbr.git;protocol=ssh;branch=master"

S = "${WORKDIR}/git"

DEPENDS = "node-native twlib"

BBCLASSEXTEND = "native"

INHIBIT_PACKAGE_STRIP = "1"  

FILES_${PN} = "/wigwag/devicejs/core/utils/node-6lbr \
               /wigwag/devicejs/core/utils/node_modules"


do_package_qa () {
  echo "done"
}

do_compile() {
    cd ${S}
    /bin/rm -rf ${S}/build
    # Obtain and export the Architecture for NPM / node-gyp
    ARCH=`echo $AR | awk -F '-' '{print $1}'`
    PLATFORM=`echo $AR | awk -F '-' '{print $3}'`
    export npm_config_arch=$ARCH

#
# Yocto finds this file with a need for /usr/local/bin/node-bench
# If it does not exist, the build will fail
#
    npm install --build-from-source --target_arch=$ARCH --target_platform=linux --production

#     node-gyp -d configure
#     node-gyp -d build

    if [ -e  ${S}/node_modules/aws-sdk/node_modules/xml2js/node_modules/sax/examples/switch-bench.js ] ; then
       rm ${S}/node_modules/aws-sdk/node_modules/xml2js/node_modules/sax/examples/switch-bench.js
    fi

#    ./node_modules/.bin/node-pre-gyp package --target_arch=$ARCH --target_platform=$PLATFORM
#    ./node_modules/.bin/node-pre-gyp publish --target_arch=$ARCH --target_platform=$PLATFORM
}

do_install() {
    install -d ${D}/wigwag
    install -d ${D}/wigwag/devicejs
    install -d ${D}/wigwag/devicejs/core
    install -d ${D}/wigwag/devicejs/core/utils
    install -d ${D}/wigwag/devicejs/core/utils/node-6lbr
    install -d ${D}/wigwag/devicejs/core/utils/node_modules
  
    cp -r ${S}/build ${D}/wigwag/devicejs/core/utils/node-6lbr
    cp -r ${S}/slip-radio_bin ${D}/wigwag/devicejs/core/utils/node-6lbr
    cp -r ${S}/node_modules ${D}/wigwag/devicejs/core/utils/node-6lbr
    cp ${S}/index.js ${D}/wigwag/devicejs/core/utils/node-6lbr

}
