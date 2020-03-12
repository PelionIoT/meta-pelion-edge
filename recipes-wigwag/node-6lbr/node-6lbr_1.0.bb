DESCRIPTION = "6LBR for Node"

LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://README.md;md5=4336ad26bb93846e47581adc44c4514d"

inherit autotools pkgconfig gitpkgv

PV = "1.0+git${SRCPV}"
PKGV = "1.0+git${GITPKGV}"
PR = "r1"

SRCREV = "${AUTOREV}"
SRC_URI="git://git@github.com/armPelionEdge/node-6lbr.git;protocol=ssh;branch=master"

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

    npm install --build-from-source --target_arch=$ARCH --target_platform=linux --production

    if [ -e  ${S}/node_modules/aws-sdk/node_modules/xml2js/node_modules/sax/examples/switch-bench.js ] ; then
     rm ${S}/node_modules/aws-sdk/node_modules/xml2js/node_modules/sax/examples/switch-bench.js
 fi

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
