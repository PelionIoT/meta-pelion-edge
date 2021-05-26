FILESEXTRAPATHS_prepend_imx8mmevk := "${THISDIR}/${PN}:"

SRC_URI_append_imx8mmevk += "file://0001-Disable-1V8-on-usdhc2-for-SDMUX.patch "

do_configure_append_imx8mmevk() {
    # boot from SD
    sed -i '/CONFIG_SECONDARY_BOOT_RUNTIME_DETECTION=y/d' ${B}/.config
    sed -i '/CONFIG_SECONDARY_BOOT_SECTOR_OFFSET=0x1000/d' ${B}/.config
    sed -i 's/CONFIG_ENV_FAT_DEVICE_AND_PART="2:1"/CONFIG_ENV_FAT_DEVICE_AND_PART="1:1"/g' ${B}/.config
    sed -i 's/CONFIG_BOOTCOMMAND="fatload mmc 2:1 ${loadaddr} \/boot.itb; setenv verify 1; source ${loadaddr}; reset"/CONFIG_BOOTCOMMAND="fatload mmc 1:1 ${loadaddr} \/boot.itb; setenv verify 1; source ${loadaddr}; reset"/g' ${B}/.config
}
