DESCRIPTION = "Pelion Relay-Terminal for web based ssh sessions"

LICENSE = "Apache-2.0"

GO_IMPORT = "github.com/PelionIoT/pe-terminal"

# avoid the `-linkshared` option in this recipe as it causes a panic
GO_LINKSHARED=""

inherit pkgconfig systemd go gitpkgv edge

RT_SERVICE_FILE = "pelion-relay-term.service"
PR = "r1"

SRC_URI = "git://${GO_IMPORT};protocol=https \
file://${RT_SERVICE_FILE} \
file://${BPN}-watcher.service \
file://${BPN}-watcher.path \
"

LIC_FILES_CHKSUM = "file://src/${GO_IMPORT}/LICENSE;md5=86d3f3a95c324c9479bd8986968f4327"

SRCREV = "v${PV}"
SYSTEMD_PACKAGES = "${PN}"
SYSTEMD_SERVICE_${PN} = "${RT_SERVICE_FILE} \
${PN}-watcher.service \
${PN}-watcher.path"

SYSTEMD_AUTO_ENABLE_${PN} = "enable"

FILES_${PN} += "\
    ${systemd_system_unitdir}/${PN}-watcher.service\
    ${systemd_system_unitdir}/${PN}-watcher.path\
    ${systemd_system_unitdir}/${RT_SERVICE_FILE}\
    ${EDGE_BIN}/pe-terminal\
    "

do_compile() {
  cd src/${GO_IMPORT}
  ${GO} build
  #next 2 lines: workaround for permission error during yocto cleanup
  cd ${B}
  chmod -R u+w *
  cd ${S}/../

  edge_replace_vars ${RT_SERVICE_FILE} ${BPN}-watcher.path
}

do_install() {

    install -d ${D}${EDGE_BIN}
    install -d ${D}${systemd_system_unitdir}
    install -m 0644 ${S}/../${RT_SERVICE_FILE} ${D}${systemd_system_unitdir}/${RT_SERVICE_FILE}
    install -m 0644 ${S}/../${PN}-watcher.path ${D}${systemd_system_unitdir}/${PN}-watcher.path
    install -m 0644 ${S}/../${PN}-watcher.service ${D}${systemd_system_unitdir}/${PN}-watcher.service
    install -m 0755 ${S}/src/${GO_IMPORT}/pe-terminal ${D}${EDGE_BIN}/pe-terminal

}
