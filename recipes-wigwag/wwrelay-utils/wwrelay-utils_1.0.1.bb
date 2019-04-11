DESCRIPTION = "Utilities used by the WigWag Relay"

LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=1dece7821bf3fd70fe1309eaa37d52a2"

SRC_URI="git://git@github.com/armPelionEdge/edge-utils.git;protocol=ssh;branch=master;name=wwrelay \
git://git@github.com/armPelionEdge/edgeos-shell-scripts.git;protocol=ssh;branch=master;name=dss;destsuffix=git/dss \
file://wwrelay \
file://BUILDMMU.txt \
file://logrotate_directives/ \
"

SRCREV_FORMAT = "wwrelay-dss"
SRCREV_wwrelay = "${AUTOREV}"
SRCREV_dss = "${AUTOREV}"

inherit pkgconfig gitpkgv npm-base update-rc.d

INHIBIT_PACKAGE_STRIP = "1"

INITSCRIPT_NAME = "wwrelay"
INITSCRIPT_PARAMS = "defaults 80 20" 

PV = "1.0+git${SRCPV}"
PKGV = "1.0+git${GITPKGV}"
PR = "r7"

DEPENDS = "update-rc.d-native nodejs nodejs-native"
RDEPENDS_${PN} += " bash nodejs openssl10"

FILES_${PN} = "/wigwag/* /wigwag/etc /wigwag/etc/* /etc/logrotate.d/* /etc/init.d /etc/init.d/* /etc/wigwag /etc/wigwag/* /etc/rc?.d/* /usr/bin /usr/bin/* /etc/* /userdata /upgrades /localdata "

S = "${WORKDIR}/git"
S_MODPROBED="${S}/etc/modprobe.d"
S_PROFILED="${S}/etc/profile.d"
SWSB="${S}/system/bin"
SDTB ="${S}/dev-tools/bin"

do_package_qa () {
	echo "done"
}

do_log(){
	echo -e "$1" >> /tmp/YOCTO_wwrelay-utils.log
}
do_configure(){
	echo "its a new build (erasing old log)" > /tmp/YOCTO_wwrelay-utils.log
	oe_runnpm_native -g install node-gyp
	do_log "node-gyp installed at: $(which node-gyp)"
}

do_compile() {
	BUILDMMU=$(cat ${S}/../BUILDMMU.txt)
	VER_FILE=${S}/version.json
	if [ -e $VER_FILE ] ; then
		rm $VER_FILE
	fi
	echo  "{" > $VER_FILE
	echo  "   "  \"version\" ":" \"0.0.1\", >> $VER_FILE
	echo  "   "  \"packages\" ":" [{ >> $VER_FILE
	echo  "      "  \"name\" ":" \"WigWag-Firmware\", >> $VER_FILE
	echo  "      "  \"version\" ":" \"${BUILDMMU}\", >> $VER_FILE
	echo  "      "  \"description\" ":" \"Base Factory deviceOS\", >> $VER_FILE
	echo  "      "  \"node_module_hash\" ":" \"\", >> $VER_FILE
	echo  "      "  \"ww_module_hash\" ":" \"\" >> $VER_FILE
	echo  "   "  }]  >> $VER_FILE
	echo  "}" >> $VER_FILE
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
	
	do_log "6BSMD"
	cd ${S}/6BSMD
	make all

	do_log "I2c"
	cd ${S}
	cd ..
	if [[ -e tempI2C ]]; then
		rm -rf tempI2C/
	fi
	mkdir tempI2C
	cd tempI2C
	git clone -b master git@github.com:armPelionEdge/node-i2c.git
	cd node-i2c
	do_log "in wwrelay-utils node-i2c"
	oe_runnpm install --target_arch=arm --production
	node-gyp configure
	node-gyp build

	cd ${S}/I2C
	do_log "entered the directory $(pwd)"
	sed -i -- "/node-i2c/d" package.json
	oe_runnpm install  --target_arch=arm --production
	cd node_modules
	cp -r ${S}/../tempI2C/node-i2c/ i2c

	do_log "GPIO"
	cd ${S}/GPIO
	oe_runnpm install --production
	make

do_log "slip-radio"
cd ${S}/slip-radio
oe_runnpm install --production

do_log "slipcoms"
cd ${S}/slipcomms 
make 

do_log "cc2530prog"
cd ${S}/cc2530prog
make

cd ${S}

}

do_compile[nostamp] += "1"

do_dirInstall(){
	pushd . >> /dev/null
	cd $1
	find . -type d -exec install -d $2/{} \;
	find . -type f -exec install -m 0755 {} $2/{} \; 
#a more cleaver one:
popd >> /dev/null
}



