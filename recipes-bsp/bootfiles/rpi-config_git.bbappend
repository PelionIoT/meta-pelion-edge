
COMPATIBLE_MACHINE = "raspberrypi3"

do_deploy_append() {
    if [ -z "${MENDER_ARTIFACT_NAME}" ]; then
        if [ -n "${KERNEL_IMAGETYPE}" ]; then
            sed -i '/#kernel=/ c\kernel=${KERNEL_IMAGETYPE}' ${DEPLOYDIR}/bcm2835-bootfiles/config.txt
        fi
    fi

    if [ -n "${DISABLE_SPLASH}" ]; then
        sed -i '/#disable_splash=/ c\disable_splash=${DISABLE_SPLASH}' ${DEPLOYDIR}/bcm2835-bootfiles/config.txt
    fi

    # HDMI display rotation
    # 0 = none, 1 = 90cw, 2 = 180cw, 3 = 270cw, 0x10000 = hflip, 0x20000 = vflip
    if [ -n "${DISPLAY_ROTATE}" ]; then
        sed -i '/#display_rotate=/ c\display_rotate=${DISPLAY_ROTATE}' ${DEPLOYDIR}/bcm2835-bootfiles/config.txt
    fi

    if [ -n "${ENABLE_SERIAL_CONSOLE}" ]; then
        echo "" >> ${DEPLOYDIR}/bcm2835-bootfiles/config.txt
        echo "dtoverlay=pi3-disable-bt" >> ${DEPLOYDIR}/bcm2835-bootfiles/config.txt
    fi

    # match the u-boot.bin install name set in IMAGE_BOOT_FILES by meta-raspberrypi/conf/machine/include/rpi-base.inc
    #if [ "${RPI_USE_U_BOOT}" = "1" ]; then
    #    sed -i 's/kernel=.*/kernel=${SDIMG_KERNELIMAGE}/' ${DEPLOYDIR}/bcm2835-bootfiles/config.txt
    #fi
}
