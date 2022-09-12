FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"

SRC_URI:append_imx8mmsolidrun = " \
        file://freescale_imx8mm-solidrun.dts \
"
COMPATIBLE_MACHINE_imx8mmsolidrun = ".*"
