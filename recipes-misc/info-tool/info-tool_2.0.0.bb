DESCRIPTION = "Information command to display key Pelion stats"

LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${WORKDIR}/git/pe-utils/LICENSE;md5=1dece7821bf3fd70fe1309eaa37d52a2"

SRC_URI="\
git://git@github.com/armPelionEdge/pe-utils.git;protocol=https;name=pe-utils;destsuffix=git/pe-utils;branch=master \
"

SRCREV_pe-utils = "2.0.9"

inherit pkgconfig gitpkgv edge

PV = "1.0+git${SRCPV}"
PKGV = "1.0+git${GITPKGV}"
PR = "r0"

DEPENDS = ""
RDEPENDS:${PN} += " bash curl bc jq"

RM_WORK_EXCLUDE += "${PN}"

FILES:${PN} = "\
${EDGE_BIN}/ \
/etc/tmpfiles.d/userdatai-tmpfiles.conf \
"

S = "${WORKDIR}/git"

do_compile() {
	cd ${S}/pe-utils/info-tool
	edge_replace_vars info
}

do_install() {
	install -d ${D}${EDGE_BIN}
	install -m 0755 ${S}/pe-utils/info-tool/info ${D}/${EDGE_BIN}/
	install -d "${D}/etc/tmpfiles.d"
    echo "d ${EDGE_DATA}/info 0777 root root -" >> "${D}/etc/tmpfiles.d/userdatai-tmpfiles.conf"
}