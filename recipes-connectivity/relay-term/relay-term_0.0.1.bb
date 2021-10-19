DESCRIPTION = "Pelion Relay-Terminal for web based ssh sessions"

LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://src/${GO_IMPORT}/LICENSE;md5=58b1e0eba1968eab8a0f46444674102a"

RT_SERVICE_FILE = "pelion-relay-term.service"
PR = "r1"
S = "${WORKDIR}/git/relay-term"

FILES_${PN} += "\
    ${systemd_system_unitdir}/${PN}-watcher.service\
    ${systemd_system_unitdir}/${PN}-watcher.path\
    "

SRC_URI = "git://git@github.com/PelionIoT/pe-terminal.git;protocol=https \
file://${RT_SERVICE_FILE} \
file://${BPN}-watcher.service \
file://${BPN}-watcher.path \
"
GO_IMPORT = "github.com/PelionIoT/pe-terminal/"

SRCREV = "5be41d5f8ae5a6e93643d0067533124e11d5d3a0"

inherit pkgconfig systemd go

SYSTEMD_PACKAGES = "${PN}"
SYSTEMD_SERVICE_${PN} = "${RT_SERVICE_FILE} \
${PN}-watcher.service \
${PN}-watcher.path"

SYSTEMD_AUTO_ENABLE_${PN} = "enable"

DEPENDS =""
RDEPENDS_${PN} += ""

do_install() {

    edge_replace_vars ${RT_SERVICE_FILE} ${BPN}-watcher.path

    install -d ${D}${EDGE_BIN}
    install -d ${D}${systemd_system_unitdir}
    install -m 755 ${WORKDIR}/${RT_SERVICE_FILE} ${D}${systemd_system_unitdir}/${RT_SERVICE_FILE}
    install -m 755 ${WORKDIR}/${PN}-watcher.service ${D}${systemd_system_unitdir}/${PN}-watcher.service
    install -m 755 ${WORKDIR}/${PN}-watcher.path ${D}${systemd_system_unitdir}/${PN}-watcher.path
    install -m 0755 ${B}/${GO_BUILD_BINDIR}/pe-terminal ${D}${EDGE_BIN}/pe-terminal

}