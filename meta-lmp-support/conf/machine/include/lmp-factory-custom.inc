
#Use ttyAMA0 instead of ttyS0 that is set in meta-lmp
#this can cause problems with bluetooth. config removed
#KERNEL_SERIAL_rpi = "${@oe.utils.conditional("ENABLE_UART", "1", "console=ttyAMA0,115200", "", d)}"

#Docker requires cgroup memory
OSTREE_KERNEL_ARGS_COMMON += "cgroup_enable=memory cgroup_memory=1"

#Use sdcard as root device by default
OSTREE_KERNEL_ARGS_imx8mmevk = "console=tty1 console=ttymxc1,115200 earlycon=ec_imx6q,0x30890000,115200 root=/dev/mmcblk1p2 rootfstype=ext4"

# imx8mm solidrun customizations. 
# TODO!: To be checked when official LmP release with these targets is out
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
       linux-firmware-cyw-fmac-fw-bcm43455-sdio \
       linux-firmware-cyw-fmac-nvram-bcm43455-sdio \
       linux-firmware-cyw-bt-patch-bcm43455 \
"
