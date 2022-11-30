SUMMARY = "A console enabled image with minimal additions to run all of Izuma Edge"

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
mbed-fcce \
"

PELION_SYSTEMS_MANAGEMENT = "\
edge-proxy \
maestro \
info-tool \
pe-terminal \
fluentbit \
testnet \
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

PARSEC_SERVICE = " \
parsec-service-tpm \
"

PARSEC_TOOL = " \
parsec-tool \
"

SOFTWARE_TPM = " \
swtpm-service \
tpm2-tools \
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
${PARSEC_SERVICE} \
${PARSEC_TOOL} \
${SOFTWARE_TPM} \
"


# Create a parsec user and then set permissions on the parsec components to control access.
# Create the parsec user.
inherit extrausers
EXTRA_USERS_PARAMS += "\
    useradd parsec;\
"

# modify the ownership of the folders and files that only the parsec user needs access to.

ROOTFS_POSTPROCESS_COMMAND_append = " \
  setup_parsec_files; \
"
setup_parsec_files() {
    chown -R parsec:parsec ${IMAGE_ROOTFS}/etc/parsec
    chown -R parsec:parsec ${IMAGE_ROOTFS}/usr/libexec/parsec
    chown parsec:parsec ${IMAGE_ROOTFS}/usr/bin/swtpm.sh
}
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
