#
# Recipe for packaging the pc-ble-driver.
#

# LICENSE - proprietary Nordic Semiconductor 5-Clause
#
# You need to have a a Nordic chip with the SoftDevice running in it.
# You should likely use the latest API version, i.e. sd_api_v6 (as of writing this).
# Yocto build will not use the softdevice, it will use only PC-BLE-driver.
#
LICENSE = "Nordic-Semiconductor-5-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=772c3f93b8a2f4f2dec94ef7b9f434fb"
NO_GENERIC_LICENSE[Nordic-Semiconductor-5-Clause] = "LICENSE"

# Fetch UART Client Example
SRC_URI = "git://github.com/NordicSemiconductor/pc-ble-driver.git;protocol=https;branch=master"
SRCREV = "bc8821bf74edaad84e5eaf07782affd7005c70a6"

DEPENDS = "asio catch2 fmt spdlog udev"

# Recipe revision, please bump when changed.
PR = "r1"

S = "${WORKDIR}/git"

# Utilize existing cmake machinery to keep this file small and simple.
inherit cmake

# Specify any options you want to pass to cmake using EXTRA_OECMAKE:
# EXTRA_OECMAKE = "-DDISABLE_EXAMPLES=1 -DDISABLE_TESTS=1"
EXTRA_OECMAKE = ""

# XXX: need to shovel these somewhere, so far -dev package will do. Perhaps a -doc would be better for LICENSE file?
FILES:${PN}-dev += "\
    ${datadir}/LICENSE \
    ${datadir}/nrf-ble-driver/nrf-ble-driver*.cmake \
     "
