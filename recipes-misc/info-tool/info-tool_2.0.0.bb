DESCRIPTION = "Information command to display key Pelion stats"

LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${WORKDIR}/git/pe-utils/LICENSE;md5=1dece7821bf3fd70fe1309eaa37d52a2"

SRC_URI="\
git://git@github.com/armPelionEdge/pe-utils.git;protocol=https;name=pe-utils;destsuffix=git/pe-utils \
"

#SRCREV_FORMAT = "wwrelay-dss"
SRCREV_pe-utils = "2.0.7"

inherit pkgconfig gitpkgv edge

PV = "1.0+git${SRCPV}"
PKGV = "1.0+git${GITPKGV}"
PR = "r0"

DEPENDS = ""
RDEPENDS_${PN} += " bash curl bc"

RM_WORK_EXCLUDE += "${PN}"

FILES_${PN} = "\
 ${EDGE_BIN} \
 ${EDGE_LIB}/bash \
"

S = "${WORKDIR}/git"

do_install() {
	install -d ${D}${EDGE_BIN}
	install -d ${D}${EDGE_LIB}/bash
	install -m 0755 -o fio -g fio -d ${D}${EDGE_DATA}/info
	install -m 0755 ${S}/pe-utils/info-tool/info ${EDGE_BIN}/
	install -m 0755 ${S}/pe-utils/info-tool/procinfo ${EDGE_BIN}/
	install -m 0755 ${S}/pe-utils/info-tool/json2sh ${EDGE_BIN}/
	install -m 0755 ${S}/pe-utils/info-tool/common.sh ${EDGE_LIB}/bash/
	install -m 0755 ${S}/pe-utils/info-tool/math.sh ${EDGE_LIB}/bash/
	install -m 0755 ${S}/pe-utils/info-tool/json.sh ${EDGE_LIB}/bash/
}
