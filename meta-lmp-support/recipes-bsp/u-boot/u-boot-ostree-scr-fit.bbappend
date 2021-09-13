FILESEXTRAPATHS_prepend := "${THISDIR}/${PN}:"

SRC_URI_append_imx8mmevk = " file://sd-boot-cmd.patch;patchdir=${WORKDIR}"
