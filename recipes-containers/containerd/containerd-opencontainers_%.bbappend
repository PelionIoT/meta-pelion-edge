FILESEXTRAPATHS_prepend := "${THISDIR}/files:"
SRC_URI += "file://config.toml"

FILES_${PN} += "${sysconfdir}/containerd/config.toml"

inherit edge

do_compile_append () {
     edge_replace_vars ../../../config.toml
}

do_install_append () {
    install -d ${D}/${sysconfdir}/containerd
    install -m 0644 ${WORKDIR}/config.toml ${D}/${sysconfdir}/containerd/
}