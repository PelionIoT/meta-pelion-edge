# Building Yocto for Raspberry Pi

The following are the instructions used to build Yocto for Raspberry Pi with as much of the WigWag-Meta as will compiles.

Currently the 'meta-gateway-ww' has the WigWag stuff added to it for the build so we could make a clean break with the old build system.

## Build environment for Wigwag gateway firmware

The following instruction were built and tested on Ubuntu 18.10.

These instructions assume that this repo is cloned at ~/wigwag-build-env.  If you cloned the repo at a different location, you will need to make appropriate changes to the commands below.

### Build and run a Docker container

First clone the repository:

```
git clone git@github.com:ARMmbed/wigwag-build-env.git
```

The most up to date instructions on getting the Docker container started are in the README.md of wigwag-build-env. For convenience, below is a copy (as of Feb 8th 2019) of the relevant part.

* build the image

```
    cd ~/wigwag-build-env

    docker build -t wigwag-build-env_${USER} --build-arg user=${USER} --build-arg group=${USER} --build-arg uid=$(id -u) --build-arg gid=$(id -g) .
```

* run the container

```
    docker run -it -v $HOME:$HOME -v $(dirname $SSH_AUTH_SOCK):$(dirname $SSH_AUTH_SOCK) -e SSH_AUTH_SOCK=$SSH_AUTH_SOCK -e EDITOR=vim --name wigwag-build-env_${USER} wigwag-build-env_${USER}
```

You might also want to run that inside of screen to be able to detach and reattach.

## (optional) Outside of Docker

If you don't run the build in a Docker container, you'll need to manually verify the following requirements.

If you are using the container from the previous step, this step is already done for you. Switch to the Docker container and proceed with all remaining steps.

You will need the following packages installed
```
build-essential
chrpath
diffstat
gawk
libncurses5-dev
texinfo
```
For older Ubuntu's you also need to install the python 2.7 package
```
python2.7
```
And then create some links for it in /usr/bin
```
sudo ln -sf /usr/bin/python2.7 /usr/bin/python
sudo ln -sf /usr/bin/python2.7 /usr/bin/python2
```
For all versions of Ubuntu, you should change the default Ubuntu shell from dash to bash by running this command from a shell
```
sudo dpkg-reconfigure dash
```
Choose 'No' to dash when prompted.

## Clone Repositories

For all upstream repositories, use the [thud] branch.

The directory layout I am describing here is my preference. All of the paths to the meta-layers are configurable. If you choose something different, adjust the following instructions accordingly.

First the main Yocto project poky layer
```
~# git clone -b thud git://git.yoctoproject.org/poky.git poky-thud
```
Then the dependency layers under that
```
~$ cd poky-thud
~/poky-thud$ git clone -b thud git://git.openembedded.org/meta-openembedded
~/poky-thud$ git clone -b thud https://github.com/meta-qt5/meta-qt5
~/poky-thud$ git clone -b thud git://git.yoctoproject.org/meta-raspberrypi
```
These repositories shouldn’t need modifications other then periodic updates 
and can be reused for different projects or different boards.

## Clone the meta-gateway-ww repository

Create a separate sub-directory for the meta-gateway-ww repository before cloning. This is where you will be doing your customization.
```
~$ mkdir ~/rpi
~$ cd ~/rpi
~/rpi$ git clone git://github.com:ARMmbed/meta-gateway-ww.git
```
The meta-gateway-ww/README.md contains these very instructions.

## Clone the meta-nodejs repository
The meta-nodejs repository can be cloned into a sibling directory of meta-gateway-ww
```
~$ cd ~/rpi
~/rpi$ git clone -b pyro git://git@github.com:aaronovz1/meta-nodejs.git
```


## Initialize the build directory
Again much of the following are only my conventions.

Choose a build directory. I tend to do this on a per board and/or per project basis so I can quickly switch between projects. For this example I’ll put the build directory under ~/rpi/ with the meta-gateway-ww layer.

Manually create the directory structure like this

```
$ mkdir -p ~/rpi/build/conf
```

## Customize the configuration files
There are some sample configuration files in the `meta-gateway-ww/conf` directory.

