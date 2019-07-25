DESCRIPTION = "Pelion Relay-Terminal for web based ssh sessions"

LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/Apache-2.0;md5=89aea4e17d99a7cacdbeed46a0096b10"

RT_SERVICE_FILE = "pelion-relay-term.service"
PR = "r1"


SRC_URI = "file://${RT_SERVICE_FILE}"

inherit pkgconfig systemd


SYSTEMD_PACKAGES = "${PN}"
SYSTEMD_SERVICE_${PN} = "${RT_SERVICE_FILE}"
SYSTEMD_AUTO_ENABLE_${PN} = "enable"


RDEPENDS_${PN} += "bash"


do_install() {
    install -d ${D}${systemd_system_unitdir}
    install -m 755 ${WORKDIR}/${RT_SERVICE_FILE} ${D}${systemd_system_unitdir}/${RT_SERVICE_FILE}
}



