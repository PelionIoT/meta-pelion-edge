DESCRIPTION = "maetro is a runtime / container manager for deviceOS"

LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://src/${GO_IMPORT}/LICENSE;md5=1dece7821bf3fd70fe1309eaa37d52a2"

inherit go pkgconfig gitpkgv

PV = "1.0+git${SRCPV}"
PKGV = "1.0+git${GITPKGV}"
SRCREV = "6453fba93557fc7c4593c48022cf88395bd23a57"

PR = "r0"

SRC_URI="git://git@github.com/armPelionEdge/maestro-shell.git;protocol=ssh"

S= "${WORKDIR}/git"
GO_IMPORT = "github.com/armPelionEdge/maestro-shell"

DEPENDS +=" maestro"

FILES_${PN} += "/wigwag/system/bin/maestro-shell"
WBIN="/wigwag/system/bin"

do_install() {
 install -d ${D}/${WBIN}
 install -m 0755 "${B}/${GO_BUILD_BINDIR}/maestro-shell" "${D}/${WBIN}"
}

