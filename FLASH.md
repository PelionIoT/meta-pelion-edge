# Flashing the Pelion gateway firmware image

The built image will be located in the build directory under `poky/build/tmp/deploy/images/raspberrypi3/`. The file name will be `console-image-raspberry3.SOMETHING` (the ending will vary based on the value of IMAGE_FSTYPES in your local.conf).

## To flash console-image-raspberry3.wic:

To flash on Mac OS X, use dd.  This example assumes the SD card is enumerated as /dev/diskX and you should verify your device's path.

        $ gunzip -c console-image-raspberrypi3.rootfs.wic.gz | sudo dd bs=4m of=/dev/diskX iflag=fullblock oflag=direct conv=fsync status=progress

Alternatively, you can use the [Etcher](https://www.balena.io/etcher/) app (the UI is self explanatory - simply choose the file to flash, the destination SD card, and then click Flash). In some cases, using Etcher results in significant time savings over using dd.

To flash on Linux, use dd.  You can use `lsblk` to find out the name of your SD card block device.

        $ gunzip -c console-image-raspberrypi3.rootfs.wic.gz | sudo dd bs=4M of=/dev/mmcblkX conv=sync
