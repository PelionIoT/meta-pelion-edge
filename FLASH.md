## Flashing the Pelion gateway firmware image

After you flash, the built image will be in the build directory under `poky/build/tmp/deploy/images/raspberrypi3/` with a file name `console-image-raspberrypi3-<timestamp>.SOMETHING`. The ending varies based on the value of `IMAGE_FSTYPES` in your `local.conf`.

### To flash `console-image-raspberrypi3-<timestamp>.rootfs.wic.gz`:

#### macOS

This example assumes the SD card is enumerated as `/dev/diskX`. Be sure to verify your device's path. 

   Note: Make sure the SD card drive and its partitions, if any, are unmounted. To unmount all partitions, run the following command:

   ```
   ls /dev/diskX?* | xargs -n1 diskutil umount
   ```

To flash on macOS, use `dd`:

```
$ gunzip -c console-image-raspberrypi3-<timestamp>.rootfs.wic.gz | sudo dd bs=4m of=/dev/diskX conv=sync
```

Alternatively, you can use the [Etcher](https://www.balena.io/etcher/) app:

1. Choose the file to flash.
1. Choose the destination SD card.
1. Click **Flash**. 

In some cases, using Etcher results in significant time savings over using dd.

#### Linux

Note: Make sure the SD card drive and its partitions, if any, are unmounted. You can use `lsblk` to find the name of your SD card block device. To unmount all partitions run the following command:

   ```
   ls /dev/mmcblkX?* | xargs -n1 umount -l
   ```

To flash on Linux, use `dd`:

```
$ gunzip -c console-image-raspberrypi3-<timestamp>.rootfs.wic.gz | sudo dd bs=4M of=/dev/mmcblkX conv=fsync status=progress iflag=fullblock oflag=direct
```
