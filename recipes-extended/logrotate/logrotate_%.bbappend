SUMMARY = "Add logrotate conf file for /var/log/*"
FILESEXTRAPATHS_prepend := "${THISDIR}/files:"
SRC_URI += "file://varlog"

FILES_${PN} += "${sysconfdir}/logrotate.d/varlog"

do_install_append() {
    install -d ${D}${sysconfdir}/logrotate.d
    install -p -m 644 ${WORKDIR}/varlog ${D}${sysconfdir}/logrotate.d/varlog
}