Copy them to the `build/conf` directory (removing the ‘-sample’)
```
~/rpi$ cp meta-gateway-ww/conf/local.conf.sample build/conf/local.conf
~/rpi$ cp meta-gateway-ww/conf/bblayers.conf.sample build/conf/bblayers.conf
```
If you used the `oe-init-build-env` script to create the build directory, it generated some generic configuration files in the `build/conf` directory. If you want to look at them, save them with a different name before overwriting.

It is not necessary, but you may want to customize the configuration files before your first build.

## Warning:
Do not use the ‘~’ character when defining directory paths in the Yocto configuration files.

## Edit bblayers.conf
In `bblayers.conf` file replace `${HOME}` with the appropriate path to the meta-layer repositories on your system if you modified any of the paths in the previous instructions.

## WARNING: 
Do not include `meta-yocto-bsp` in your `bblayers.conf`. The Yocto BSP requirements for the Raspberry Pi are in `meta-raspberrypi`.

For example, if your directory structure does not look exactly like this, you will need to modify bblayers.conf
```
~/poky-thud/
     meta-openembedded/
     meta-qt5/
     meta-raspberrypi
     ...

~/rpi/
    meta-gateway-ww/
    build/
        conf/
```

## Edit local.conf
The variables you may want to customize are the following:
```
MACHINE
TMPDIR
DL_DIR
SSTATE_DIR
```
The defaults for all of these work fine with the exception of MACHINE.

## MACHINE
The MACHINE variable is used to determine the target architecture and various compiler tuning flags.

See the conf files under `meta-raspberrypi/conf/machine` for details.

The choices for MACHINE are
```
raspberrypi (BCM2835)
raspberrypi0 (BCM2835)
raspberrypi0-wifi (BCM2835)
raspberrypi2 (BCM2836 or BCM2837 v1.2+)
raspberrypi3 (BCM2837)
raspberrypi-cm (BCM2835)
raspberrypi-cm3 (BCM2837)
You can only build for one type of MACHINE at a time.
```
There are really just two tuning families using the default Yocto configuration files

1. arm1176jzfshf - for the the BCM2835 boards
1. cortexa7thf-neon-vfpv4 - for the BCM2836 and BCM2837 boards

Boards in the same family can generally run the same software.

One exception is u-boot, which is NOT the default for the systems being built here.

One of the reasons you would want to use u-boot with the RPis is to work with the Mender upgrade system.

## TMPDIR
This is where temporary build files and the final build binaries will end up. Expect to use at least 50GB.

The default location is under the build directory, in this example '~/rpi/build/tmp'.

If you specify an alternate location as I do in the example conf file make sure the directory is writable by the user running the build.

## DL_DIR
This is where the downloaded source files will be stored. You can share this among configurations and builds so I always create a general location for this outside the project directory. Make sure the build user has write permission to the directory you decide on.

The default location is in the build directory, '~/rpi/build/sources'.

## SSTATE_DIR
This is another Yocto build directory that can get pretty big, greater then 8GB. I often put this somewhere else other then my home directory as well.

The default location is in the build directory, '~/rpi/build/sstate-cache'.


## ROOT PASSWORD
There is only one login user by default, root.

The default password is set to 'redmbed' by these two lines in the local.conf file
```
INHERIT += "extrausers"
EXTRA_USERS_PARAMS = "usermod -P redmbed root; "
```
These two lines force a password change on first login
```
INHERIT += "chageusers"
CHAGE_USERS_PARAMS = "chage -d0 root; "
```
You can comment them out if you do not want that behavior.

If you want no password at all (development only hopefully), comment those four lines and uncomment this line
```
EXTRA_IMAGE_FEATURES = "debug-tweaks"

#INHERIT += "extrausers"
#EXTRA_USERS_PARAMS = "usermod -P redmbed root; "

#INHERIT += "chageusers"
#CHAGE_USERS_PARAMS = "chage -d0 root; "
```
You can always add or change the password once logged in.

