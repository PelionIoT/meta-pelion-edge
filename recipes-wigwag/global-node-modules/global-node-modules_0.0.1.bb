DESCRIPTION = "DeviceJS Core Modules"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://wcm/LICENSE;md5=4011f5b49f62dc7a25bef33807edc4bd"

inherit pkgconfig gitpkgv npm-base

PV = "1.0+git${SRCPV}"
PKGV = "1.0+git${GITPKGV}"
PR = "r6"

SRC_URI="git://git@github.com/WigWagCo/wigwag-core-modules.git;protocol=ssh;branch=development;name=wcm;destsuffix=git/wcm \
git://git@github.com/WigWagCo/devicejs-core-modules.git;protocol=ssh;branch=development;name=dcm;destsuffix=git/dcm"
SRCREV_FORMAT = "dcm-wcm"
SRCREV_dcm = "${AUTOREV}"
SRCREV_wcm = "${AUTOREV}"

S = "${WORKDIR}/git"
WSYS= "${D}/wigwag/system"
WBIN= "${D}/wigwag/system/bin"

BBCLASSEXTEND = "native"
DEPENDS = "nodejs avahi udev nodejs-native"
RDEPENDS_${PN} += " nodejs"
FILES_${PN} = "/wigwag/*"

INHIBIT_PACKAGE_STRIP = "1"

do_rmnow () {
	if [ -e "${1}" ] ; then
		rm -rf "${1}"
	fi
}

do_package_qa () {
	echo "done"
}

do_configure() {
	cd ${S}
	cp ${STAGING_INCDIR}/avahi-compat-libdns_sd/dns_sd.h ${STAGING_INCDIR}/
	whereisgyp=$(which node-gyp) || :  #  force a return code of 0 always, so bitbake doesn't crash
	if [[ $whereisgyp = "" ]]; then
		oe_runnpm_native install -g node-gyp
	fi
	mkdir combo > /dev/null 2>&1 || :
	cd combo
	cp -R ../wcm/* . >> /dev/null
	cp -R ../dcm/* . >> /dev/null
	echo -en "{\n\"devjs-configurator\": \"http://github.com/WigWagCo/devjs-configurator#maestroRunner\",\n\"netkit\": \"git+ssh://git@github.com:WigWagCo/node-netkit.git\"\n}\n" > /tmp/overrides.json
	cd ../../
	#--------------------devjs-production-tools-----------------------------------------------------------
	cd ${S}/../
	if [[ ! -e devjs-production-tools ]]; then
		echo "devjs-production-tools does not exist" >> /tmp/global-node-modules.log
		git clone https://github.com/WigWagCo/devjs-production-tools
	else
		echo "devjs-production-tools exists, lets pull" >>/tmp/global-node-modules.log
		cd devjs-production-tools
		git pull >> /tmp/global-node-modules.log
		cd ..
	fi
}


do_compile() {
	echo -e "do_compile()\n----------------------------------------" >> /tmp/global-node-modules.log
	export AR
	export LD=$CXX
	export LINK=$CXX
	export CC
	export CXX
	export RANLIB
	export GYPFLAGS="-Dv8_can_use_fpu_instructions=false -Darm_version=7 -Darm_float_abi=hardfp"
	NGYP_OPTIONS="-v --without-snapshot --dest-cpu=arm --dest-os=linux --with-arm-float-abi=hardfp"
	export CONFIG_OPTIONS="--host=arm-poky-linux-gnueabihp --target=arm-poky-linux-gnueabihp"
	ARCH=`echo $AR | awk -F '-' '{print $1}'`
	export npm_config_arch=$ARCH
	NPM_ARCH=$ARCH
	cd ${S}/../devjs-production-tools
	oe_runnpm_native install
	cd ${S}/combo/
	rm -f package.json || :
	rm -f package-lock.json || :
	node ../../devjs-production-tools/consolidator.js -O /tmp/overrides.json -d grease-log -d dhclient ../wcm/* ../dcm/* ../dcm/wigwag-devices
	sed -i '/isc-dhclient/d' ./package.json
	sed -i '/node-hotplug/d' ./package.json
	NPM_FLAGS="--target_arch=$ARCH --target_platform=linux --loglevel silly"
	oe_runnpm install --target_arch=$ARCH --target_platform=linux --loglevel silly node-expat iconv bufferutil@3.0.5 >> npm-first.log 2>&1
	rm package-lock.json || :
	oe_runnpm --target_arch=$ARCH --target_platform=linux --loglevel silly install >> npm-second.log 2>&1
	rm package-lock.json || :
 	#oe_runnpm --target_arch=$ARCH --target_platform=linux --loglevel silly install git+ssh://git@github.com:WigWagCo/node-6lbr.git#May10Ship >> npm-third.log 2>&1
	#oe_runnpm --target_arch=$ARCH --target_platform=linux --loglevel silly install git+ssh://git@github.com:WigWagCo/greaseLog#arm-node8 >> npm.log 2>&1
}


do_install() {
	cd ${S}
	install -d ${D}/wigwag
	install -d ${D}/wigwag/devicejs-core-modules
	install -d ${D}/wigwag/devicejs-core-modules/node_modules
	install -d ${D}/wigwag/system/bin
	cp -r ${S}/combo/package.json ${D}/wigwag/devicejs-core-modules/
	cp -r ${S}/combo/node_modules/* ${D}/wigwag/devicejs-core-modules/node_modules/
	cp -r ${S}/dcm/* ${D}/wigwag/devicejs-core-modules
	cp -r ${S}/dcm/.b ${D}/wigwag/devicejs-core-modules
	do_rmnow ${D}/wigwag/devicejs-core-modules/BACnet/deps
	do_rmnow ${D}/wigwag/devicejs-core-modules/rsmi/bin/cc2530prog-x86
	do_rmnow ${D}/wigwag/devicejs-core-modules/rsmi/bin/slipcomms-x86
	do_rmnow ${D}/wigwag/devicejs-core-modules/node_modules/6lbr/6lbr/tools
	do_rmnow ${D}/wigwag/devicejs-core-modules/node_modules/6lbr/6lbr/cpu
	do_rmnow ${D}/wigwag/devicejs-core-modules/wigwag-devices/node_modules/6lbr/6lbr/cpu/mc1322x/tools/run-kermit
	do_rmnow ${D}/wigwag/devicejs-core-modules/node_modules/6lbr/6lbr/tools/sky
	do_rmnow ${D}/wigwag/devicejs-core-modules/wigwag-devices/node_modules/6lbr/6lbr/tools/sky/motelist-windows.exe
	do_rmnow ${D}/wigwag/devicejs-core-modules/wigwag-devices/node_modules/6lbr/6lbr/tools/sky/msp430-bsl-windows.exe
	do_rmnow ${D}/wigwag/devicejs-core-modules/wigwag-devices/node_modules/6lbr/6lbr/tools/sky/serialdump-windows.exe
	do_rmnow ${D}/wigwag/devicejs-core-modules/wigwag-devices/node_modules/6lbr/6lbr/tools
	install -d ${D}/wigwag/wigwag-core-modules
	cp -r ${S}/wcm/* ${D}/wigwag/wigwag-core-modules
	cp -r ${S}/wcm/.b ${D}/wigwag/wigwag-core-modules
}

