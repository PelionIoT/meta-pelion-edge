SUMMARY = "Minimal factory image which includes OTA Lite, Docker, and OpenSSH support"

require recipes-samples/images/lmp-image-common.inc

# Factory tooling requires SOTA (OSTree + Aktualizr-lite)
require ${@bb.utils.contains('DISTRO_FEATURES', 'sota', 'recipes-samples/images/lmp-feature-factory.inc', '', d)}

# Enable wayland related recipes if required by DISTRO
require ${@bb.utils.contains('DISTRO_FEATURES', 'wayland', 'recipes-samples/images/lmp-feature-wayland.inc', '', d)}

# Enable OP-TEE related recipes if provided by the image
require ${@bb.utils.contains('MACHINE_FEATURES', 'optee', 'recipes-samples/images/lmp-feature-optee.inc', '', d)}

require recipes-samples/images/lmp-feature-softhsm.inc
require recipes-samples/images/lmp-feature-wireguard.inc
require recipes-samples/images/lmp-feature-docker.inc
require recipes-samples/images/lmp-feature-wifi.inc
require recipes-samples/images/lmp-feature-ota-utils.inc
require recipes-samples/images/lmp-feature-sbin-path-helper.inc

IMAGE_FEATURES += "ssh-server-openssh"

CORE_IMAGE_BASE_INSTALL += " \
    kernel-modules \
    networkmanager-nmcli \
    git \
    vim \
    packagegroup-core-full-cmdline-utils \
    packagegroup-core-full-cmdline-extended \
    packagegroup-core-full-cmdline-multiuser \
"

# Custom Additions
CORE_IMAGE_BASE_INSTALL += " \
    usb-modeswitch \
"

# SolidRun Hummingboard Pulse has a Murata 1MW wifi/bt module which uses custom recipes
# linux-firmware-cyw-fmac-fw, linux-firmware-cyw-fmac-nvram and linux-firmware-cyw-bt-patch.
# Make sure we avoid default linux bcm43455 firmware
CORE_IMAGE_BASE_INSTALL_remove_imx8mmsolidrun = " \
    linux-firmware-bcm43455 \
"
