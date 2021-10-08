FILESEXTRAPATHS_prepend := "${THISDIR}/files:"

SRC_URI_append_imx8mm-lpddr4-evk = " file://0001-Disable-1V8-on-usdhc2-for-SDMUX-kernel.patch"
