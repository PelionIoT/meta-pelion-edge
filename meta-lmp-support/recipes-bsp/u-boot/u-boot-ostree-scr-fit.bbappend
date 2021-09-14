FILESEXTRAPATHS_prepend := "${THISDIR}/${PN}:"

SRC_URI_append_imx8mmevk = " file://sd-boot-cmd.patch;patchdir=${WORKDIR}"

# remove because imx8mmsolidrun inherits imx8mmevk
SRC_URI_remove_imx8mmsolidrun = " file://sd-boot-cmd.patch;patchdir=${WORKDIR}"
