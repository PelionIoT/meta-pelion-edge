DESCRIPTION = "Pelion Relay-Terminal for web based ssh sessions"

LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/Apache-2.0;md5=89aea4e17d99a7cacdbeed46a0096b10"

RT_SERVICE_FILE = "pelion-relay-term.service"
PR = "r1"

FILES_${PN} += "\
    ${systemd_system_unitdir}/${PN}-watcher.service\
    ${systemd_system_unitdir}/${PN}-watcher.path\
    "

SRC_URI = "file://${RT_SERVICE_FILE} \
file://${PN}-watcher.service \
file://${PN}-watcher.path \
"

inherit pkgconfig systemd


SYSTEMD_PACKAGES = "${PN}"
SYSTEMD_SERVICE_${PN} = "${RT_SERVICE_FILE}"
SYSTEMD_AUTO_ENABLE_${PN} = "enable"


RDEPENDS_${PN} += "bash"


do_install() {
    install -d ${D}${systemd_system_unitdir}
    install -m 755 ${WORKDIR}/${RT_SERVICE_FILE} ${D}${systemd_system_unitdir}/${RT_SERVICE_FILE}
    install -m 755 ${WORKDIR}/${PN}-watcher.service ${D}${systemd_system_unitdir}/${PN}-watcher.service
    install -m 755 ${WORKDIR}/${PN}-watcher.path ${D}${systemd_system_unitdir}/${PN}-watcher.path
}