## Run the build
You need to `source` the Yocto environment into your shell before you can use `bitbake`. The `oe-init-build-env` will not overwrite your customized conf files.
```
~$ source poky-thud/oe-init-build-env ~/rpi/build

##  Shell environment set up for builds. ## 

You can now run 'bitbake '

Common targets are:
    core-image-minimal
    core-image-sato
    meta-toolchain
    meta-toolchain-sdk
    adt-installer
    meta-ide-support

You can also run generated qemu images with a command like 'runqemu qemux86'
~/rpi/build$
```
I don’t use any of those Common targets, but instead always write my own custom image recipes.

The `meta-gateway-ww` layer has some examples under`meta-gateway-ww/images/`.


## Build
To build the console-image run the following command
```
~/rpi/build$ bitbake console-image
```
You may occasionally run into build errors related to packages that either failed to download or sometimes out of order builds. The easy solution is to clean the failed package and rerun the build again.

For instance if the build for zip failed for some reason, I would run this
```
~/rpi/build$ bitbake -c cleansstate zip
~/rpi/build$ bitbake zip 
```
And then continue with the full build.
```
~/rpi/build$ bitbake console-image
```
To build the qt5-image it would be
```
~/rpi/build$ bitbake qt5-image
```
The cleansstate command (with two s’s) works for image recipes as well.

The image files won’t get deleted from the TMPDIR until the next time you build.

## Copying the binaries to an SD card (or eMMC)
After the build completes, the bootloader, kernel and rootfs image files can be found in **/deploy/images/$MACHINE** with **MACHINE** coming from your **local.conf**.

The meta-gateway-ww/scripts directory has some helper scripts to format and copy the files to a microSD card.

See this post for an additional first step required for the RPi Compute eMMC.

## mk2parts.sh
This script will partition an SD card with the minimal 2 partitions required for the RPI.

Insert the microSD into your workstation and note where it shows up.

`lsblk` is convenient for finding the microSD card.

For example
```
~/rpi/meta-gateway-ww$ lsblk
NAME    MAJ:MIN RM   SIZE RO TYPE MOUNTPOINT
sda       8:0    0 931.5G  0 disk
|-sda1    8:1    0  93.1G  0 part /
|-sda2    8:2    0  93.1G  0 part /home
|-sda3    8:3    0  29.8G  0 part [SWAP]
|-sda4    8:4    0     1K  0 part
|-sda5    8:5    0   100G  0 part /oe5
|-sda6    8:6    0   100G  0 part /oe6
|-sda7    8:7    0   100G  0 part /oe7
|-sda8    8:8    0   100G  0 part /oe8
|-sda9    8:9    0   100G  0 part /oe9
`-sda10   8:10   0 215.5G  0 part /oe10
sdb       8:16   1   7.4G  0 disk
|-sdb1    8:17   1    64M  0 part
`-sdb2    8:18   1   7.3G  0 part
```
So I will use `sdb` for the card on this machine.

It doesn’t matter if some partitions from the SD card are mounted. The mk2parts.sh script will unmount them.

WARNING: This script will format any disk on your workstation so make sure you choose the SD card.
```
~$ cd ~/rpi/meta-gateway-ww/scripts
~/rpi/meta-gateway-ww/scripts$ sudo ./mk2parts.sh sdb
```
You only have to format the SD card once.

## Temporary mount point
You will need to create a mount point on your workstation for the copy scripts to use.

This is the default
```
~$ sudo mkdir /media/card
```
You only have to create this directory once.

If you don’t want that location, you will have to edit the following scripts to use the mount point you choose.


## copy_boot.sh
This script copies the GPU firmware, the Linux kernel, dtbs and overlays, config.txt and cmdline.txt to the boot partition of the SD card.

This `copy_boot.sh` script needs to know the `TMPDIR `to find the binaries. It looks for an environment variable called `OETMP`.

For instance, if I had this in `build/conf/local.conf`
```
TMPDIR = "/oe4/rpi/tmp-thud"
```
Then I would export this environment variable before running `copy_boot.sh`
```
~/rpi/meta-gateway-ww/scripts$ export OETMP=/oe4/rpi/tmp-thud
```
If you didn’t override the default `TMPDIR` in `local.conf`, then set it to the default `TMPDIR`
```
~/rpi/meta-gateway-ww/scripts$ export OETMP=~/rpi/build/tmp
```
The `copy_boot.sh` script also needs a `MACHINE` environment variable specifying the type of RPi board.
```
~/rpi/meta-gateway-ww/scripts$ export MACHINE=raspberrypi3
```
or
```
~/rpi/meta-gateway-ww/scripts$ export MACHINE=raspberrypi0-wifi
```
Then run the `copy_boot.sh` script passing the location of SD card
```
~/rpi/meta-gateway-ww/scripts$ ./copy_boot.sh sdb
```
This script should run very fast.

