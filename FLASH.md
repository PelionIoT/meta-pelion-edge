## Flashing the Pelion gateway firmware image

The built image will be located in the build directory under `poky/build/tmp/deploy/images/raspberrypi3/`. The file name will be `console-image-raspberrypi3-<timestamp>.SOMETHING` (the ending will vary based on the value of IMAGE_FSTYPES in your local.conf).

### To flash console-image-raspberrypi3-<timestamp>.rootfs.wic.gz:

#### MAC OS X
Note: Make sure the SD card drive and its partition, if any, are all unmounted. This example assumes the SD card is enumerated as /dev/diskX and you should verify your device's path. To unmount all partitions run the following command -

```
ls /dev/diskX?* | xargs -n1 diskutil umount
```
To flash on Mac OS X, use dd -

```
$ gunzip -c console-image-raspberrypi3-<timestamp>.rootfs.wic.gz | sudo dd bs=4m of=/dev/diskX conv=sync
```

Alternatively, you can use the [Etcher](https://www.balena.io/etcher/) app (the UI is self explanatory - simply choose the file to flash, the destination SD card, and then click Flash). In some cases, using Etcher results in significant time savings over using dd.

#### Linux
Note: Make sure the SD card drive and its partitions, if any, are all unmounted. You can use `lsblk` to find out the name of your SD card block device. To unmount all partitions run the following command -

```
ls /dev/mmcblkX?* | xargs -n1 umount -l
```

To flash on Linux, use dd -

```
$ gunzip -c console-image-raspberrypi3-<timestamp>.rootfs.wic.gz | sudo dd bs=4M of=/dev/mmcblkX conv=fsync status=progress iflag=fullblock oflag=direct
```
