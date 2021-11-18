SUMMARY = "Linux microPlatform image for stress testing (No Pelion Programs)"

require recipes-samples/images/lmp-image-common.inc

require recipes-samples/images/lmp-feature-docker.inc
require recipes-samples/images/lmp-feature-wifi.inc
require recipes-samples/images/lmp-feature-sbin-path-helper.inc

IMAGE_FEATURES[validitems] += "tools-debug tools-sdk"
IMAGE_FEATURES += "package-management ssh-server-openssh"
#IMAGE_LINGUAS = "en-us

IMAGE_OVERHEAD_FACTOR = "2"

DEPENDS += "deviceos-users"

CORE_IMAGE_BASE_INSTALL += " \
    kernel-modules \
    networkmanager-nmcli \
    git \
    vim \
    rng-tools \
    haveged \
    packagegroup-core-full-cmdline-utils \
    packagegroup-core-full-cmdline-extended \
    packagegroup-core-full-cmdline-multiuser \
"

CORE_OS = " \
openssh \
packagegroup-core-boot \
packagegroup-core-full-cmdline \
"

STRESS_TESTING = " \
ltp \
stress-ng \
fio \
stressapptest \
rt-app \
"

BUILDING = " \
packagegroup-core-buildessential \
"

IMAGE_INSTALL += " \
${CORE_OS} \
${STRESS_TESTING} \
${BUILDING} \
${MACHINE_EXTRA_RRECOMMENDS} \
"

set_local_timezone() {
    ln -sf /usr/share/zoneinfo/EST5EDT ${IMAGE_ROOTFS}/etc/localtime
}

disable_bootlogd() {
    echo BOOTLOGD_ENABLE=no > ${IMAGE_ROOTFS}/etc/default/bootlogd
}

ROOTFS_POSTPROCESS_COMMAND += " \
    set_local_timezone ; \
    disable_bootlogd ; \
"

export IMAGE_BASENAME = "console-image-lmp-stress"
