DESCRIPTION = "Information command to display key Pelion stats"

LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${WORKDIR}/git/pe-utils/LICENSE;md5=1dece7821bf3fd70fe1309eaa37d52a2"

SRC_URI="\
git://git@github.com/armPelionEdge/pe-utils.git;protocol=https;name=pe-utils;destsuffix=git/pe-utils \
"

#SRCREV_FORMAT = "wwrelay-dss"
SRCREV_pe-utils = "6a436d6986c67f36936a40d31f43462c97b6f615"

inherit pkgconfig gitpkgv 

PV = "1.0+git${SRCPV}"
PKGV = "1.0+git${GITPKGV}"
PR = "r0"

DEPENDS = ""
RDEPENDS_${PN} += " bash curl bc"

RM_WORK_EXCLUDE += "${PN}"

FILES_${PN} = "\
/wigwag/system/bin/ \
/wigwag/system/lib/bash/ \
"

S = "${WORKDIR}/git"
BINLOCATION = "${D}/wigwag/system/bin"
LIBLOCATION = "${D}/wigwag/system/lib/bash"

do_install() {
	install -d ${BINLOCATION}
	install -d ${LIBLOCATION}
	install -m 0755 ${S}/pe-utils/info-tool/info ${BINLOCATION}/
	install -m 0755 ${S}/pe-utils/info-tool/procinfo ${BINLOCATION}/
	install -m 0755 ${S}/pe-utils/info-tool/json2sh ${BINLOCATION}/
	install -m 0755 ${S}/pe-utils/info-tool/common.sh ${LIBLOCATION}/
	install -m 0755 ${S}/pe-utils/info-tool/math.sh ${LIBLOCATION}/
	install -m 0755 ${S}/pe-utils/info-tool/json.sh ${LIBLOCATION}/
}
