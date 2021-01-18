DESCRIPTION = "Utilities used by the WigWag Relay"

LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${WORKDIR}/git/pe-utils/LICENSE;md5=1dece7821bf3fd70fe1309eaa37d52a2"

SRC_URI="\
git://git@github.com/armPelionEdge/pe-utils.git;protocol=ssh;name=pe-utils;destsuffix=git/pe-utils \
file://wait-for-pelion-identity.service \
"

#SRCREV_FORMAT = "wwrelay-dss"
SRCREV_pe-utils = "365c21002df48f310c286b21a01c496d31929df7"

inherit pkgconfig gitpkgv systemd

#INHIBIT_PACKAGE_STRIP = "1"

SYSTEMD_PACKAGES = "${PN}"
SYSTEMD_SERVICE_${PN} = "wait-for-pelion-identity.service"
SYSTEMD_AUTO_ENABLE_${PN} = "enable"

PV = "1.0+git${SRCPV}"
PKGV = "1.0+git${GITPKGV}"
PR = "r0"

DEPENDS = ""
RDEPENDS_${PN} += " bash curl"

RM_WORK_EXCLUDE += "${PN}"

FILES_${PN} = "\
/wigwag/* \
${systemd_system_unitdirsystemd_system_unitdir}/wait-for-pelion-identity.service \
"

S = "${WORKDIR}/git"
PU = "${D}/wigwag/wwrelay-utils/identity-tools"

do_install() {
	install -d ${PU}/developer_identity
	install -m 0755 ${S}/pe-utils/identity-tools/generate-identity.sh ${PU}/
	install -m 0755 ${S}/pe-utils/identity-tools/developer_identity/create-dev-identity.sh ${PU}/developer_identity/
	install -m 0755 ${S}/pe-utils/identity-tools/developer_identity/radioProfile.template.json ${PU}/developer_identity/
	install -m 0755 ${S}/pe-utils/identity-tools/developer_identity/common.sh ${PU}/developer_identity/

	# Install systemd units
	install -d ${D}${systemd_system_unitdir}
	install -m 644 ${WORKDIR}/wait-for-pelion-identity.service ${D}${systemd_system_unitdir}/wait-for-pelion-identity.service
}



