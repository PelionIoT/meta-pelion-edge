# BOOT Capsule generation

IMAGE_TYPEDEP_capsule = "wic"

IMAGE_CMD_capsule () {
    bbwarn "DYER: IMAGE_CMD_capsule"

    mkimage -f u-boot-caps.its u-boot-caps.itb
    mkeficapsule --fit u-boot-caps.itb -i 1 u-boot-caps.bin
}

# vim:set ts=4 sw=4 sts=4 expandtab:
