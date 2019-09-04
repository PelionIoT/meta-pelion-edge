DESCRIPTION = "FCC tool"

LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${WORKDIR}/git/LICENSE;md5=4336ad26bb93846e47581adc44c4514d"

DEPENDS = "deviceos-users python-native python python-setuptools-native python-setuptools-scm cmake-native"
RDEPENDS_${PN} += "bash python python-core"

inherit cmake pkgconfig gitpkgv distutils setuptools pythonnative

PV = "1.0+git${SRCPV}"
PKGV = "1.0+git${GITPKGV}"
PR = "r0"
SRCREV = "14ab6f4a4743da1e234a55557c2ba964f9de87e1"

SRC_URI = "git://git@github.com/ARMmbed/factory-configurator-client-example.git;protocol=ssh;branch=master; \
file://0001-fix-build-getting-cross-compiler-iface-setting-to-et.patch \
"
S = "${WORKDIR}/git"
FILES_${PN} = "/wigwag/wwrelay-utils/I2C/*"


do_configure () {
	cd ${S}
	curl -k https://bootstrap.pypa.io/get-pip.py | python
	export PYTHONPATH=`pwd`/recipe-sysroot-native/user/lib/python2.7
	export PATH=$PYTHONPATH:$PATH
	python -m pip install mbed-cli click requests
}

do_compile() {
	cd ${S}
	BUILD_TYPE=${1:-DEBUG}
	mbedpath=$(which mbed);
	python "$mbedpath" deploy
	#<todo> make a machine specific patch for sunxi vs rpi
	# userandom=random
	# sed -i "/char dev_random\[/c\char dev_random[] = \"/dev/${userandom}\";" ./mbed-cloud-client/mbed-client-pal/Source/Port/Reference-Impl/OS_Specific/Linux/Board_Specific/TARGET_Yocto_Generic/pal_plat_Yocto_Generic.c
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
