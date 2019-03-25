DESCRIPTION = "Node binding udev"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=4336ad26bb93846e47581adc44c4514d"

inherit pkgconfig gitpkgv npm-base npm-install
#npm-install-global

PV = "1.0+git${SRCPV}"
PKGV = "1.0+git${GITPKGV}"
PR = "r1"

SRCREV = "${AUTOREV}"
SRC_URI="git://git@github.com/WigWagCo/node-hotplug.git;protocol=ssh"

S = "${WORKDIR}/git"

BBCLASSEXTEND = "native"
DEPENDS = "libcap nodejs node-native udev"
RDEPENDS_${PN} += "nodejs libcap"
FILES_${PN} = "/wigwag/devicejs-core-modules/*"

INHIBIT_PACKAGE_STRIP = "1"  



do_configure(){
	oe_runnpm_native install -g node-gyp
}

#https://github.com/imyller/meta-nodejs/issues/39
do_compile() {
    cd ${S}
    # Obtain and export the Architecture for NPM / node-gyp
    export ARCH=`echo $AR | awk -F '-' '{print $1}'`
    export PLATFORM=`echo $AR | awk -F '-' '{print $3}'`
    export npm_config_arch=$ARCH
    export GYPFLAGS="-Dv8_can_use_fpu_instructions=false -Darm_version=7 -Darm_float_abi=hardfp"
    NGYP_OPTIONS="--without-snapshot --dest-cpu=arm --dest-os=linux --with-arm-float-abi=hardfp"
    CONFIG_OPTIONS="--host=arm-poky-linux-gnueabihf --target=arm-poky-linux-gnueabihf"


    cd ${S}
    npm install nan --production
#    oe_runnpm install nan --production
    node-gyp configure
    node-gyp build 
}

do_package_qa() {
   echo "done"
}


do_install() {
    install -d ${D}/wigwag
    install -d ${D}/wigwag/devicejs-core-modules
    install -d ${D}/wigwag/devicejs-core-modules/node_modules
    install -d ${D}/wigwag/devicejs-core-modules/node_modules/node-udev
    cp -r ${S}/* ${D}/wigwag/devicejs-core-modules/node_modules/node-udev

   # These files require /usr/local/bin/perl and need to be removed or yocto will not complete the build
   # without having /usr/local/bin/perl installed
   if [ -e ${D}/wigwag/devicejs-core-modules/node_modules/isc-dhclient/deps ]; then
      rm -rf  ${D}/wigwag/devicejs-core-modules/node_modules/isc-dhclient/deps
   fi 
}

