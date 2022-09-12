FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"

SRC_URI:append_imx8mmsolidrun = " \
	file://0001-ASoC-wm8904-configure-sysclk-FLL-automatically.patch \
	file://0002-ASoC-wm8904-fix-automatic-sysclk-configuration.patch \
	file://imx8mmsolidrun-standard.scc \
	file://imx8mmsolidrun.cfg \
"
