DESCRIPTION = "FCCE tool"

LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${WORKDIR}/git/LICENSE;md5=4336ad26bb93846e47581adc44c4514d"

DEPENDS = "deviceos-users python3-native python3-pip-native python3 python3-setuptools-native python3-setuptools-scm cmake-native mercurial-native python3-pyusb-native"
RDEPENDS_${PN} += "bash python3 python3-core"

inherit cmake pkgconfig gitpkgv distutils3 setuptools3 python3native

SRCREV = "${PV}"

SRC_URI = "git://git@github.com/ARMmbed/factory-configurator-client-example.git;protocol=https; \
file://0001-fix-build-getting-cross-compiler-iface-setting-to-et.patch \
file://linux-se-config.cmake \
"
S = "${WORKDIR}/git"
FILES_${PN} = "/wigwag/wwrelay-utils/I2C/*"


do_configure () {
    cd ${S}
    export PYTHONPATH=$PYTHONPATH:${RECIPE_SYSROOT_NATIVE}/usr/lib/python3.8
    export PATH=$PYTHONPATH:$PATH
    export HTTP_PROXY=${HTTP_PROXY}
    export HTTPS_PROXY=${HTTPS_PROXY}
    pip3 install mbed-cli==1.10.5 click==7.1.2 requests pyopenssl==20.0.1
}

do_compile() {
    cd ${S}
    export HTTP_PROXY=${HTTP_PROXY}
    export HTTPS_PROXY=${HTTPS_PROXY}
    BUILD_TYPE=${1:-DEBUG}
    mbedpath=$(which mbed);
    python3 $mbedpath deploy
    #<todo> make a machine specific patch for sunxi vs rpi
    # userandom=random
    # sed -i "/char dev_random\[/c\char dev_random[] = \"/dev/${userandom}\";" ./mbed-cloud-client/mbed-client-pal/Source/Port/Reference-Impl/OS_Specific/Linux/Board_Specific/TARGET_Yocto_Generic/pal_plat_Yocto_Generic.c
    python3 pal-platform/pal-platform.py -v deploy --target=Yocto_Generic_YoctoLinux_mbedtls generate
    cd __Yocto_Generic_YoctoLinux_mbedtls/
    export ARMGCC_DIR=${RECIPE_SYSROOT_NATIVE}/usr

    if [ $MBED_EDGE_CORE_CONFIG_PARSEC_TPM_SE_SUPPORT == "ON" ]; then
        cmake -G 'Unix Makefiles' -DCMAKE_BUILD_TYPE="$BUILD_TYPE" -DCMAKE_TOOLCHAIN_FILE=../pal-platform/Toolchain/POKY-GLIBC/POKY-GLIBC.cmake -DPARSEC_TPM_SE_SUPPORT=ON -DEXTARNAL_DEFINE_FILE=${WORKDIR}/linux-se-config.cmake
    else
        cmake -G 'Unix Makefiles' -DCMAKE_BUILD_TYPE="$BUILD_TYPE" -DCMAKE_TOOLCHAIN_FILE=../pal-platform/Toolchain/POKY-GLIBC/POKY-GLIBC.cmake -DEXTARNAL_DEFINE_FILE=../linux-config.cmake
    fi

    make factory-configurator-client-example.elf

}

do_install() {
    install -d ${D}/wigwag
    install -d ${D}/wigwag/wwrelay-utils
    install -d ${D}/wigwag/wwrelay-utils/I2C
    install -m 755 ${S}/__Yocto_Generic_YoctoLinux_mbedtls/factory-configurator-client-example.elf ${D}/wigwag/wwrelay-utils/I2C/factory-configurator-client-armcompiled.elf
}
