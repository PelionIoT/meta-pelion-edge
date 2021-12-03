DESCRIPTION = "Utilities used by the WigWag Relay"

LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${WORKDIR}/git/pe-utils/LICENSE;md5=1dece7821bf3fd70fe1309eaa37d52a2"

SRC_URI="\
git://git@github.com/armPelionEdge/pe-utils.git;protocol=https;name=pe-utils;destsuffix=git/pe-utils \
file://wait-for-pelion-identity.service \
"

#SRCREV_FORMAT = "wwrelay-dss"
SRCREV_pe-utils = "2.0.7"

inherit pkgconfig gitpkgv systemd edge

#INHIBIT_PACKAGE_STRIP = "1"

SYSTEMD_PACKAGES = "${PN}"
SYSTEMD_SERVICE_${PN} = "wait-for-pelion-identity.service"
SYSTEMD_AUTO_ENABLE_${PN} = "enable"

PV = "1.0+git${SRCPV}"
PKGV = "1.0+git${GITPKGV}"
PR = "r0"

DEPENDS = ""
RDEPENDS_${PN} += " bash curl jq"

RM_WORK_EXCLUDE += "${PN}"

FILES_${PN} = "\
	${EDGE_SCRIPTS}/identity-tools/generate-identity.sh  \
	${EDGE_SCRIPTS}/identity-tools/developer_identity/create-dev-identity.sh \
	${EDGE_SCRIPTS}/identity-tools/developer_identity/radioProfile.template.json \
	${EDGE_SCRIPTS}/identity-tools/developer_identity/common.sh \
	${EDGE_SCRIPTS}/identity-tools/developer_identity/VERSION \
	${systemd_system_unitdir}/wait-for-pelion-identity.service \
"

S = "${WORKDIR}/git"

do_compile(){
	cd ${WORKDIR}
	edge_replace_vars wait-for-pelion-identity.service
}

do_install() {
	install -d ${D}${EDGE_SCRIPTS}/identity-tools/developer_identity
	install -m 0755 ${S}/pe-utils/identity-tools/generate-identity.sh ${D}${EDGE_SCRIPTS}/identity-tools/
	install -m 0755 ${S}/pe-utils/identity-tools/developer_identity/create-dev-identity.sh ${D}${EDGE_SCRIPTS}/identity-tools/developer_identity/
	install -m 0755 ${S}/pe-utils/identity-tools/developer_identity/radioProfile.template.json ${D}${EDGE_SCRIPTS}/identity-tools/developer_identity/
	install -m 0755 ${S}/pe-utils/identity-tools/developer_identity/common.sh ${D}${EDGE_SCRIPTS}/identity-tools/developer_identity/
	install -m 0755 ${S}/pe-utils/identity-tools/developer_identity/VERSION ${D}${EDGE_SCRIPTS}/identity-tools/developer_identity/

	# Install systemd units
	install -d ${D}${systemd_system_unitdir}
	install -m 644 ${WORKDIR}/wait-for-pelion-identity.service ${D}${systemd_system_unitdir}/wait-for-pelion-identity.service
}



