DESCRIPTION = "devicejs message bus"

LICENSE = "DEVICEJS-1"
LICENSE_FLAGS = "WigWagCommericalDeviceJS"
LIC_FILES_CHKSUM = "file://LICENSE;md5=4011f5b49f62dc7a25bef33807edc4bd"

inherit pkgconfig gitpkgv npm-base npm-install

PR = "r4"
SRC_URI = "git://git@github.com/WigWagCo/devicejs-ng.git;protocol=ssh;branch=development"

SRCREV = "v0.2.6"

S = "${WORKDIR}/git"

DEPENDS = "nodejs node-native udev avahi"
RDEPENDS_${PN} += " nodejs"

BBCLASSEXTEND = "native"

INHIBIT_PACKAGE_STRIP = "1"  

FILES_${PN} = "/wigwag/*"

do_configure(){
	oe_runnpm_native install -g node-gyp
}

do_compile() {
  cp ${STAGING_INCDIR}/avahi-compat-libdns_sd/dns_sd.h ${STAGING_INCDIR}/
  ARCH=`echo $AR | awk -F '-' '{print $1}'`
  PLATFORM=`echo $AR | awk -F '-' '{print $3}'`
  export npm_config_arch=$ARCH
  cd ${S}
  oe_runnpm install --production
}

do_install() {
  cd ${S}
  install -d ${D}/wigwag
  install -d ${D}/wigwag/etc
  install -d ${D}/wigwag/devicejs-ng
  cp -r * ${D}/wigwag/devicejs-ng
}
