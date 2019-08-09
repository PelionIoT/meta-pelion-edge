# Meta for Pelion Edge firmware

This repository contains the package and recipes that make up the meta layer for Pelion Edge.  It is intended to be used as part of a Yocto Linux build.
This repository also includes instructions for [Building](BUILD.md) and [Flashing](https://github.com/armpelionedge/meta-pelion-edge/blob/master/FLASH.md) a complete firmware image.

Optionally, there are two automation repositories for convenience.

[Repo Manifest](https://github.com/armpelionedge/manifest-pelion-edge) can be used to manage all the repositories (including this one) used for a complete build.

[Build Environment](https://github.com/armpelionedge/build-pelion-edge) provides a Docker container with all of the requirements for build and a Makefile for building using the make utility.


## Supported Platforms
Raspberry Pi 3B+

## Images
This meta currently defines recipes for two images

* ww-console-image-initramfs - an initramfs image used as middle boot for upgrade processing (loaded by u-boot)

* console-image - the complete runtime filesystem image (switched to at the conclusion of initramfs processing)

These images and u-boot are included in the resultant complete flashable image.



### Adding packages
Packages are addded in the file 'meta-pelion-edge/recipes-core/images/console-image.bb'

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
