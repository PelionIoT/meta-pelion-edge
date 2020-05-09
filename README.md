# Meta for Pelion Edge firmware

This repository contains the package and recipes that make up the metalayer for Pelion Edge. It is intended to be used as part of a Yocto Linux build.

## Quick start

You can use [repo manifest](https://github.com/armpelionedge/manifest-pelion-edge) to manage all the repositories (including this one) needed for a complete build. It automates many of the manual steps and provides a guide to get started.

## Manual instructions

This repository also includes instructions for [Building](BUILD.md) and [Flashing](FLASH.md) a complete firmware image.

## Supported platforms

Raspberry Pi 3B+

## Images

This meta defines recipes for two images:

* `ww-console-image-initramfs` - an initramfs image used as middle boot for upgrade processing (loaded by u-boot).
* `console-image` - the complete runtime file system image (switched to at the conclusion of initramfs processing).

The complete flashable image includes these images and u-boot.

### Adding packages

Packages are addded in the file `meta-pelion-edge/recipes-core/images/console-image.bb`.

1. Open the file with your favorite editor.
1. Scroll down to the variable `WIGWAG_STUFF`. You will see something like:

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

1. Before the last quote, add a new line containing the name of the recipe as defined in its `<recipe>.bb` file.

## Contributing

See [Contributing.md](CONTRIBUTING.md). We use [GitHub issues](https://github.com/armPelionEdge/meta-pelion-edge/issues) to track requests and bugs.

## License

[Apache License 2.0](LICENSE)
