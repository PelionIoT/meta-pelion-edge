DESCRIPTION = "FCCE tool"

LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${WORKDIR}/git/LICENSE;md5=4336ad26bb93846e47581adc44c4514d"

MBED_EDGE_CORE_CONFIG_PARSEC_TPM_SE_SUPPORT ?= "OFF"
MBED_EDGE_CMAKE_BUILD_TYPE ?= "Debug"

DEPENDS = "deviceos-users python3-native python3-pip-native python3 python3-setuptools-native python3-setuptools-scm cmake-native mercurial-native python3-pyusb-native"
DEPENDS += "${@ 'parsec-se-driver' if d.getVar('MBED_EDGE_CORE_CONFIG_PARSEC_TPM_SE_SUPPORT') == 'ON' else ' '}"

RDEPENDS_${PN} += "bash python3 python3-core"

inherit cmake pkgconfig gitpkgv distutils3 setuptools3 python3native

SRC_URI = " \
git://github.com/parallaxsecond/parsec-se-driver.git;protocol=https;name=parsec;destsuffix=parsec-se-driver;branch=main \
git://git@github.com/ARMmbed/factory-configurator-client-example.git;protocol=https; \
file://0001-Added-trusted-storage-to-Yocto-target.patch \
file://0001-fix-build-getting-cross-compiler-iface-setting-to-et.patch \
file://linux-se-config.cmake \
file://0001-fix_psa_storage_location.patch \
"

SRCREV_pn-${PN} = "${PV}"
SRCREV_parsec = "0.5.0"

S = "${WORKDIR}/git"
FILES_${PN} = "/wigwag/wwrelay-utils/I2C/*"

lcl_maybe_fortify = '-D_FORTIFY_SOURCE=0'

do_configure() {

    cd ${S}

    export PYTHONPATH=$PYTHONPATH:`pwd`/recipe-sysroot-native/usr/lib/python3.8
    export PATH=$PYTHONPATH:$PATH

    export HTTP_PROXY=${HTTP_PROXY}
    export HTTPS_PROXY=${HTTPS_PROXY}

    pip3 install mbed-cli==1.10.5 click==7.1.2 requests pyopenssl==20.0.1

}

do_compile() {

    cd ${S}

    export HTTP_PROXY=${HTTP_PROXY}
    export HTTPS_PROXY=${HTTPS_PROXY}

    mbedpath=$(which mbed);
    python3 $mbedpath deploy

    python3 pal-platform/pal-platform.py -v deploy --target=Yocto_Generic_YoctoLinux_mbedtls generate

    if [ "${MBED_EDGE_CORE_CONFIG_PARSEC_TPM_SE_SUPPORT}" = "ON" ]; then


        # Manually adding the parsec-se-driver Middleware
        cp -R ${WORKDIR}/parsec-se-driver ${S}/pal-platform/Middleware/parsec_se_driver/parsec_se_driver

        # Place the precompiled parsec-se-driver static library
        if [ "${MBED_EDGE_CMAKE_BUILD_TYPE}" = "Debug" ]; then
            mkdir -p ${S}/pal-platform/Middleware/parsec_se_driver/parsec_se_driver/target/debug
            cp ${PKG_CONFIG_SYSROOT_DIR}/usr/lib/libparsec_se_driver.a ${S}/pal-platform/Middleware/parsec_se_driver/parsec_se_driver/target/debug/
        else
            mkdir -p ${S}/pal-platform/Middleware/parsec_se_driver/parsec_se_driver/target/release
            cp ${PKG_CONFIG_SYSROOT_DIR}/usr/lib/libparsec_se_driver.a ${S}/pal-platform/Middleware/parsec_se_driver/parsec_se_driver/target/release/
        fi
    fi

    cd ${S}/__Yocto_Generic_YoctoLinux_mbedtls/

    export ARMGCC_DIR=$(realpath $(pwd)/../../recipe-sysroot-native/usr/)

    if [ "${MBED_EDGE_CORE_CONFIG_PARSEC_TPM_SE_SUPPORT}" = "ON" ]; then
        cmake -G 'Unix Makefiles' -DCMAKE_BUILD_TYPE="${MBED_EDGE_CMAKE_BUILD_TYPE}" -DCMAKE_TOOLCHAIN_FILE=../pal-platform/Toolchain/POKY-GLIBC/POKY-GLIBC.cmake -DPARSEC_TPM_SE_SUPPORT=ON -DEXTARNAL_DEFINE_FILE=../../linux-se-config.cmake
    else
        cmake -G 'Unix Makefiles' -DCMAKE_BUILD_TYPE="${MBED_EDGE_CMAKE_BUILD_TYPE}" -DCMAKE_TOOLCHAIN_FILE=../pal-platform/Toolchain/POKY-GLIBC/POKY-GLIBC.cmake -DEXTARNAL_DEFINE_FILE=../linux-config.cmake
    fi

    make factory-configurator-client-example.elf

}

do_install() {

    install -d ${D}/wigwag
    install -d ${D}/wigwag/wwrelay-utils
    install -d ${D}/wigwag/wwrelay-utils/I2C
    install -m 755 ${S}/__Yocto_Generic_YoctoLinux_mbedtls/Debug/factory-configurator-client-example.elf ${D}/wigwag/wwrelay-utils/I2C/factory-configurator-client-armcompiled.elf
}
