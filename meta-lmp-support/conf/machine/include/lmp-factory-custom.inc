
#Use ttyAMA0 instead of ttyS0 that is set in meta-lmp
#this can cause problems with bluetooth. config removed
KERNEL_SERIAL_rpi = "${@oe.utils.conditional("ENABLE_UART", "1", "console=ttyAMA0,115200", "", d)}"

#Docker requires cgroup memory
OSTREE_KERNEL_ARGS_COMMON += "cgroup_enable=memory cgroup_memory=1"

#Use sdcard as root device by default
OSTREE_KERNEL_ARGS_imx8mmevk = "console=tty1 console=ttymxc1,115200 earlycon=ec_imx6q,0x30890000,115200 root=/dev/mmcblk1p2 rootfstype=ext4"
