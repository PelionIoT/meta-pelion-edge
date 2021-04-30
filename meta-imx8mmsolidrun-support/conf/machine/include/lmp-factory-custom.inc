# LMP factory specific customizations (either replace or extend options as defined by meta-lmp)

# imx8mm solidrun
OSTREE_KERNEL_ARGS_imx8mmsolidrun ?= "console=tty1 console=ttymxc1,115200 earlycon=ec_imx6q,0x30890000,115200 root=LABEL=otaroot rootfstype=ext4"

# device tree setup
PREFERRED_PROVIDER_virtual/dtb_imx8mmsolidrun ?= "lmp-device-tree"
KERNEL_DEVICETREE_imx8mmsolidrun = ""
# Make sure we build the devicetree and add it to lmp-base builds
EXTRA_IMAGEDEPENDS_append_imx8mmsolidrun = " virtual/dtb "
IMAGE_BOOT_FILES_append_lmp-base_imx8mmsolidrun = " \
	devicetree/freescale_imx8mm-solidrun.dtb:imx8mm-solidrun.dtb \
"

# Add additional firmware for Wifi/BT
MACHINE_FIRMWARE_append_imx8mmsolidrun = " \
       linux-firmware-cyw-fmac-fw \
       linux-firmware-cyw-fmac-nvram \
       linux-firmware-cyw-bt-patch \
"