If you want to customize the config.txt or cmdline.txt files for the system, you can place either of those files in the `meta-gateway-ww/scripts` directory and the `copy_boot.sh` script will copy them as well.

Take a look at the script if this is unclear.


## copy_rootfs.sh
This script copies the root file system to the second partition of the SD card.

The `copy_rootfs.sh` script needs the same `OETMP` and `MACHINE` environment variables.

The script accepts an optional command line argument for the image type, for example console or qt5. The default is console if no argument is provided.

The script also accepts a hostname argument if you want the host name to be something other then the default `MACHINE`.

Here’s an example of how you would run `copy_rootfs.sh`
```
~/rpi/meta-gateway-ww/scripts$ ./copy_rootfs.sh sdb console
```
or
```
~/rpi/meta-gateway-ww/scripts$ ./copy_rootfs.sh sdb qt5 rpi3
```
The `copy_rootfs.sh` script will take longer to run and depends a lot on the quality of your SD card. With a good Class 10 card it should take less then 30 seconds.

The copy scripts will NOT unmount partitions automatically. If an SD card partition is already mounted, the script will complain and abort. This is for safety, mine mostly, since I run these scripts many times a day on different machines and the SD cards show up in different places.

Here’s a realistic example session where I want to copy already built images to a second SD card that I just inserted.
```
~$ sudo umount /dev/sdb1
~$ sudo umount /dev/sdb2
~$ export OETMP=/oe4/rpi/tmp-thud
~$ export MACHINE=raspberrypi2
~$ cd rpi/meta-gateway-ww/scripts
~/rpi/meta-gateway-ww/scripts$ ./copy_boot.sh sdb
~/rpi/meta-gateway-ww/scripts$ ./copy_rootfs.sh sdb console rpi3
```
Once past the development stage I usually wrap all of the above in another script for convenience.

Both `copy_boot.sh` and `copy_rootfs.sh` are simple scripts, easily customized.


## Adding additional packages
To display the list of available recipes from the meta-layers included in `bblayers.conf`
```
~$ source poky-thud/oe-init-build-env ~/rpi/build

~/rpi/build$ bitbake -s
```
Once you have the recipe name, you need to find what packages the recipe produces. Use the `oe-pkgdata-util` utility for this.

For instance, to see the packages produced by the openssh recipe
```
~/rpi/build$ oe-pkgdata-util list-pkgs -p openssh
openssh-keygen
openssh-scp
openssh-ssh
openssh-sshd
openssh-sftp
openssh-misc
openssh-sftp-server
openssh-dbg
openssh-dev
openssh-doc
openssh
```
These are the individual packages you could add to your image recipe.

You can also use `oe-pkgdata-util` to check the individual files a package will install.

For instance, to see the files for the `openssh-sshd` package
```
~/rpi/build$ oe-pkgdata-util list-pkg-files openssh-sshd
openssh-sshd:
        /etc/default/volatiles/99_sshd
        /etc/init.d/sshd
        /etc/ssh/moduli
        /etc/ssh/sshd_config
        /etc/ssh/sshd_config_readonly
        /usr/libexec/openssh/sshd_check_keys
        /usr/sbin/sshd
```
For a package to be installed in your image it has to get into the IMAGE_INSTALL variable some way or another. See the example image recipes for some common conventions.

## Adding WigWag packages
Packages are addded in the file 'meta-gateway-ww/images/console-image.bb'

Open it up with your favorite editor and scroll down to the variable WIGWAG_STUFF and you should see something like:

```
WIGWAG_STUFF = " \
    emacs \
    fftw \
    imagemagick \
    lcms \
    mbed-cloud-edge \
    panic \
    pps-tools \
    pwgen \
    tsb \
    twlib \
"
```

Add a new line before the last quote containing the name of the recipe as defined in it's <recipe>.bb file.
