#
# Recipe for packaging the pc-ble-driver. So far just buld tested only.
#

# TODO:
# * clarify the license declaration; is there a label for them?
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


# Licenses digged out by recipetool, remove comments when clarified.
#
# WARNING: the following LICENSE and LIC_FILES_CHKSUM values are best guesses - it is
# your responsibility to verify that the values are complete and correct.
#
# The following license files were not able to be identified and are
# represented as "Unknown" below, you will need to check them yourself:
#   LICENSE
#   hex/sd_api_v2/s132_nrf52_2.0.1_license-agreement.txt
#   hex/sd_api_v2/s130_nrf51_2.0.1_license-agreement.txt
#   hex/sd_api_v5/s132_nrf52_5.1.0_license-agreement.txt
#   hex/sd_api_v3/s132_nrf52_3.1.0_license-agreement.txt

# TODO: find out what on earth to use on this, as there are many of them?
LICENSE = "Unknown"
LIC_FILES_CHKSUM = "file://LICENSE;md5=772c3f93b8a2f4f2dec94ef7b9f434fb \
                    file://hex/sd_api_v2/s132_nrf52_2.0.1_license-agreement.txt;md5=322efcbf6eca7c4aa0ad3c9939c042b2 \
                    file://hex/sd_api_v2/s130_nrf51_2.0.1_license-agreement.txt;md5=322efcbf6eca7c4aa0ad3c9939c042b2 \
                    file://hex/sd_api_v5/s132_nrf52_5.1.0_license-agreement.txt;md5=c9d192fce740e93619dc42332d2af845 \
                    file://hex/sd_api_v3/s132_nrf52_3.1.0_license-agreement.txt;md5=322efcbf6eca7c4aa0ad3c9939c042b2"

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
