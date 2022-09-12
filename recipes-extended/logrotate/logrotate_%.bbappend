SUMMARY = "Add logrotate conf file for /var/log/*"
FILESEXTRAPATHS:prepend := "${THISDIR}/files:"
SRC_URI += "file://varlog"

FILES:${PN} += "${sysconfdir}/logrotate.d/varlog"

LOGROTATE_SYSTEMD_TIMER_BASIS = "*:0/5"
LOGROTATE_SYSTEMD_TIMER_ACCURACY = "1m"

do_install:append() {
    install -d ${D}${sysconfdir}/logrotate.d
    install -p -m 644 ${WORKDIR}/varlog ${D}${sysconfdir}/logrotate.d/varlog
}