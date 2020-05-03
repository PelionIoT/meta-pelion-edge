DESCRIPTION = "Node module for network interface and routing manipulation"

LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=4336ad26bb93846e47581adc44c4514d"

inherit autotools pkgconfig gitpkgv

PV = "1.0+git${SRCPV}"
PKGV = "1.0+git${GITPKGV}"
PR = "r1"

SRCREV = "9b0c8cb53f3d1c9a70e13d942a941fb6fc970f5c"
SRC_URI="git://git@github.com/armPelionEdge/node-netkit.git;protocol=ssh;"

S = "${WORKDIR}/git"


BBCLASSEXTEND = "native"

INHIBIT_PACKAGE_STRIP = "1"  

FILES_${PN} = "/wigwag/devicejs/*"

do_package_qa () {
  echo "done"
}

do_compile() {
 cd ${S}
 export AR
 export LD=$CXX
 export LINK=$CXX
 export CC
 export CXX
 export RANLIB

 export GYPFLAGS="-Dv8_can_use_fpu_instructions=false -Darm_version=7 -Darm_float_abi=softfp"
 NGYP_OPTIONS="--without-snapshot --dest-cpu=arm --dest-os=linux --with-arm-float-abi=softfp"
 CONFIG_OPTIONS="--host=arm-poky-linux-gnueabi --target=arm-poky-linux-gnueabi"

 # Obtain and export the Architecture for NPM / node-gyp
 ARCH=`echo $AR | awk -F '-' '{print $1}'`
 export npm_config_arch=$ARCH

 node-gyp -d configure
 node-gyp build
}

do_install() {
  cd ${S}
  install -d ${D}/wigwag
  install -d ${D}/wigwag/devicejs
  install -d ${D}/wigwag/devicejs/core
  install -d ${D}/wigwag/devicejs/core/utils
  install -d ${D}/wigwag/devicejs/core/utils/node-netkit
  cp -r ${S}/build ${D}/wigwag/devicejs/core/utils/node-netkit
  cp -r ${S}/deps ${D}/wigwag/devicejs/core/utils/node-netkit
  cp -r ${S}/libs ${D}/wigwag/devicejs/core/utils/node-netkit
  cp -r ${S}/tests ${D}/wigwag/devicejs/core/utils/node-netkit
  cp ${S}/index.js ${D}/wigwag/devicejs/core/utils/node-netkit
  cp ${S}/package.json ${D}/wigwag/devicejs/core/utils/node-netkit
}

