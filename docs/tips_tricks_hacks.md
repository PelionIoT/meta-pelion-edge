# Tips Tricks and Hacks

**Disclaimer:** The following page is dedicated to tips, tricks and hacks that are not officially supported features; however, are documented to help developers get started with a particular concept or demo.  

## Controlling Serial Port output
RPI: The serial port (pins 8 and 10) are used by this yocto project in 4 stages of the boot process.  
1. During U-boot
1. During middle-boot (initramfs busybox) environment
1. During the kernel boot-up (post middle-boot)
1. for login and usermode

In some scenarios a developer may want to disable the use of /dev/ttyS0 so that a usermode program may utilize the port.  To do this, we will disable the use of the port in the later stages of the boot process
### Disabling kernel console output to /dev/ttyS0
After the middle-boot (initramfs busybox) environment, kmesg's are directed to /dev/ttyS0 which displays the boot process. To disable these messages at this stage of the boot, remove the console directive in the file /mnt/.boot/cmdline.txt.

**Before**
```
dwc_otg.lpm_enable=0 console=serial0,115200 root=/dev/mmcblk0p2 rootfstype=ext4 rootwait cgroup_enable=cpuset cgroup_enable=memory cgroup_memory=1
```
**afer**
```
dwc_otg.lpm_enable=0 root=/dev/mmcblk0p2 rootfstype=ext4 rootwait cgroup_enable=cpuset cgroup_enable=memory cgroup_memory=1
```
### Disabling login on the ttyS0
To disable the login stage of the boot, issue the following command from the linux prompt and reboot.
```bash
systemctl disable serial-getty@ttyS0.service
```
 * Note before doing this, make sure you have a method for getting onto the gateway.  Serial port login will no longer work.

### Shifting serial port user
**Untested**

Should a developer wish to shift the serial port to a different port (such as a usb serial port on the RPI) modify the console directive in the file /mnt/.boot/cmdline.txt to point to that device.  serial-getty@.service should adopt that directive, however, if it doesn't serial-getty@.service can be modified changing the agetty command to point to the same device directed in the cmdline.txt file.
