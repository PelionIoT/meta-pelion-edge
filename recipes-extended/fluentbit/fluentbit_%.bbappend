# Adding default Fluentbit configuration.
# Adding watcher service to restart Fluenbit when configuration is updated.

FILESEXTRAPATHS_prepend := "${THISDIR}/files:"

FB_PKG_NAME = "td-agent-bit"
FB_CONF_FILES_LOCATION = "/etc/${FB_PKG_NAME}"

# To install fluentbit with systemd headers
DEPENDS_append = " systemd"

# Enable SystemD input plugin
EXTRA_OECMAKE += "-DFLB_IN_SYSTEMD=On "

FILES_${PN} += "\
    ${systemd_system_unitdir}/${FB_PKG_NAME}-watcher.service\
    ${systemd_system_unitdir}/${FB_PKG_NAME}.path\
    "

SRC_URI = "http://fluentbit.io/releases/1.3/fluent-bit-${PV}.tar.gz \
            ${@bb.utils.contains('DISTRO_FEATURES','usrmerge','file://0001-support-usrmerge.patch','',d)} \
            file://${FB_PKG_NAME}.service \
            file://${FB_PKG_NAME}.conf \
            file://${FB_PKG_NAME}-watcher.service \
            file://${FB_PKG_NAME}.path"

SYSTEMD_PACKAGES = "${PN}"
SYSTEMD_SERVICE_${PN} = "${FB_PKG_NAME}.service \
${FB_PKG_NAME}-watcher.service \
${FB_PKG_NAME}.path"

do_install_append() {
    install -d ${D}${FB_CONF_FILES_LOCATION}
    install -m 0644 ${WORKDIR}/${FB_PKG_NAME}.conf ${D}${FB_CONF_FILES_LOCATION}/${FB_PKG_NAME}.conf

    install -d ${D}${systemd_system_unitdir}
    install -m 0644 ${WORKDIR}/${FB_PKG_NAME}-watcher.service ${D}${systemd_system_unitdir}/${FB_PKG_NAME}-watcher.service
    install -m 0644 ${WORKDIR}/${FB_PKG_NAME}.path ${D}${systemd_system_unitdir}/${FB_PKG_NAME}.path
    install -m 0644 ${WORKDIR}/${FB_PKG_NAME}.service ${D}${systemd_system_unitdir}/${FB_PKG_NAME}.service
}