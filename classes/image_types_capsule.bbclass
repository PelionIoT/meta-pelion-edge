# BOOT Capsule generation

IMAGE_TYPEDEP_capsule = "wic"

IMAGE_CMD_capsule () {
    bbwarn "DYER: IMAGE_CMD_capsule"

    bbwarn "IMGDEPLOYDIR: ${IMGDEPLOYDIR}"
    bbwarn "IMAGE_NAME: ${IMAGE_NAME}"
    bbwarn "TOPDIR: ${TOPDIR}"
    bbwarn "WORKDIR: ${WORKDIR}"
    bbwarn "STAGING_DIR: ${STAGING_DIR}"
    bbwarn "MACHINE: ${MACHINE}"
    bbwarn "MACHINE: ${MACHINE}"

    mkimage -f ${TOPDIR}/deploy/images/${MACHINE}/u-boot-caps.its ${TOPDIR}/deploy/images/${MACHINE}/u-boot-caps.itb
    #mkeficapsule --fit u-boot-caps.itb -i 1 u-boot-caps.bin
}

# vim:set ts=4 sw=4 sts=4 expandtab:
