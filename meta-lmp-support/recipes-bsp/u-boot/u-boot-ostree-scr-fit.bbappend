FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"

SRC_URI:append_imx8mmevk = " file://sd-boot-cmd.patch;patchdir=${WORKDIR}"

# remove because imx8mmsolidrun inherits imx8mmevk
SRC_URI:remove_imx8mmsolidrun = " file://sd-boot-cmd.patch;patchdir=${WORKDIR}"
