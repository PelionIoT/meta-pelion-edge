# Building Yocto for Raspberry Pi

The following are the instructions used to build Yocto for Raspberry Pi with as much of the WigWag-Meta as will compiles.

Currently the 'meta-gateway-ww' has the WigWag stuff added to it for the build so we could make a clean break with the old build system.

## Install Docker

Before we begin we need to install the appropriate version of Docker CE for your operating system.

Goto: https://docs.docker.com/install/

All of the instructions in this document were built and tested with Docker CE on Ubuntu 18.10.

Ubuntu instructions at this URL: https://docs.docker.com/install/linux/docker-ce/ubuntu/

## Build environment for Wigwag gateway firmware

These instructions assume that this repo is cloned at ~/wigwag-build-env.  If you cloned the repo at a different location, you will need to make appropriate changes to the commands below.

## Build and run a Docker container

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

First the main Yocto project poky layer
```
~# git clone -b thud git://git.yoctoproject.org/poky.git poky
```
Then the dependency layers under that
```
~$ cd poky
~/poky$ git clone -b thud git://git.openembedded.org/meta-openembedded
~/poky$ git clone -b thud git://git.yoctoproject.org/meta-virtualization
~/poky$ git clone -b thud git://git.yoctoproject.org/meta-security
~/poky$ git clone -b thud git://git.yoctoproject.org/meta-raspberrypi
~/poky$ git clone -b pyro git@github.com:aaronovz1/meta-nodejs
~/poky$ git clone -b dev git@github.com:armmbed/meta-gateway-ww
```

The meta-gateway-ww/README.md contains these very instructions.

## Initialize the build directory

Use Yocto's oe-init-build-env script to create the build directory layout and provide the meta-gateway-ww/conf example configuration scripts to initialize the build environment.

```
~/poky$ TEMPLATECONF=meta-gateway-ww/conf source oe-init-build-env 
```

## WARNING: 
Do not include `meta-yocto-bsp` in your `bblayers.conf`. The Yocto BSP requirements for the Raspberry Pi are in `meta-raspberrypi`.

For example, if your directory structure does not look exactly like this, you will need to modify bblayers.conf
```
~/poky/
     meta-openembedded/
     meta-raspberrypi
     meta-gateway-ww/
     build/
        conf/
     ...
```

## Edit local.conf
Customize your local.conf to suit your build.

### ROOT PASSWORD
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
~/poky$ source oe-init-build-env

##  Shell environment set up for builds. ## 

You can now run 'bitbake '

Common targets are:
    console-image

## Build
To build the console-image run the following command
```
~/poky/build$ bitbake console-image
```
You may occasionally run into build errors related to packages that either failed to download or sometimes out of order builds. The easy solution is to clean the failed package and rerun the build again.

For instance if the build for zip failed for some reason, I would run this
```
~/poky/build$ bitbake -c cleansstate zip
~/poky/build$ bitbake zip 
```
And then continue with the full build.
```
~/poky/build$ bitbake console-image
```
The cleansstate command (with two s’s) works for image recipes as well.

### NOTE: trouble running bitbake

If you recieve an error running bitbake like:

```
Host key verification failed.
fatal: Could not read from remote repository.

Please make sure you have the correct access rights
and the repository exists.

Summary: There was 1 ERROR message shown, returning a non-zero exit code.
```

You need to fix your ssh access to github.

This can usually be fixed by the following ssh command and then rerunning bitbake:

```
$ ssh -T git@github.com
```

# burn SD card image with WIC method
Skip this if you use tar.gz or SDI methods

## copy the WIC file to a SD

First identify your SD card by inserting the microSD into your workstation and note where it shows up.

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


Next fetch the WIC image from the build folder: 
```
~/rpi/build/tmp/deploy/images/raspberrypi3
```

It will have a name something like: 
```
console-image-raspberrypi3-20190219203633.rootfs.wic
```

Then burn it to your sd card with a command like:
```
dd bs=4M if=console-image-raspberrypi3-somedate.rootfs.wic of=/dev/sdX conv=fsync
```

Note:
Be sure to replace /dev/sdX with your sd cards path and console-image-raspberrypi3-somedate.rootfs.wic with your exact file name.

# burn SD card image with tar.xz method 
Skip this if you use the WIC image

## Copying the binaries to an SD card (or eMMC)
After the build completes, the bootloader, kernel and rootfs image files can be found in **/deploy/images/$MACHINE** with **MACHINE** coming from your **local.conf**.

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
