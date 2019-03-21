DESCRIPTION = "DeviceJS Core Modules"
LICENSE = "DEVICEJS-1"
LICENSE_FLAGS = "WigWagCommericalDeviceJS"
LIC_FILES_CHKSUM = "file://wcm/LICENSE;md5=4011f5b49f62dc7a25bef33807edc4bd"


inherit pkgconfig gitpkgv npm-base
#DBN="/home/walt/bin/config/builds"
#DBN="/builds/walt/42/wwrelay-rootfs/tools/build-scrips2/data/config/builds"

PV = "1.0+git${SRCPV}"
PKGV = "1.0+git${GITPKGV}"
PR = "r6"

#SRC_URI="git://git@github.com/WigWagCo/devicejs-core-modules.git;protocol=ssh;branch=devicejs2.0-compatibility"
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
	echo "STAGING: ${STAGING_INCDIR}" > /tmp/global-node-modules.log
	cp ${STAGING_INCDIR}/avahi-compat-libdns_sd/dns_sd.h ${STAGING_INCDIR}/
	whereisgyp=$(which node-gyp) || :  # a little trick to force a return code of 0 always, so bitbake doesn't crash
	if [[ $whereisgyp != "" ]]; then
		echo "node-gyp is here: $(which node-gyp)" >> /tmp/global-node-modules.log
	else
		echo "we are installing nodegyp" >> /tmp/global-node-modules.log
		oe_runnpm_native install -g node-gyp
	fi



	#devshell only COMMENT THIS OUT----------
	#S=`pwd`
	#cp /home/wigwag/yocto/thud/poky/build/tmp/work/cortexa7hf-neon-vfpv4-poky-linux-gnueabi/global-node-modules/1.0+gitAUTOINC+61cd3e3e56-bade8ce3ac-r6/recipe-sysroot/usr/include/avahi-compat-libdns_sd/dns_sd.h /home/wigwag/yocto/thud/poky/build/tmp/work/cortexa7hf-neon-vfpv4-poky-linux-gnueabi/global-node-modules/1.0+gitAUTOINC+61cd3e3e56-bade8ce3ac-r6/recipe-sysroot/usr/include/
	#npm install -g node-gyp
	#devshell only COMMENT THIS OUT----------



	mkdir combo > /dev/null 2>&1 || :
	cd combo
	cp -R ../wcm/* . >> /dev/null
	cp -R ../dcm/* . >> /dev/null
	#echo -en "{\n\"devjs-configurator\": \"http://github.com/WigWagCo/devjs-configurator#devicejs2.0-compatibility\",\n\"netkit\": \"git+ssh://git@github.com:WigWagCo/node-netkit.git\"\n}\n" > /tmp/overrides.json
	echo -en "{\n\"devjs-configurator\": \"http://github.com/WigWagCo/devjs-configurator#maestroRunner\",\n\"netkit\": \"git+ssh://git@github.com:WigWagCo/node-netkit.git\"\n}\n" > /tmp/overrides.json
	cd ../../


	#git clone -b arm-node8 git@github.com:WigWagCo/greaseLog.git
	#git clone https://github.com/WigWagCo/devjs-production-tools
	#git clone -b May10Ship git@github.com:WigWagCo/node-6lbr
	#--------------------devjs-production-tools-----------------------------------------------------------
#	if [[ ! -e greaseLog ]]; then
#		echo "greaseLog does not exist" >> /tmp/global-node-modules.log
#		git clone -b arm-node8 git@github.com:WigWagCo/greaseLog.git
#	else
#		echo "greaseLog exists, lets pull" >>/tmp/global-node-modules.log
#		cd greaseLog
#		git pull >> /tmp/global-node-modules.log
#		cd ..
#	fi


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


#   node tools/consolidator.js Runner UPnP AppServer APIProxy core-interfaces core-lighting Configurator IPStack sonos wigwag-devices Insteon ww-zwave zigbeeHA ModbusRTU BACnet Enocean
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

#	cd ${S}/../greaseLog
#	pwd >> /tmp/global-node-modules.log
#	oe_runnpm install
#	echo "oe_runnpm install on grease-log" >> /tmp/global-node-modules.log
#	node-gyp configure
#	node-gyp build
#	echo "greaseLog Compiled" >> /tmp/global-node-modules.log

	#cd ${S}/../node-6lbr
   	#pwd > /tmp/global-node-modules.log
   	#oe_runnpm install
   	#echo "oe_runnpm install on node-6lbr" >> /tmp/global-node-modules.log
   	#node-gyp configure
   	#node-gyp build
   	#echo "node-6lbr Compiled" >> /tmp/global-node-modules.log

   	cd ${S}/../devjs-production-tools
   	pwd >> /tmp/global-node-modules.log
   	oe_runnpm_native install





   	cd ${S}/combo/
   	rm -f package.json || :
   	rm -f package-lock.json || :
   	echo "running consolidtor" >> /tmp/global-node-modules.log
   	node ../../devjs-production-tools/consolidator.js -O /tmp/overrides.json -d grease-log -d dhclient ../wcm/* ../dcm/* ../dcm/wigwag-devices
 	#node ../../devjs-production-tools/consolidator.js -O /tmp/overrides.json -d dhclient ../wcm/* ../dcm/* ../dcm/wigwag-devices
 	echo  "done node consolidator" >> /tmp/global-node-modules.log
 	sed -i '/isc-dhclient/d' ./package.json
 	sed -i '/node-hotplug/d' ./package.json



 	echo -e "PACKAGE JSON logfile\n-------------------" > npm-package.log
 	echo -e "PJ1---------------------------- out of consolidator" >> npm-package.log
 	cat package.json >> npm-package.log
 	echo $ARCH >> /tmp/global-node-modules.log
 	NPM_FLAGS="--target_arch=$ARCH --target_platform=linux --loglevel silly"



 #	cp /tmp/package.json .
# 	echo -e "PJ1b--------------------------- after /tmp/copyover" >> npm-package.log
cat package.json >> npm-package.log
echo -e "PLJ1----------------------------" >> npm-package.log
cat package-lock.json >> npm-package.log  || :
echo "----------------^^^^^^^^^^^^^^^^^^^^^^^--------------------" >> npm-package.log



echo "----- first npm" > npm-first.log
echo "oe_runnpm install --target_arch=$ARCH --target_platform=linux --loglevel silly node-expat iconv bufferutil@3.0.5"  >> npm-first.log
oe_runnpm install --target_arch=$ARCH --target_platform=linux --loglevel silly node-expat iconv bufferutil@3.0.5 >> npm-first.log 2>&1




echo -e "PJ2---------------------------- after first npm  bufferutil iconv node-expat" >> npm-package.log
cat package.json >> npm-package.log
echo -e "PLJ2----------------------------" >> npm-package.log
rm package-lock.json || :
cat package-lock.json >> npm-package.log  || :
echo "----------------^^^^^^^^^^^^^^^^^^^^^^^--------------------" >> npm-package.log


echo "----- second npm" > npm-second.log
cat package.json >> npm-second.log
echo "oe_runnpm --target_arch=$ARCH --target_platform=linux --loglevel silly install" >> npm-second.log
oe_runnpm --target_arch=$ARCH --target_platform=linux --loglevel silly install >> npm-second.log 2>&1


echo -e "PJ3---------------------------- after second npm install" >> npm-package.log
cat package.json >> npm-package.log
rm package-lock.json || :
echo -e "PLJ3----------------------------" >> npm-package.log
cat package-lock.json >> npm-package.log  || :
echo "----------------^^^^^^^^^^^^^^^^^^^^^^^--------------------" >> npm-package.log


	#echo "----- third npm" >> npm-third.log
 	#oe_runnpm --target_arch=$ARCH --target_platform=linux --loglevel silly install git+ssh://git@github.com:WigWagCo/node-6lbr.git#May10Ship >> npm-third.log 2>&1
	#oe_runnpm --target_arch=$ARCH --target_platform=linux --loglevel silly install git+ssh://git@github.com:WigWagCo/greaseLog#arm-node8 >> npm.log 2>&1


	#cp ../greaseLog/node_modules/

}


do_install() {
	cd ${S}
	install -d ${D}/wigwag
	install -d ${D}/wigwag/devicejs-core-modules
	install -d ${D}/wigwag/devicejs-core-modules/node_modules
	install -d ${D}/wigwag/system/bin
	#	install -d ${D}/wigwag/devicejs-core-modules/node_modules/grease-log
	cp -r ${S}/combo/package.json ${D}/wigwag/devicejs-core-modules/
	cp -r ${S}/combo/node_modules/* ${D}/wigwag/devicejs-core-modules/node_modules/
	#	cp -r ${S}/../greaseLog/* ${D}/wigwag/devicejs-core-modules/node_modules/grease-log/
	#install -m 755 ${S}/BACnet/deps/bacnet-stack-0.8.3/demo/portal/bacportal ${D}/wigwag/system/bin
	#rm -rf ${S}/BACnet/deps/
	cp -r ${S}/dcm/* ${D}/wigwag/devicejs-core-modules
	cp -r ${S}/dcm/.b ${D}/wigwag/devicejs-core-modules
	#rm ${D}/wigwag/devicejs-core-modules/Runner/default_logger.conf.json
	#cp ${D}/wigwag/devicejs-core-modules/Runner/production_logger.conf.json ${D}/wigwag/devicejs-core-modules/Runner/default_logger.conf.json
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

