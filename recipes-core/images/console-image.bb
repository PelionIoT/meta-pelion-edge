SUMMARY = "A console enabled image with minimal additions to run all of Pelion"

IMAGE_FEATURES[validitems] += ""
IMAGE_FEATURES += "package-management splash"
IMAGE_LINGUAS = "en-us"

inherit image

DEPENDS += "deviceos-users"

IMAGE_BOOT_FILES += "ww-console-image-initramfs-raspberrypi3.cpio.gz.u-boot;initramfs.img"



CORE_OS = " \
openssh \
packagegroup-core-boot \
packagegroup-core-full-cmdline \
nano \
"

#mbed-edge-core is currently dependant on deviceos-users "developer"
PELION_BASE_REQUIRED = " \
deviceos-users \
virtual/mbed-edge-core \
identity-tool \
path-set \
pelion-version \
"
PELION_BASE_OPTIONAL = " \
mbed-fcc \
"

PELION_SYSTEMS_MANAGEMENT = "\
edge-proxy \
maestro \
devicedb \
info-tool \
relay-term \
fluentbit \
"

PELION_PROTOCOL_TRANSLATION = " \
mbed-edge-examples \
"

PELION_CONTAINER_ORCHESTRATION = " \
kubelet \
edge-proxy \
"

PELION_TESTING = " \
git \
panic \
"

RPI_EXTRA = " \
wpa-supplicant \
"

IMAGE_INSTALL += " \
${CORE_OS} \
${PELION_BASE_REQUIRED} \
${PELION_BASE_OPTIONAL} \
${PELION_PROTOCOL_TRANSLATION} \
${PELION_SYSTEMS_MANAGEMENT} \
${PELION_CONTAINER_ORCHESTRATION} \
${PELION_TESTING} \
${RPI_EXTRA} \
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

export IMAGE_BASENAME = "console-image"
