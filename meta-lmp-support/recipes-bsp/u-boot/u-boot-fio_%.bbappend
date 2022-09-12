FILESEXTRAPATHS:prepend_imx8mm-lpddr4-evk := "${THISDIR}/${PN}:"

do_configure:append_imx8mm-lpddr4-evk() {
    # boot from SD
    sed -i '/CONFIG_SECONDARY_BOOT_RUNTIME_DETECTION=y/d' ${B}/.config
    sed -i '/CONFIG_SECONDARY_BOOT_SECTOR_OFFSET=0x1000/d' ${B}/.config
    sed -i 's/CONFIG_ENV_FAT_DEVICE_AND_PART="2:1"/CONFIG_ENV_FAT_DEVICE_AND_PART="1:1"/g' ${B}/.config
    sed -i 's/CONFIG_BOOTCOMMAND="fatload mmc 2:1 ${loadaddr} \/boot.itb; setenv verify 1; source ${loadaddr}; reset"/CONFIG_BOOTCOMMAND="fatload mmc 1:1 ${loadaddr} \/boot.itb; setenv verify 1; source ${loadaddr}; reset"/g' ${B}/.config
}
