# Copyright (c) 2018 Arm Limited and Contributors. All rights reserved.
#
# SPDX-License-Identifier: BSD-3-Clause

#setenv bootdelay 0

# The FIT image contains this script plus the kernel and initramfs; it will look
# like we load ourselves recursively, but we actually run a different part of the image next
fitimg_name=boot.scr
fitimg_addr=0x02100000
msg_src=MMC

# The area between 0x10000000 and 0x11000000 has to be kept for secure
# world so that the kernel doesn't use it.
setenv bootargs "${bootargs} memmap=16M$256M"

# Set serial console parameters
setenv bootargs "${bootargs} 8250.nr_uarts=1 console=ttyS0,115200 rootwait rw"

# Disable FIQs in the USB driver. Using FIQs in USB driver causes the TF-A to crash.
setenv bootargs "${bootargs} dwc_otg.fiq_enable=0 dwc_otg.fiq_fsm_enable=0 dwc_otg.nak_holdoff=0"

if fatload usb 0:1 ${fitimg_addr} /boot/${fitimg_name} || ext4load usb 0:1 ${fitimg_addr} /boot/${fitimg_name}; then
	msg_src=USB
else
	fatload mmc 0:1 ${fitimg_addr} ${fitimg_name} || ext4load mmc 0:1 ${fitimg_addr} ${fitimg_name}
fi

echo Booting secure image from ${msg_src}
bootm ${fitimg_addr}#conf@bcm2710-rpi-3-b-plus.dtb
