DESCRIPTION = "FCC tool"

LICENSE = "DEVICEOS-1"
LICENSE_FLAGS = "WigWagCommericalDeviceOS"
LIC_FILES_CHKSUM = "file://../LICENSE;md5=543feeb21d5afbbe88012f44261f5217"


DEPENDS = "deviceos-users python-native python python-setuptools-native python-setuptools-scm cmake-native"
RDEPENDS_${PN} += "bash python python-core"

#PYPI_PACKAGE = "Adafruit-GPIO"
inherit cmake pkgconfig gitpkgv distutils setuptools pythonnative
#inherit pkgconfig gitpkgv python-dir pythonnative pypi setuptools


PV = "1.0+git${SRCPV}"
PKGV = "1.0+git${GITPKGV}"
PR = "r0"
SRCREV = "${AUTOREV}"

#SRC_URI = "git://git@github.com/ARMmbed/factory-configurator-client-example.git;protocol=ssh;branch=master;

SRC_URI = "git://git@github.com/bridadan/factory-configurator-client-example.git;protocol=ssh;branch=minimal_dependencies \
file://LICENSE \
"

S = "${WORKDIR}/git"

FILES_${PN} = "/wigwag/wwrelay-utils/I2C/*"



LOG = "/tmp/${PN}.log"
_log () {
	echo "$1" >> ${LOG}
}

do_configure () {
	cd ${S}
	echo "Logging setup for ${PN}" > ${LOG}
	_log  "do_configure"
	_log "our version of bash: $(/bin/sh --version)"
	_log "current working directory: $(pwd)"
	_log "---------"
	_log  "  -python path: "
	which python >> ${LOG} 2>&1 || :
	_log "  -mbed-cli path: "
	which mbed >> ${LOG} 2>&1 || :
	_log "  -pip path: "
	which pip >> ${LOG} 2>&1 || :
	curl -k https://bootstrap.pypa.io/get-pip.py | python
	export PYTHONPATH=`pwd`/recipe-sysroot-native/user/lib/python2.7
	export PATH=$PYTHONPATH:$PATH
	python -m pip install mbed-cli click requests
	_log "exiting configure"
	_log  "  -python path: "
	which python >> ${LOG} 2>&1 || :
	_log "  -mbed-cli path: "
	which mbed >> ${LOG} 2>&1 || :
	_log "  -pip path: "
	which pip >> ${LOG} 2>&1 || :
	# pip install --install-option="--prefix=mbed" mbed-cli
	# ~/yocto/thud/poky/build/tmp/work/cortexa7hf-neon-vfpv4-poky-linux-gnueabi/fcc/1.0+gitAUTOINC+b7bd3a7294-r0
	
}

do_compile() {
	cd ${S}
	_log "do_compile"
	_log "---------------"
	_log "pwd: $(pwd)"
	BUILD_TYPE=${1:-DEBUG}
	mbedpath=$(which mbed);
	python "$mbedpath" deploy
	useface=eth0
	sed -i "/set(TMPD/i \#TRM Hack to fix ethernet iface problem where the binary is pre-compiled to a specific\n#ETH intefrace in linux.  Someone needs to add a command line switch\nset(ETHNAME $useface)" CMakeLists.txt
	python pal-platform/pal-platform.py -v deploy --target=Yocto_Generic_YoctoLinux_mbedtls generate
	cd __Yocto_Generic_YoctoLinux_mbedtls/
	export ARMGCC_DIR=$(realpath $(pwd)/../../recipe-sysroot-native/usr/)
	cmake -G 'Unix Makefiles' -DCMAKE_BUILD_TYPE="$BUILD_TYPE" -DCMAKE_TOOLCHAIN_FILE=../pal-platform/Toolchain/POKY-GLIBC/POKY-GLIBC.cmake -DEXTARNAL_DEFINE_FILE=../linux-config.cmake
	make factory-configurator-client-example.elf
}

do_install() {
	install -d ${D}/wigwag
	install -d ${D}/wigwag/wwrelay-utils
	install -d ${D}/wigwag/wwrelay-utils/I2C
	install -m 755 ${S}/__Yocto_Generic_YoctoLinux_mbedtls/factory-configurator-client-example.elf ${D}/wigwag/wwrelay-utils/I2C/factory-configurator-client-armcompiled.elf

}
