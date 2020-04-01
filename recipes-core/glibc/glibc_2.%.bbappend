SUMMARY = "Introducing gai.conf to priortize IPv4 address over IPv6"
FILESEXTRAPATHS_prepend := "${THISDIR}/files:"
SRC_URI += "file://gai.conf"

FILES_${PN}-utils += "/etc/gai.conf"

do_install_append () {
    install -d ${sysconfdir}
    install -m 0644 ${WORKDIR}/gai.conf ${D}${sysconfdir}/gai.conf
}