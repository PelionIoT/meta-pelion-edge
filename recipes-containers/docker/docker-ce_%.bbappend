FILESEXTRAPATHS_prepend := "${THISDIR}/files:"
SRC_URI += "file://daemon.json"

FILES_${PN} += "/etc/docker/daemon.json"

do_install_append () {
    install -d ${D}/${sysconfdir}/docker
    install -m 0644 ${WORKDIR}/daemon.json ${D}/${sysconfdir}/docker/
}