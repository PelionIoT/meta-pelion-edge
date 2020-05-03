DESCRIPTION = "maetro-watchdog for ralypoint hw"

LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://src/${GO_IMPORT}/LICENSE;md5=1dece7821bf3fd70fe1309eaa37d52a2"

inherit go pkgconfig gitpkgv

PV = "1.0+git${SRCPV}"
PKGV = "1.0+git${GITPKGV}"
SRCREV = "54ee3bd50b063425606ad76aefad4167780d8760"

PR = "r0"
SRC_URI="git://git@github.com/armPelionEdge/rallypointwatchdogs.git;protocol=ssh;"
GO_IMPORT = "github.com/armPelionEdge/rallypointwatchdogs"

do_compile() {
  pushd src/${GO_IMPORT}
   ./build.sh
  popd
}

WLIB="/wigwag/system/lib"
FILES_${PN} += "/wigwag/system/bin/ /wigwag/system/lib/rp100wd.so /wigwag/system/lib/dummywd.so"

do_install() {
 install -d "${D}/${WLIB}"
 install -m 0755 "${B}/src/${GO_IMPORT}/rp100/rp100wd.so" "${D}/${WLIB}"
 install -m 0755 "${B}/src/${GO_IMPORT}/dummy/dummywd.so" "${D}/${WLIB}"
}

