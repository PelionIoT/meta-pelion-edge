SUMMARY = "Linux microPlatform image running Pelion Edge"

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

export IMAGE_BASENAME = "console-image-lmp"

# SolidRun Hummingboard Pulse has a Murata 1MW wifi/bt module which uses custom recipes
# linux-firmware-cyw-fmac-fw, linux-firmware-cyw-fmac-nvram and linux-firmware-cyw-bt-patch.
# Make sure we avoid default linux bcm43455 firmware
CORE_IMAGE_BASE_INSTALL_remove_imx8mmsolidrun = " \
    linux-firmware-bcm43455 \
"
