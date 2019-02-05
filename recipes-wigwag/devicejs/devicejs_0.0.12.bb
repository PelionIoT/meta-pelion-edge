DESCRIPTION = "devicejs"

LICENSE = "DEVICEJS-1"
LICENSE_FLAGS = "WigWagCommericalDeviceJS"
LIC_FILES_CHKSUM = "file://LICENSE;md5=4011f5b49f62dc7a25bef33807edc4bd"

BUILD_NUMBER_FILE = "/home/walt/bin/build_number"

DBN="/builds/walt/42/wwrelay-rootfs/tools/build-scrips2/data/config/builds"

inherit pkgconfig gitpkgv npm-base npm-install

# when using a tag don't use AUTOREV, or any of this crap:
#PV = "1.0+git${SRCPV}"
#PKGV = "1.0+git${GITPKGV}"
PR = "r4"
SRCREV="330c8ceaa82abff7a15f5da83be1f6b0daaf7cd6"
#SRCREV = "${AUTOREV}"
#SRC_URI="git://git@github.com/WigWagCo/devicejs-ng.git;protocol=ssh;branch=development;tag=v0.2.0-rc14"
#SRC_URI="git://git@github.com/WigWagCo/devicejs-ng.git;protocol=ssh;branch=development;tag=v0.2.0-rc28"
#SRC_URI="git://git@github.com/WigWagCo/devicejs-ng.git;protocol=ssh;branch=development;tag=v0.2.6"
SRC_URI="git://git@github.com/WigWagCo/devicejs-ng.git;protocol=ssh;branch=development"

S = "${WORKDIR}/git"

DEPENDS = "nodejs node-native udev avahi"
RDEPENDS_${PN} += " nodejs"

BBCLASSEXTEND = "native"

INHIBIT_PACKAGE_STRIP = "1"  


FILES_${PN} = "/wigwag/*"

# ************************
# ***REMOVE ME PLEASE ****
# ************************
do_package_qa () {
  echo "done"
}

do_configure(){
	oe_runnpm_native install -g node-gyp
}

do_compile() {
    cp ${STAGING_INCDIR}/avahi-compat-libdns_sd/dns_sd.h ${STAGING_INCDIR}/
    VER_FILE=${S}/version.json

    if [ -e $VER_FILE ] ; then
       rm $VER_FILE
    fi

    COMMIT=`git rev-parse HEAD`

   if [ -d ${DBN} ] ; then
       DBNmajor=$(cat ${DBN}/.major)
       DBNminor=$(cat "${DBN}"/.minor)
       DBNuptick=$(cat ${DBN}/.uptick)
       BNN="${DBNmajor}.${DBNminor}.${DBNuptick}"
    else
       BNN="0.0.0"
    fi

    # Build the version info file
    echo "{" > $VER_FILE
    echo  "   "  \"Build\" " : " \"${BNN}\", >> $VER_FILE
    echo  "   "  \"Copyright \" " : " \"Copyright © 2014,2015,2016 by WigWag\", >> $VER_FILE
    echo  "   "  \"License\" " : " \"TBD - License Link to go here\", >> $VER_FILE
    echo  "   "  \"Commit\" " : " \"$COMMIT\", >> $VER_FILE
    echo  "   "  \"Product Name\" " : " \"DeviceJS\" >> $VER_FILE
    echo "}" >> $VER_FILE


    # Obtain and export the Architecture for NPM / node-gyp
#     TARGET_ARCH=`echo $AR | awk -F '-' '{print $1}'`
#    NPM_ARCH=`echo $AR | awk -F '-' '{print $1}'`
#    export npm_config_arch=$ARCH



#    oe_runnpm install
#     npm install
    ARCH=`echo $AR | awk -F '-' '{print $1}'`
    PLATFORM=`echo $AR | awk -F '-' '{print $3}'`
    export npm_config_arch=$ARCH

#    npm install --build-from-source --target_arch=$ARCH --target_platform=linux
    cd ${S}
    oe_runnpm install --production

# nope... this does not exist in the devicebd-go devicedb:
#    cd ${S}/deps/devicedb-distributed
#     npm i --build-from-source --target_arch=$ARCH --target_platform=linux --production




}

do_install() {
    cd ${S}
    install -d ${D}/wigwag
    install -d ${D}/wigwag/etc
 #  install -d ${D}/wigwag/bin
    #cp -r ${S}/install/etc/* ${D}/wigwag/etc/
    #used during devicejs-ng:keyValueStore
    install -d ${D}/wigwag/devicejs-ng
    cp -r * ${D}/wigwag/devicejs-ng
}
