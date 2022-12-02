# Building Izuma Edge with LmP (Linux microPlatform)

This guide shows you how to get started with [Izuma](https://izumanetworks.com) Edge and Linux microPlatform (LmP). LmP is a Yocto-built distribution maintained by [Foundries.io](https://foundries.io/). At the end of this tutorial, you will have built an [LmP image](https://docs.foundries.io/latest/reference-manual/linux/linux.html) that contains the [Izuma Edge software](https://developer.izumanetworks.com/docs/device-management-edge/latest/introduction/index.html) that is ready to be deployed.

## Prerequisites

Building the Izuma Edge image has been verified to work on [Ubuntu 20.04](https://releases.ubuntu.com/20.04/) operating system.

1. Verify you comply with the [build machine requirements](#build-machine-requirements).

1. Install the build requirements:

   ```
    sudo apt-get install coreutils curl gawk wget git-core diffstat unzip \
    texinfo g++ gcc-multilib build-essential chrpath socat cpio \
    openjdk-11-jre python3 python3-pip python3-venv python3-pexpect \
    xz-utils debianutils iputils-ping libsdl1.2-dev xterm libssl-dev \
    libelf-dev android-sdk-ext4-utils ca-certificates whiptail xxd \
    libtinfo5
   ```

   If you are building for the Avnet UltraZed board, install:

   ```
    sudo apt-get install libncurses5-dev
   ```

1. Set up the required building tools:

   1. Install [repo](https://gerrit.googlesource.com/git-repo/+/refs/heads/master/README.md). You can install `repo` on Linux with this command:

      ```
      mkdir ~/bin && PATH=~/bin:$PATH && curl https://storage.googleapis.com/git-repo-downloads/repo > ~/bin/repo && chmod a+x ~/bin/repo
      ```

1. Check your default Python version:

    ```
    python --version
    ```

    If your default Python version is earlier than 3.6, you need to install a newer Python version.

1. Create a [Python virtual environment](https://docs.python.org/3.6/tutorial/venv.html) for pelion-build, activate it, check Python version again and install [Manifest tool v2.1.1](https://github.com/PelionIoT/manifest-tool/tree/v2.1.1):

   ```
   python3 -m venv ~/pelion-venv
   source ~/pelion-venv/bin/activate
   python --version
   pip install wheel
   pip install manifest-tool==2.1.1
   ```

   After you have activated the Python 3 virtual enviroment, a `python` command that starts  `python3`.

1. Configure `user.name` and `user.email` because the `repo` tool is built on top of Git:

   ```
   git config --global user.name "Mona Lisa"
   git config --global user.email "email@example.com"
   ```

1. Decide to build a Production (factory), a Developer or a Bring your own certificate (BYOC) mode image:

   - ***Production mode*** results in a build that only contains bootstrap credentials against Pelion Device Management, so you can use the FCU/FCC process to pair the gateway to any Pelion Device Management account. Look at the [PEP tool](../provisioning/index.html) to learn about automating your factory floor in a production environment.
   - ***Developer mode*** results in a build that automatically pairs to a specific Pelion Device Management account. You need to provide the Izuma Device Management configuration files at build time, and they are compiled within the program binary.
   - ***BYOC mode*** results in a developer build and is similar to the developer mode except the Pelion Device Management configuration is provided at runtime rather than at build time. This creates a generic developer build you can distribute for development or testing purposes.

   You can learn more about how to configure Izuma Edge build options for the different modes in the [mbed-edge GitHub](https://github.com/PelionIoT/mbed-edge#configuring-edge-build).

### Build machine requirements

Building an LmP image means building a whole Linux distribution. To successfully build such images, you must:

- Have a build machine capable of building an LmP image:
   - A CPU with a minimum of eight cores.
   - A minimum of 100GB of free disk space per target. We highly recommend a solid state drive disk.
   - A minimum of 16GB of RAM available. Our recommendation is 32GB.
      - 64GB RAM accelerates the build because you can keep most of the files in a RAM cache.

      <span class="tips">**Tip:** Logs with `signal 9` or `Killed signal terminated program` indicate a lack of memory. You can decrease parallel memory use by building node.js separately. Issue `bitbake nodejs` before starting the image build.</span>

- Have the required unhindered connectivity to internet:

   Yocto builds consist of more than 1,500 components spread around the internet. Invasive man-in-the-middle proxies can prevent the builds from working by interfering with the end-to-end security built into some of the tooling (such as `curl` or `git`). If you have such an enviroment, please work with your local IT support staff for solutions because these will be specific to the network infrastructure your IT has chosen. We also recommend you read [Yocto documentation on working with proxies](https://wiki.yoctoproject.org/wiki/Working_Behind_a_Network_Proxy).

   Errors that indicate issues caused by proxies include:

   - `fatal: unable to connect to github.com: github.com[0: 140.82.121.3]: errno=Connection timed out`.
   - `do_fetch. Failed to fetch URL.`
   - `fatal: unable to connect to git.yoctoproject.org:git.yoctoproject.org[0: 44.225.90.102]: errno=Connection timed out`.

   You can also test for this:

   ```
   curl -L https://dl.google.com/go/go1.14.4.linux-amd64.tar.gz >go1.14.4.tar.gz
   ```

   If that fails with `curl: (60) server certificate verification failed. CAfile: /etc/ssl/certs/ca-certificates.crt`, a proxy is being used.

- Log into the build machine as a non-root user. If you are a `root` user:

   1. Add another user.
   1. Log in as the non-root user before proceeding further. The user may be in the sudoers group.

### Developer mode prerequisites

1. Create a folder to store Pelion-specific configuration files:

   ```
   mkdir ~/Edge_Credentials
   cd ~/Edge_Credentials
   ```

      <span class="notes">**Note**: You can reuse this folder in further tutorials, such as when using the Pelion Edge Provisioner. Store it in a safe location.</span>

1. Create and download a developer certificate to allow the edge gateway to connect to your Pelion account:

   1. Go to the Izuma Device Management Portal:

      - [United States](https://portal.mbedcloud.com/).

   1. Go to **Device Identity** > **Certificates**.
   1. Create a developer certificate.
   1. Download the certificate `mbed_cloud_dev_credentials.c`.
   1. Place this file into `~/Edge_Credentials`.

1. Create the corresponding `update_default_resources.c` file, so you can apply firmware updates to the gateway device:

   1. In the Izuma Device Management Portal, go to **Access Management** > **Access keys**.
   1. Create an access key and save it.
   1. Run this command:

      ```
      manifest-dev-tool init -a <access key>
      ```

   1. Copy the generated `update_default_resources.c` to `~/Edge_Credentials`.

      For more information, please refer to the [manifest tool](https://github.com/PelionIoT/manifest-tool/tree/v2.1.0/README.md#quick-start) README.

## Build

1. Initialize a repository client:

   1. Create an empty directory to hold the build directory:

      ```
      mkdir ~/build
      cd ~/build
      ```

   1. Download the Yocto manifest file in this repository:

      ```
      repo init -u https://github.com/PelionIoT/manifest-lmp-pelion-edge.git -m pelion.xml
      ```
      <span class="notes">**Note**: If you use two-factor authentication, you need to [generate a personal access token](https://docs.github.com/en/github/authenticating-to-github/creating-a-personal-access-token) and use it as password.</span>

   Your directory now contains a `.repo` directory.

1. Fetch all the repositories:

   ```
   repo sync -j"$(nproc)"
   ```

## Select provisioning mode

1. Determine whether you're running in Production mode, Developer mode or BYOC mode:

   - If running in BYOC mode, inform the build system about BYOC mode, and update:

      ```
      echo -e "\n" >> .repo/manifests/conf/local.conf
      echo 'MBED_EDGE_CORE_CONFIG_BYOC_MODE = "ON"' >> .repo/manifests/conf/local.conf
      echo 'MBED_EDGE_CORE_CONFIG_DEVELOPER_MODE = "OFF"' >> .repo/manifests/conf/local.conf
      echo 'MBED_EDGE_CORE_CONFIG_FIRMWARE_UPDATE = "ON"' >> .repo/manifests/conf/local.conf
      echo 'MBED_EDGE_CORE_CONFIG_FOTA_ENABLE = "ON"' >> .repo/manifests/conf/local.conf
      ```

   - If running in Developer mode:

      1. Ensure you followed the Developer mode prerequsites above.
      1. Copy the `mbed_cloud_dev_credentials.c` and `update_default_resources.c` to the edge-core recipe folder.

         ```
         cp ~/Edge_Credentials/mbed_cloud_dev_credentials.c layers/meta-mbed-edge/recipes-connectivity/mbed-edge-core/files/
         cp ~/Edge_Credentials/update_default_resources.c layers/meta-mbed-edge/recipes-connectivity/mbed-edge-core/files/
         ```

      1. Inform the build system about developer mode and update:

         ```
         echo -e "\n" >> .repo/manifests/conf/local.conf
         echo 'MBED_EDGE_CORE_CONFIG_DEVELOPER_MODE = "ON"' >> .repo/manifests/conf/local.conf
         echo 'MBED_EDGE_CORE_CONFIG_FIRMWARE_UPDATE = "ON"' >> .repo/manifests/conf/local.conf
         echo 'MBED_EDGE_CORE_CONFIG_FOTA_ENABLE = "ON"' >> .repo/manifests/conf/local.conf
         ```

   - If running in Production mode, inform the build system that this is not a developer mode build (which is the default):

      ```
      echo -e "\n" >> .repo/manifests/conf/local.conf
      echo 'MBED_EDGE_CORE_CONFIG_FACTORY_MODE = "ON"' >> .repo/manifests/conf/local.conf
      echo 'MBED_EDGE_CORE_CONFIG_DEVELOPER_MODE = "OFF"' >> .repo/manifests/conf/local.conf
      echo 'MBED_EDGE_CORE_CONFIG_FIRMWARE_UPDATE = "ON"' >> .repo/manifests/conf/local.conf
      echo 'MBED_EDGE_CORE_CONFIG_FOTA_ENABLE = "ON"' >> .repo/manifests/conf/local.conf
      ```

      To securely run Pelion Edge with the Trusted Platform Module (TPM) v2.0, add this flag:

      ```
      echo 'MBED_EDGE_CORE_CONFIG_PARSEC_TPM_SE_SUPPORT = "ON"' >> .repo/manifests/conf/local.conf
      ```

      <span class="notes">**Note:** You can only work with Izuma Edge in production mode when you use a TPM. For more details, please see the [documentation about securing Pelion Edge with a TPM](../security/security-tpm.html).</span>

      To speed up subsequent builds, set up your `SSTATE_CACHE` (or `SSTATE_MIRRORS`) and `DL_DIR` for BitBake. Modify the path according to your needs and username:

      ```
      echo 'SSTATE_DIR = "/home/username/SSTATE_CACHE"' >> .repo/manifests/conf/local.conf
      echo 'DL_DIR = "/home/username/DL_DIR"' >> .repo/manifests/conf/local.conf

      ```

1. Set up the build environment variables:

   - For i.MX 8M Mini EVK, run:

      ```
      MACHINE=imx8mmevk source setup-environment
      ```

   - For Hummingboard Ripple or Pulse, run:

      ```
      MACHINE=imx8mmsolidrun source setup-environment
      ```

   - For Avnet UltraZed, run:

      ```
      MACHINE=uz3eg-iocc source setup-environment
      ```

   - For Raspberry Pi 3, run:

      ```
      MACHINE=raspberrypi3-64 source setup-environment
      ```

   - For Raspberry Pi 4, run:

      ```
      MACHINE=raspberrypi4-64 source setup-environment
      ```

1. Start the build with `bitbake`:

   ```
   bitbake console-image-lmp
   ```

   The built image is in the build directory under `deploy/images/<MACHINE_NAME>/` with a file name `console-image-lmp-<MACHINE_NAME>-<timestamp>.rootfs.wic`.

   If limited memory is available, you can build in two stages: `bitbake nodejs && bitbake console-image-lmp`.

1. Prepare the device for booting from the SD card:

   Check the positions of the boot switches:

   - For i.MX 8M Mini, Hummingboard Pulse or Hummingboard Ripple, you can see the required positions of the switches printed on the silkscreen of the PCB.
   - For Avnet UltraZed, you can find the required positions of the switches from the user guide of your board.

1. Flash the image `console-image-lmp-<MACHINE_NAME>-<timestamp>.rootfs.wic.gz` to an SD card:

   - macOS:

      1. Be sure to verify your device's path. This example assumes the SD card is enumerated as `/dev/diskX`.

      1. Make sure the SD card drive and its partitions, if any, are unmounted. To unmount all partitions, run the following command:

         ```
         ls /dev/diskX?* | xargs -n1 diskutil umount
         ```

      1. Use `dd`:

         ```
         gunzip -c console-image-lmp-<MACHINE_NAME>-<timestamp>.rootfs.wic.gz | sudo dd bs=4m of=/dev/diskX conv=sync
         ```

         Alternatively, you can use the [Etcher](https://www.balena.io/etcher/) app, which can result in significant time savings over using `dd`:

            1. Choose the file to flash.
            1. Choose the destination SD card.
            1. Click **Flash**.

   - Linux:

      1. Make sure the SD card drive and its partitions, if any, are unmounted. You can use `lsblk` to find the name of your SD card block device. To unmount all partitions, run:

         ```
         ls /dev/mmcblkX?* | xargs -n1 umount -l
         ```

      1. Use `dd`:

         ```
         gunzip -c console-image-lmp-<MACHINE_NAME>-<timestamp>.rootfs.wic.gz | sudo dd bs=4M of=/dev/mmcblkX conv=fsync status=progress iflag=fullblock oflag=direct
         ```

### Flash the i.MX 8M Mini eMMC

If you are using the i.MX 8M Mini, you must flash the device with the built image. This writes the internal boot tools to the device. You only need to do this once:

1. Navigate to the root of your work directory (one folder above the build-folder).

1. Build the mfgtool-files for flashing:

   ```
   DISTRO=lmp-mfgtool MACHINE=imx8mmevk source setup-environment build-mfgtool
   bitbake mfgtool-files
   ```

1. Navigate to the mfgtools directory:

   ```
   cd deploy/images/imx8mmevk/mfgtool-files
   ```

1. Update file paths in `full_image.uuu`:

   ```
   ...
   FB: flash -raw2sparse all ../../../../../build-lmp/deploy/images/imx8mmevk/console-image-lmp-imx8mmevk.wic
   FB: flash bootloader ../../../../../build-lmp/deploy/images/imx8mmevk/imx-boot-imx8mmevk
   FB: flash bootloader2 ../../../../../build-lmp/deploy/images/imx8mmevk/u-boot-imx8mmevk.itb
   FB: flash bootloader_s ../../../../../build-lmp/deploy/images/imx8mmevk/imx-boot-imx8mmevk
   FB: flash bootloader2_s ../../../../../build-lmp/deploy/images/imx8mmevk/u-boot-imx8mmevk.itb
   FB: flash sit ../../../../../build-lmp/deploy/images/imx8mmevk/sit-imx8mmevk.bin
   ...
   ```

1. Set your i.MX 8M Mini EVK board to a download mode, and turn it on. Please consult your user guide on how to do this.

1. Flash the eMMC of the board using `uuu`:

   ```
   sudo ./uuu full_image.uuu
   ```

1. Set your i.MX 8M Mini EVK back to the SD card boot mode.

### Special notes on Hummingboard Ripple and Pulse

- Boot mode configuration information at [Solidrun's website](https://developer.solid-run.com/knowledge-base/hummingboard-pulse-ripple-boot-select/).
- Ethernet cable must be connected to CON1 -connector (closest to power connector).
- If you want to use the internal eMMC, you must connect the device to the PC from the topmost USB port with a USB type-A to USB type-A OTG cable. To usE eMMC, see the instructions above for i.MX8.

## Running Izuma Edge

After preparing the SD card (and flashing the device in the case of i.MX 8M Mini EVK), you are ready to run Izuma Edge:

1. Insert the prepared SD card.

1. Power on the device.

1. Use the serial connection of your board to communicate with the device. Please consult the user's guide of your board on how to do this.

1. Modify the login credentials:

   The default login user for the system is `fio`. The default password is set to `fio`. The user has sudo-permissions. To modify the default password, use the `passwd` command after login.

### BYOC mode provisioning steps

If you are using BYOC mode, after you boot the device and log in, you must provision the device with certificates:

1. Create and download a developer certificate to allow the edge gateway to connect to your Pelion account:

   1. Go to the Pelion Device Management Portal:

      - [United States](https://portal.mbedcloud.com/).

   1. Go to **Device Identity** > **Certificates**.
   1. Create a developer certificate.
   1. Download the certificate:

      `mbed_cloud_dev_credentials.c`

   <span class="notes">**Note:** The downloaded developer certificate is **confidential to your account**. Store it securely in a place where unauthorized users can't access or use it.</span>

1. Create the corresponding `update_default_resources.c` file, so you can apply firmware updates to the gateway device:

   ```
   manifest-dev-tool init
   ```

   For more information, please refer to the [manifest tool](https://github.com/PelionIoT/manifest-tool/blob/v2.1.0/README.md#quick-start) README.

1. Securely copy the above files to the gateway, and place them at `/userdata/mbed/`:

   ```
   scp mbed_cloud_dev_credentials.c update_default_resources.c fio@<gw-ip-address>:~/
   ssh fio@<gw-ip-address>
   sudo mv *.c /userdata/mbed/
   ```

   The gateway is now connected to Pelion Device Management.

1. Run `sudo info` on the gateway to validate the gateway's connection to Device Management and find the gateway's `Device ID`. You can view and access the gateway through the Pelion Device Management Portal:

   - [United States](https://portal.mbedcloud.com/).

## Licenses

Each LmP (and Yocto) build includes a large list of third-party IP (TPIP). Each build produces a license manifest file as part of the build process. This file is human readable but difficult to create summaries of. It is also difficult to compare changes between license manifest files in different builds or releases.

We recommend you use the open-source tool [licensetool](https://github.com/PelionIoT/licensetool/) to convert the license manifest files into `.csv` and `.xlsx` formats and highlight the changes between two different license manifest files.

## Troubleshooting

Please see the meta-pelion-edge [GitHub issues](https://github.com/PelionIoT/meta-pelion-edge/issues) for solutions to common build errors.

### fatal error: ld terminated with signal 9 [Killed]

If you see "signal 9" in the logs, it means your build machine has run out RAM memory, such as in this example log:

```
| collect2: fatal error: ld terminated with signal 9 [Killed]
| compilation terminated.
| cctest.target.mk:187: recipe for target 'Release/cctest' failed
| make[1]: *** [Release/cctest] Error 1
| make[1]: *** Waiting for unfinished jobs....
| collect2: fatal error: ld terminated with signal 9 [Killed]
| compilation terminated.
| mkcodecache.target.mk:163: recipe for target 'Release/mkcodecache' failed
| make[1]: *** [Release/mkcodecache] Error 1
| rm 512ddb6f206424ba3727f48e2a88c3e2a07f1f7a.intermediate 2717ed055cefa06f0c281328677fc46980107d50.intermediate 86dcd984228c4d2848091aaa2f7c778b4720a32a.intermediate
| Makefile:101: recipe for target 'node' failed
| make: *** [node] Error 2
| WARNING: /home/user/poky/build/tmp/work/cortexa7t2hf-neon-vfpv4-poky-linux-gnueabi/nodejs/12.14.1-r0/temp/run.do_compile.2301077:1 exit 1 from 'exit 1'
```

### autoconf fails

The Yocto build system does not support work areas behind symbolic links. You likely have a symbolic link to your work area if you see an error like this:

```
/2.69-r11/autoconf-2.69/man/autoupdate' | sed 's,.*/,,'`
/bin/bash: line 3: help2man: command not found
Makefile:460: recipe for target '../../../../../../../../../../home/tappuh01/workspace/disk2/abb_ngww_master/build-lmp/tmp-lmp/work/x86_64-linux/autoconf-native/2.69-r11/autoconf-2.69/man/config.sub.1' failed
make[2]: *** [../../../../../../../../../../home/tappuh01/workspace/disk2/abb_ngww_master/build-lmp/tmp-lmp/work/x86_64-linux/autoconf-native/2.69-r11/autoconf-2.69/man/config.sub.1] Error 127
make[2]: *** Waiting for unfinished jobs....
/bin/bash: line 3: help2man: command not found
Makefile:460: recipe for target '.
```

### upstream 'master' -> 'main' branch renaming causing fetch failures

Upstream recipe maintainers are slowly transitioning primary branches from being named `master` to `main`. However, when `bitbake` is fetching sources, the tool by default only checks one branch, even if the reference exists in the remote repository.

This workaround is only required for packages with the following output failure pattern (with `go-systemd` used as an example):

```
WARNING: go-systemd-4+gitAUTOINC+b4a58d9518-r0 do_fetch: Failed to fetch URL git://github.com/coreos/go-systemd.git, attempting MIRRORS if available
ERROR: go-systemd-4+gitAUTOINC+b4a58d9518-r0 do_fetch: Fetcher failure: Unable to find revision b4a58d95188dd092ae20072bac14cece0e67c388 in branch master even from upstream
ERROR: go-systemd-4+gitAUTOINC+b4a58d9518-r0 do_fetch: Fetcher failure for URL: 'git://github.com/coreos/go-systemd.git'. Unable to fetch URL from any source.
ERROR: Logfile of failure stored in: /ws/build/tmp/work/cortexa7t2hf-neon-vfpv4-poky-linux-gnueabi/go-systemd/4+gitAUTOINC+b4a58d9518-r0/temp/log.do_fetch.81
ERROR: Task (/ws/meta-virtualization/recipes-devtools/go/go-systemd_git.bb:do_fetch) failed with exit code '1'
```

To fix locally:

1. Locate and open the recipe that failed.

   In the above example, this is `/ws/meta-virtualization/recipes-devtools/go/go-systemd_git.bb`.

1. Locate the line starting with `SRC_URI =`. Immediately after that line, add a new line:

   `SRC_URI.=;branch=main`

1. Restart the build by running:

   `bitbake console-image-lmp --continue`
