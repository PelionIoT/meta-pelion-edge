#
# Recipe for packaging the pc-ble-driver. So far just buld tested only.
#

# TODO:
# * separate the examples out from library build;
#     Q: is a separate recipe commonly used or what?
#     A: cmake class pushes these to dev package.
# * clarify the do_package step, at least these are needed:
#     ** there are dynamic and static libraries, cmake files should be enough, but it is not
#
# Out of scope:
#
# * build the HEX files too
#     ** downsides:
#        * needs native toolchain (GCC 7.3.1 exact, no other version allowed) to be built for connectivity HW
#        * requires GIT 2.19 or later, Ubuntu 18.04 has only 2.17.x
#        * requires a binary tool download and execution (hexmerge)
# * nrftool?
#     ** it is available from PIP repo, but required only for HEX build

# LICENSE - proprietary Nordic Semiconductor 5-Clause
#
# You need to have a a Nordic chip with the SoftDevice running in it.
# You should likely use the latest API version, i.e. sd_api_v6 (as of writing this).
# Yocto build will not use the softdevice, it will use only PC-BLE-driver.
#
LICENSE = "Nordic-Semiconductor-5-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=772c3f93b8a2f4f2dec94ef7b9f434fb"

# Fetch a tagged release
SRC_URI = "git://github.com/NordicSemiconductor/pc-ble-driver.git;protocol=https;tag=v${PV}"

DEPENDS = "asio catch2 fmt spdlog udev"

# Recipe revision, please bump when changed.
PR = "r1"

S = "${WORKDIR}/git"

# Utilize existing cmake machinery to keep this file small and simple.
inherit cmake

# Specify any options you want to pass to cmake using EXTRA_OECMAKE:
EXTRA_OECMAKE = ""

# XXX: need to shovel these somewhere, so far -dev package will do. Perhaps a -doc would be better for LICENSE file?
FILES_${PN}-dev += "\
    ${datadir}/LICENSE \
    ${datadir}/nrf-ble-driver/nrf-ble-driver*.cmake \
     "
