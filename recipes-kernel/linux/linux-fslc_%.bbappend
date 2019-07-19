FILESEXTRAPATHS_prepend := "${THISDIR}/files:"

SRC_URI += "file://0001-Edge-increased-the-HCI_LE_AUTOCONN_TIMEOUT-to-20-sec.patch \
            file://extra-kernel-config.cfg"


do_configure_prepend() {
    ${S}/scripts/kconfig/merge_config.sh -m -O ${B} ${B}/.config ${WORKDIR}/extra-kernel-config.cfg
}