do_install() {
	#create all directoires
	install -d ${D}${INIT_D_DIR}
	install -d ${D}/localdata
	install -d ${D}/userdata
	install -d ${D}/upgrades
	install -d ${D}/etc
	install -d ${D}/etc/dnsmasq.d
	install -d ${D}/etc/profile.d
	install -d ${D}/etc/modprobe.d
	install -d ${D}/etc/network
	install -d ${D}/etc/udev
	install -d ${D}/etc/udev/rules.d
	do_dirInstall ${S}/wigwag/ ${D}/wigwag/
	install -m 0755 ${S}/etc/dnsmasq.conf ${D}/etc/dnsmasq.conf
	install -m 0755 ${S}/etc/dnsmasq.d/dnsmasq.conf ${D}/etc/dnsmasq.d/dnsmasq.conf
	install -m 0755 ${S}/etc/modprobe.d/at24.conf ${D}/etc/modprobe.d/at24.conf
	install -m 0755 ${S}/etc/profile.d/wigwagpath.sh ${D}/etc/profile.d/wigwagpath.sh
	install -m 0755 ${S}/../wwrelay ${D}${INIT_D_DIR}
	install -m 0755 ${S}/etc/init.d/devjssupport ${D}${INIT_D_DIR}
	install -m 0755 ${S}/etc/init.d/relayterm ${D}${INIT_D_DIR}
	install -m 0755 ${S}/etc/init.d/wwfunctions ${D}${INIT_D_DIR}
	update-rc.d -r ${D} relayterm defaults 85 20

	#spreadsheet work needed
	#conf
	cp -r ${S}/conf ${D}/wigwag/wwrelay-utils/
	
	#version
	install -m 0755 ${S}/version.json ${D}/wigwag/wwrelay-utils/conf/versions.json
	install -m 0755 ${S}/version.json ${D}/wigwag/etc/versions.json
	cp ${S}/slipcomms/slipcomms ${D}/wigwag/devicejs-core-modules/rsmi/bin/slipcomms-arm
	cp ${S}/cc2530prog/cc2530prog ${D}/wigwag/devicejs-core-modules/rsmi/bin/cc2530prog-arm
	cp -r ${S}/6BSMD ${D}/wigwag/wwrelay-utils/6BSMD
	cp -r ${S}/common ${D}/wigwag/wwrelay-utils/common
	cp -r ${S}/.b ${D}/wigwag/wwrelay-utils/
	cp -r ${S}/initscripts ${D}/wigwag/wwrelay-utils/initscripts
	install -m 0755 ${S}/initscripts/UDEV/96-local.rules ${D}/etc/udev/rules.d/96-local.rules
	cp -r ${S}/debug_scripts ${D}/wigwag/wwrelay-utils/debug_scripts
	chmod 755 ${D}/wigwag/wwrelay-utils/debug_scripts/*
	chmod 755 ${D}/wigwag/wwrelay-utils/debug_scripts/*.*
	cp -r ${S}/slip-radio ${D}/wigwag/wwrelay-utils/slip-radio
	mkdir -p ${D}${bindir}
	#all of dev-tools
	cp -r ${S}/dev-tools ${D}/wigwag/wwrelay-utils/dev-tools
	install -m 755 ${S}/dev-tools/bin/ccommon.sh ${D}/wigwag/system/bin/
	install -m 755 ${S}/dss/* ${D}/wigwag/system/bin/
	install -m 755 ${S}/dev-tools/bin/stopwatchdog.sh ${D}/wigwag/system/bin/stopwatchdog
	install -m 755 ${S}/dev-tools/scripts/restartjob.sh ${D}/wigwag/system/bin/restartjob.sh
	rm -rf ${D}/wigwag/wwrelay-utils/dev-tools/bin/{stopwatchdog.sh,info.sh}

	#all of GPIO
	cp -r ${S}/GPIO ${D}/wigwag/wwrelay-utils/GPIO
	install -m 755 ${S}/GPIO/led.sh ${D}/wigwag/system/bin/led
	install -m 755 ${S}/GPIO/pinctrl.sh ${D}/wigwag/system/bin/pinctrl
	rm -rf ${D}/wigwag/wwrelay-utils/GPIO/led.sh
	rm -rf ${D}/wigwag/wwrelay-utils/GPIO/pinctrl.sh
	install -m 755 ${S}/GPIO/leddaemon ${D}/wigwag/system/bin/leddaemon
	rm -rf ${D}/wigwag/wwrelay-utils/GPIO/leddaemon
	#all of DOGControl
	install -m 755 ${S}/DOGcontrol/dogProgrammer.sh ${D}/wigwag/system/bin/dogProgrammer
	#cherrypick manu-tools
	#all of I2C
	cp -r ${S}/I2C ${D}/wigwag/wwrelay-utils/I2C
	install -m 755 ${S}/I2C/eetool.sh ${D}/wigwag/system/bin/eetool
	rm -rf ${D}/wigwag/wwrelay-utils/I2C/eetool.sh
	#populate the /wigwag/system/lib
	install -d ${D}/wigwag/log
	install -d ${D}/wigwag/devicejs/conf
	install -d ${D}/wigwag/etc
	install -d ${D}/wigwag/etc/devicejs
	install -d ${D}/wigwag/support
	install -d ${D}/wigwag/devicejs/devjs-usr/App
	cd ${S}/conf
	install -d ${D}${sysconfdir}/wigwag
	install -d ${D}/wigwag/devicejs-core-modules
	#install -d ${D}/wigwag/devicejs-core-modules/Runner
	install -d ${D}/wigwag/wigwag-core-modules/
	install -d ${D}/wigwag/wigwag-core-modules/relay-term/
	install -d ${D}/wigwag/wigwag-core-modules/relay-term/config/

	#logrotate.d
	install -d "${D}${sysconfdir}/logrotate.d/"
	ALL_LDs="$(ls ${WORKDIR}/logrotate_directives)"
	for f in $ALL_LDs; do
		install -m 644 "${WORKDIR}/logrotate_directives/$f" "${D}${sysconfdir}/logrotate.d"
	done 

}



