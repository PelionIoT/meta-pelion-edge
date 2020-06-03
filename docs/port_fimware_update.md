# Porting the Device Management Update client to systems running Edge Core

The update process in Linux is driven mainly by shell scripts that you can adapt to a new target. To know more about the purpose of the scripts and how to configure it for your system please read the instructions provided in [Linux update implementation](https://www.pelion.com/docs/device-management/current/porting/porting-the-device-management-update-client-to-linux-systems.html#linux-update-implementation) section of the *Porting devices* document.

## Steps followed to integrate update with Pelion Edge

**Note:** The details of the firmware update package and the process used to update the target filesystem is out of scope of this document.

The [Pelion Edge Yocto image](https://github.com/armPelionEdge/meta-pelion-edge) for RPi 3B+ target has two parts:
- The [C source code patch](https://github.com/armPelionEdge/meta-pelion-edge/blob/master/recipes-wigwag/mbed-edge-core/files/rpi3/0001-change-path-to-upgrade-scripts.patch) - Customizes the [`pal-linux/source` file](https://github.com/ARMmbed/mbed-cloud-client/blob/master/update-client-hub/modules/pal-linux/source/arm_uc_pal_linux_yocto_rpi.c) for Pelion Edge Yocto target. This source file executes the below shell scripts.
- The [shell scripts](https://github.com/armPelionEdge/meta-pelion-edge/tree/master/recipes-wigwag/mbed-edge-core/files/rpi3).

**Note:** The scripts are examples and are designed to work only with Pelion Edge Yocto image. Please modify the scripts as per your requirements.

The scripts requires the image to support OverlayFS and following partitions -
1. `factory` - It is a read-only, lower directory file system (fs) which contains the image burned at the factory.
1. `upgrade` - It is a also read-only, lower directory fs, but it is overlayed on top of *factory* fs. By default, the directory is empty. It is used to save the files which are applied during the firmware update process.
1. `user` - It is a read-write, upper directory fs and contains the files which are modified by the user.
1. `boot` - A read-write partition that contains the boot loader.
1. `userdata` - A read-write partition to store the Linux user space program data. Typically, the data stored in this partition is protected during the factory update process unless a factory reset is performed.

In this example, the system uses following shell scripts to perform firmware update:
- [arm_update_activate.sh](https://github.com/armPelionEdge/meta-pelion-edge/blob/master/recipes-wigwag/mbed-edge-core/files/rpi3/arm_update_activate.sh) - This script runs once the firmware image is downloaded by edge-core. Lets inspect the content of the script -
    1. Typically, the firmware update process erases the user partition and copies the files to the upgrade partition. If you wish to protect the files during this process, please save it under userdata partition, as in a typical workflow it is protected.

    Thus, the the header file which contains the metadata associated with the firmware image is copied to a directory under userdata. This header file is required by edge-core after the firmware update has been applied in order to verify if the update was successful or not.
        ```
        cp $HEADER /userdata/extended/header.bin
        ```

    1. Save the Pelion Edge service logs. They might be required for post update debugging.
        ```
        cp -R /wigwag/log/* /userdata/.logs-before-upgrade/
        ```

    1. Save the firmware image to a desired location on the fs.
        ```
        mv $FIRMWARE /upgrades/firmware.tar.gz
        ```

        In this setup, we unpack the firmware tarball into the user partition, under `/upgrades` folder and reboot the gateway. The update process will start upon detecting the required files.
        ```
        tar -xzf /upgrades/firmware.tar.gz -C /upgrades/
        ```

    1. Exit the script with appropiate error code. If the code is non-zero value then edge-core will notify Pelion Device Management that the update has failed.

- [arm_update_active_details.sh](https://github.com/armPelionEdge/meta-pelion-edge/blob/master/recipes-wigwag/mbed-edge-core/files/rpi3/arm_update_active_details.sh) - Returns the version (and associated metadata) of the active (running) firmware.

    1. Provide the same header file which was saved before the start of firmware update process.
        ```
        cp /userdata/extended/header.bin $HEADER
        ```

    1. Exit the script with appropiate error code. If the code is non-zero value then edge-core will notify Pelion Device Management that the update has failed.


- [arm_update_cmdline.sh](https://github.com/armPelionEdge/meta-pelion-edge/blob/master/recipes-wigwag/mbed-edge-core/files/rpi3/arm_update_cmdline.sh) - This file is used to parse the command line parameters which are passed to the shell scripts when called by by the C source file specified in the `pal-linux/source` directory. In this case it's the [update source file for Yocto target](https://github.com/ARMmbed/mbed-cloud-client/blob/master/update-client-hub/modules/pal-linux/source/arm_uc_pal_linux_yocto_rpi.c).

