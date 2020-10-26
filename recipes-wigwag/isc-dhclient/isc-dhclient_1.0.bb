DESCRIPTION = "Node binding for ISC dhclient"

LICENSE = "GPLv2+"
LIC_FILES_CHKSUM = "file://README.md;md5=cc38e3726b639d2c1274e47c3d90be91"

inherit pkgconfig gitpkgv npm

#DEPENDS+="libcap libcap-native"
#RDEPENDS_${PV}+="libcap"

PV = "1.0+git${SRCPV}"
PKGV = "1.0+git${GITPKGV}"
PR = "r1"

SRCREV = "b5aa812b2ebaf6c866f6c24fb176d71b1e6e28ba"
SRC_URI="git://git@github.com/armPelionEdge/node-isc-dhclient.git;protocol=https"

S = "${WORKDIR}/git"

BBCLASSEXTEND = "native"
DEPENDS = "libcap nodejs nodejs-native"
RDEPENDS_${PN} += "libcap nodejs"
FILES_${PN} = "/wigwag/devicejs-core-modules/*"

INHIBIT_PACKAGE_STRIP = "1"  


#thisbox
#DEPENDS = "libcap node libcap-native"

#Walts box

TARGET_CFLAGS += "-D_GNU_SOURCE"

do_configure(){
	oe_runnpm_native install -g node-gyp@5.1.1

}

do_compile() {
    cd ${S}
    echo "s: ${S}" > /tmp/isc-dhclient.log
    	gcc --version >> /tmp/isc-dhclient.log
    echo "Stage: ${STAGING_LIBDIR}" >> /tmp/isc-dhclient.log
    #waltsbox: cp /home/walt/arm-binding.gyp ./binding.gyp
    cp ./arm-binding.gyp ./binding.gyp
    cd ${S}/deps/isc-dhcp

    wget ftp://ftp.isc.org/isc/dhcp/4.3.0b1/dhcp-4.3.0b1.tar.gz
    wget http://wiki.beyondlogic.org/patches/dhcp-4.3.0b1.bind_arm-linux-gnueabi.patch
    wget http://wiki.beyondlogic.org/patches/bind-9.9.5rc1.gen_crosscompile.patch
    tar -xzf dhcp-4.3.0b1.tar.gz
    cd dhcp-4.3.0b1
    patch -p1 < ../dhcp-4.3.0b1.bind_arm-linux-gnueabi.patch
    cd bind
    tar -xzf bind.tar.gz
    cd bind-9.9.5rc1
    patch -p1 < ../../../bind-9.9.5rc1.gen_crosscompile.patch
    cd ../..
    #waltbox:
    ./configure --host=arm-linux-gnueabi --prefix= --build=i686-pc-linux-gnu ac_cv_file__dev_random=yes CFLAGS="-fPIC"
    #experiement_thisbox:
   # ./configure --with-libbind=/build-disk/test2/currentRelay/morty/poky/build/tmp/sysroots/cubietruck/usr/lib --host=arm-poky-linux-gnueabi --prefix= --build=i686-pc-linux-gnu ac_cv_file__dev_random=yes CFLAGS="-fPIC" --enable-paranoia --disable-static --with-randomdev=/dev/random
LD="$LD" AR="$AR" make    


#  LDFLAGS="$LDFLAGS -L/build-disk/test2/currentRelay/fido/poky/build/tmp/sysroots/x86_64-linux/lib -L/build-disk/test2/currentRelay/fido/poky/build/tmp/sysroots/cubieboard/lib"
#    ./configure --with-randomdev=no --without-ecdsa --without-gost  --host=arm-poky-linux-gnueabi \
#      --prefix= --build=i686-pc-linux-gnu ac_cv_file__dev_random=yes CFLAGS="-fPIC" LDFLAGS="$LDFLAGS -L/build-disk/test2/currentRelay/fido/poky/build/tmp/sysroots/cubieboard/lib  -L/build-disk/test2/currentRelay/fido/poky/build/tmp/sysroots/x86_64-linux/lib" 


#./configure --with-libbind=${STAGING_LIBDIR} --host=arm-poky-linux-gnueabi --prefix= --build=i686-pc-linux-gnu ac_cv_file__dev_random=yes CFLAGS="-fPIC" --enable-paranoia --disable-static --with-randomdev=/dev/random
    

    cd ${S}
    # Obtain and export the Architecture for NPM / node-gyp
    export ARCH=`echo $AR | awk -F '-' '{print $1}'`
    export PLATFORM=`echo $AR | awk -F '-' '{print $3}'`
    export npm_config_arch=$ARCH
    export GYPFLAGS="-Dv8_can_use_fpu_instructions=false -Darm_version=7 -Darm_float_abi=hardfp"
    NGYP_OPTIONS="--without-snapshot --dest-cpu=arm --dest-os=linux --with-arm-float-abi=hardfp"
    CONFIG_OPTIONS="--host=arm-poky-linux-gnueabihf --target=arm-poky-linux-gnueabihf"
    BDIR=${S}/deps/isc-dhcp/bind


    cd ${S}
    oe_runnpm install nan --production
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
    install -d ${D}/wigwag/devicejs-core-modules/node_modules/isc-dhclient
    cp -r ${S}/* ${D}/wigwag/devicejs-core-modules/node_modules/isc-dhclient

   # These files require /usr/local/bin/perl and need to be removed or yocto will not complete the build
   # without having /usr/local/bin/perl installed
   if [ -e ${D}/wigwag/devicejs-core-modules/node_modules/isc-dhclient/deps ]; then
      rm -rf  ${D}/wigwag/devicejs-core-modules/node_modules/isc-dhclient/deps
   fi 
}

