DESCRIPTION = "Utilities used by the WigWag Relay"

LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=1dece7821bf3fd70fe1309eaa37d52a2"

SRC_URI="\
  git://git@github.com/armPelionEdge/edge-utils.git;protocol=https;name=wwrelay \
  git://git@github.com/armPelionEdge/edgeos-shell-scripts.git;protocol=https;name=dss;destsuffix=git/dss \
  git://git@github.com/armPelionEdge/node-i2c.git;protocol=https;name=node_i2c;destsuffix=git/tempI2C/node-i2c \
  git://git@github.com/armPelionEdge/pe-utils.git;protocol=ssh;name=pe-utils;destsuffix=git/pe-utils \
  file://do-post-upgrade.service \
  file://logrotate_directives/ \
"

SRCREV_FORMAT = "wwrelay-dss"
SRCREV_wwrelay = "7c0339f63c3cd0c6d53787b730d1c0693445037c"
SRCREV_dss = "04db833a43b80ecdfae07fd388bbe4e242771f38"
SRCREV_node_i2c = "511b1f0beae55bd9067537b199d52381f6ac3e01"
SRCREV_pe-utils = "a712d9f0f01bec9a9aa70dda7153cff2f2ba1f3f"

inherit pkgconfig gitpkgv systemd

INHIBIT_PACKAGE_STRIP = "1"

SYSTEMD_PACKAGES = "${PN}"
SYSTEMD_SERVICE_${PN} = "do-post-upgrade.service"
SYSTEMD_AUTO_ENABLE_${PN} = "enable"

PV = "1.0+git${SRCPV}"
PKGV = "1.0+git${GITPKGV}"
PR = "r7"

DEPENDS = "update-rc.d-native"
RDEPENDS_${PN} += " bash openssl "

RM_WORK_EXCLUDE += "${PN}"

FILES_${PN} = "\
  /wigwag/*\
  /wigwag/etc\
  /wigwag/etc/*\
  /etc/logrotate.d/*\
  /etc/init.d\
  /etc/init.d/*\
  /etc/wigwag\
  /etc/wigwag/*\
  /etc/rc?.d/*\
  /usr/bin\
  /usr/bin/*\
  /etc/*\
  /userdata\
  /upgrades\
  /localdata\
  ${systemd_system_unitdir}/do-post-upgrade.service\
"

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
}



do_dirInstall(){
    entry_dir=$(pwd)
	cd $1
	find . -type d -exec install -d $2/{} \;
	find . -type f -exec install -m 0755 {} $2/{} \; 
    cd $entry_dir
}



do_install() {
	#create all directoires
	install -d ${D}${INIT_D_DIR}
	install -d ${D}/localdata
	install -d ${D}/userdata
	install -d ${D}/upgrades
	install -d ${D}/etc
	install -d ${D}/etc/modprobe.d
	install -d ${D}/etc/network
	install -d ${D}/etc/udev
	install -d ${D}/etc/udev/rules.d
  install -d ${D}/wigwag/system/bin
	do_dirInstall ${S}/wigwag/ ${D}/wigwag/
	install -m 0755 ${S}/etc/modprobe.d/at24.conf ${D}/etc/modprobe.d/at24.conf

# Install systemd units
  install -d ${D}${systemd_system_unitdir}
  install -m 644 ${WORKDIR}/do-post-upgrade.service ${D}${systemd_system_unitdir}/do-post-upgrade.service

	#spreadsheet work needed
	#conf
	cp -r ${S}/conf ${D}/wigwag/wwrelay-utils/
	
	
    install -m 0755 ${S}/initscripts/UDEV/96-local.rules ${D}/etc/udev/rules.d/96-local.rules

	mkdir -p ${D}${bindir}

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



