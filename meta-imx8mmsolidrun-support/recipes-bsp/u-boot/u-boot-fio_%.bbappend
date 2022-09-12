FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"

SRC_URI:append_imx8mmsolidrun = " \
	file://0001-FIO-extras-imx8mm-solidrun-create-initial-support-ba.patch \
	file://0002-FIO-extras-imx8mm-solidrun-apply-changes-from-SolidR.patch \
	file://imx8mmsolidrun.cfg \
"
