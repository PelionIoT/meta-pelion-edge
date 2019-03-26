MESSAGE ("Building Yocto Linux target")
SET (OS_BRAND Linux)
SET (MBED_CLOUD_CLIENT_DEVICE "Yocto_Generic")
SET (PAL_TARGET_DEVICE "Yocto_Generic")

SET (PAL_USER_DEFINED_CONFIGURATION "\"${TARGET_CONFIG_ROOT}/sotp_fs_rpi3_yocto.h\"")
SET (BIND_TO_ALL_INTERFACES 0)
SET (PAL_FS_MOUNT_POINT_PRIMARY "\"/userdata/mbed/mcc_config\"")
SET (PAL_FS_MOUNT_POINT_SECONDARY "\"/userdata/mbed/mcc_config\"")
SET (PAL_UPDATE_FIRMWARE_DIR "\"/userdata/mbed/firmware\"")
SET (ARM_UC_SOCKET_TIMEOUT_MS 300000)

if (${FIRMWARE_UPDATE})
  SET (MBED_CLOUD_CLIENT_UPDATE_STORAGE ARM_UCP_LINUX_YOCTO_RPI)
endif()
