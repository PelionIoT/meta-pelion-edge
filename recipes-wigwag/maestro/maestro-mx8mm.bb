require maestro_0.0.1.inc

COMPATIBLE_MACHINE = "mx8mm"

PROVIDES += " maestro "
RPROVIDES_${PN} += " maestro "

FILESEXTRAPATHS_prepend := "${THISDIR}/maestro/mx8mm:"
SRC_URI += "file://maestro-config-mx8mm.yaml \
            file://relayTerm.template.json \
            file://0001-aarch64-arm.patch \
            file://0002-eventfd2.patch \
            file://0003-build-with-autoconf.patch \
            "

do_install_append() {
    # Maestro configuration management
    install -d ${D}/${RUN_CONFIG_DIR}
    install -d ${D}/${TEMPLATE_CONFIG_DIR}
    install -m 0644 ${WORKDIR}/maestro-config-mx8mm.yaml ${D}/${RUN_CONFIG_DIR}/maestro-config.yaml
    install -m 0644 ${WORKDIR}/relayTerm.template.json ${D}/${TEMPLATE_CONFIG_DIR}/relayTerm.template.json

}
