DESCRIPTION = "DeviceJS Core Modules"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=4336ad26bb93846e47581adc44c4514d"

inherit pkgconfig gitpkgv npm-base autotools

PV = "1.0+git${SRCPV}"
PKGV = "1.0+git${GITPKGV}"
PR = "r6"

SRC_URI="git://git@github.com/armPelionEdge/edge-node-modules.git;protocol=ssh"
SRCREV = "${AUTOREV}"
SRCREV_devjs_prod_tools = "master"

S = "${WORKDIR}/git"
WSYS= "${D}/wigwag/system"
WBIN= "${D}/wigwag/system/bin"

BBCLASSEXTEND = "native"
DEPENDS = "nodejs avahi udev nodejs-native"
RDEPENDS_${PN} += " nodejs bluez5"

FILES_${PN} = "/wigwag/* /wigwag/wigwag-core-modules/* /wigwag/devicejs-core-modules/*"

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
		oe_runnpm_native install -g node-gyp@5.1.1
	fi

	echo -en "{\n\"devjs-configurator\": \"http://github.com/armPelionEdge/devjs-configurator#master\"\n}\n" > ${WORKDIR}/overrides.json

	#--------------------devjs-production-tools-----------------------------------------------------------
	cd ${S}/../
	if [[ ! -e devjs-production-tools ]]; then
		echo "devjs-production-tools does not exist" >> /tmp/global-node-modules.log
		git clone git@github.com:armPelionEdge/devjs-production-tools.git
		git -C devjs-production-tools checkout ${SRCREV_devjs_prod_tools}
	else
		echo "devjs-production-tools exists, lets pull" >>/tmp/global-node-modules.log
		cd devjs-production-tools
		git pull >> /tmp/global-node-modules.log
		cd ..
	fi
}

do_compile() {
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
	cd ${S}/
	rm -f package.json || :
	rm -f package-lock.json || :
	node ../devjs-production-tools/consolidator.js -O ${WORKDIR}/overrides.json -d grease-log -d dhclient -d WWSupportTunnel ${S}/*
	sed -i '/isc-dhclient/d' ./package.json
	sed -i '/node-hotplug/d' ./package.json
	NPM_FLAGS="--target_arch=$ARCH --target_platform=linux --loglevel silly"
	oe_runnpm install --target_arch=$ARCH --target_platform=linux --loglevel silly node-expat iconv bufferutil@3.0.5 --production >> npm-first.log 2>&1
	rm package-lock.json || :
	oe_runnpm --target_arch=$ARCH --target_platform=linux --loglevel silly install --production >> npm-second.log 2>&1
	rm package-lock.json || :
}


do_dirInstall(){
	pushd . >> /dev/null
	cd $1
	find . -type d -exec install -d $2/{} \;
	find . -type f -exec install -m 0755 {} $2/{} \; 
	popd >> /dev/null
}
do_install() {
	do_rmnow ${S}/rsmi/bin/cc2530prog-x86
	do_rmnow ${S}/rsmi/bin/slipcomms-x86
	cd ${S}
	install -d ${D}/wigwag
	install -d ${D}/wigwag/devicejs-core-modules
	install -d ${D}/wigwag/devicejs-core-modules/node_modules
	install -d ${D}/wigwag/system/bin
	install -d ${D}/wigwag/wigwag-core-modules
	cp -r ${S}/package.json ${D}/wigwag/devicejs-core-modules/
	ALL_WigWag_Core_Modules="DevStateManager LEDController RelayStatsSender VirtualDeviceDriver onsite-enterprise-server relay-term"
    for f in $ALL_WigWag_Core_Modules; do
		do_dirInstall ${S}/$f ${D}/wigwag/wigwag-core-modules/$f
    done
    ALL_Devicejs_Core_Modules="rsmi zigbeeHA node_modules maestroRunner core-interfaces bluetoothlowenergy"
    for f in $ALL_Devicejs_Core_Modules; do
		do_dirInstall ${S}/$f ${D}/wigwag/devicejs-core-modules/$f
    done
}

