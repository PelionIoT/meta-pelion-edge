# Yocto build instructions for Pelion Edge firmware on Raspberry Pi

The following are instructions for building Pelion Edge firmware for the Raspberry Pi. Pelion Edge firmware is a Yocto Linux Raspberry Pi build which includes the meta (meta-pelion-edge) included in this repository.

For convenience, the manual instructions below have been automated in repositories for [Environment](https://github.com/armpelionedge/manifest-pelion-os-edge) and [Build](https://github.com/armpelionedge/build-pelion-os-edge).
See also [instructions for flashing the image](https://github.com/armpelionedge/meta-pelion-edge-ww/blob/master/FLASH.md) onto an SD card.

## Manual instructions

Note:
All of the instructions in this document were built and tested with Docker CE on [Ubuntu 18.10](https://docs.docker.com/install/linux/docker-ce/ubuntu/).

### Requirements

[Install Yocto system requirements](https://www.yoctoproject.org/docs/2.6.1/ref-manual/ref-manual.html#ref-manual-system-requirements)

Install additional requirements
```
sudo dpkg --add-architecture i386
sudo apt-get update
sudo apt-get install -y --no-install-recommends g++-multilib libssl-dev:i386 libcrypto++-dev:i386 zlib1g-dev:i386
sudo dpkg-reconfigure dash
```
This last command reconfigures Ubuntu/Debian to use bash as the non-interactive shell.  At the prompt, select No.

For older Ubuntu's you also need to install the python 2.7 package
```
sudo apt-get install python2.7
```

And then create some links for it in /usr/bin
```
sudo ln -sf /usr/bin/python2.7 /usr/bin/python
sudo ln -sf /usr/bin/python2.7 /usr/bin/python2
```

### Clone Repositories

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
~/poky$ git clone -b dev git@github.com:armpelionedge/meta-pelion-edge
```

### Credentials, Keys, and Certificates

#### Pelion Cloud credentials

Pelion Cloud development credentials are needed for Pelion Edge.  Provision your build with a Pelion Cloud developer certificate if you are building for [Pelion Cloud developer mode](https://cloud.mbed.com/docs/current/connecting/provisioning-development-devices.html).  Copy your mbed_cloud_dev_credentials.c file to `recipes-wigwag/mbed-edge-core/files/mbed_cloud_dev_credentials.c`.


#### Firmware Update Manifest Credentials

If you enabled support for Pelion firmware updates in mbed-edge-core, copy your manifest certificate to `recipes-wigwag/mbed-edge-core/files/update_default_resources.c`.

To generate update_default_resources.c, run [manifest-tool](https://github.com/ARMmbed/manifest-tool).  See the documentation on [getting the update resources](https://github.com/ARMmbed/mbed-edge/blob/master/README.md#getting-the-update-resources).

Note: To unlock the rich node features, such as gateway logs and gateway terminal in the Pelion web portal please specify the `vendor-id=42fa7b48-1a65-43aa-890f-8c704daade54`

### Initialize the build directory

Use Yocto's oe-init-build-env script to create the build directory layout and provide the meta-pelion-edge/conf example configuration scripts to initialize the build environment.

```
~/poky$ TEMPLATECONF=meta-pelion-edge/conf source oe-init-build-env
```

### WARNING: 
Do not include `meta-yocto-bsp` in your `bblayers.conf`. The Yocto BSP requirements for the Raspberry Pi are in `meta-raspberrypi`.

For example, if your directory structure does not look exactly like this, you will need to modify bblayers.conf
```
~/poky/
     meta-openembedded/
     meta-raspberrypi
     meta-pelion-edge/
     build/
        conf/
     ...
```

### Edit local.conf
Customize your local.conf to suit your build.

#### ROOT PASSWORD
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

### Run the build
You need to `source` the Yocto environment into your shell before you can use `bitbake`. The `oe-init-build-env` will not overwrite your customized conf files.
```
~/poky$ source oe-init-build-env
```

###  Shell environment set up for builds. ### 

You can now run 'bitbake '

Common targets are:
    console-image

### Build
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

#### NOTE: trouble running bitbake

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
