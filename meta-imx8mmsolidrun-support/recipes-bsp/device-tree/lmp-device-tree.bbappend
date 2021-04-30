FILESEXTRAPATHS_prepend := "${THISDIR}/${PN}:"

SRC_URI_append_imx8mmsolidrun = " \
        file://freescale_imx8mm-solidrun.dts \
"
COMPATIBLE_MACHINE_imx8mmsolidrun = ".*"
