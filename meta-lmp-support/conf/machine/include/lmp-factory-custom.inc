
#Use ttyAMA0 instead of ttyS0 that is set in meta-lmp
#this can cause problems with bluetooth. config removed
KERNEL_SERIAL_rpi = "${@oe.utils.conditional("ENABLE_UART", "1", "console=ttyAMA0,115200", "", d)}"

#Docker requires cgroup memory
OSTREE_KERNEL_ARGS_COMMON += "cgroup_enable=memory cgroup_memory=1"

#Use sdcard as root device by default
OSTREE_KERNEL_ARGS_imx8mm-lpddr4-evk = "console=tty1 console=ttymxc1,115200 earlycon=ec_imx6q,0x30890000,115200 root=/dev/mmcblk1p2 rootfstype=ext4"

# Disable anything to do with pushing image to server
# (mainly just silences a warning about SOTA_PACKED_CREDENTIALS not being set)
IMAGE_FSTYPES:remove = "ostreepush"
IMAGE_FSTYPES:remove = "garagesign"
IMAGE_FSTYPES:remove = "garagecheck"

# Set defaults for PARSEC_PROVIDER.
# All boards can support the SOFTHSM.
# The mx8 and uz boards could use PKCS11, but not without combined update.
# The rpi can use the SOFTHSM

PARSEC_PROVIDER ?= "SOFTHSM"
PARSEC_PROVIDER_uz ?= "SOFTHSM"
PARSEC_PROVIDER_mx8 ?= "SOFTHSM"
PARSEC_PROVIDER_rpi ?= "SOFTHSM"
