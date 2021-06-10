FILESEXTRAPATHS_prepend := "${THISDIR}/${PN}:${THISDIR}/u-boot-fio:"

SRC_URI_append_imx8mmsolidrun = " \
        file://0001-FIO-extras-imx8mm-solidrun-create-initial-support-ba.patch \
        file://0002-FIO-extras-imx8mm-solidrun-apply-changes-from-SolidR.patch \
        file://imx8mmsolidrun.cfg \
"
