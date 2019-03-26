ESCRIPTION = "Emacs"
HOMEPAGE = "http://www.gnu.org/software/emacs/"
LICENSE = "GPL-3.0"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/GPL-3.0;md5=c79ff39f19dfec6d293b95dea7b07891"
#LICENSE = "GPLv3"
#LIC_FILES_CHKSUM = "file://COPYING;md5=1ebbd3e34237af26da5dc08a4e440464"

PR = "r1"

SRC_URI = "file://emacs.tar.gz;name=tarball \
"
SRC_URI[tarball.md5sum] = "34405165fcd978fbc8b304cbd99ccf4f"
SRC_URI[tarball.sha256sum] = "b9a2b8434052771f797d2032772eba862ff9aa143029efc72295170607289c18"

inherit pkgconfig

S = "${WORKDIR}"

FILES_${PN} = "/usr/*"

INHIBIT_PACKAGE_DEBUG_SPLIT = "1"
INHIBIT_PACKAGE_STRIP = "1"
PACKAGES= "${PN}"

RDEPENDS_${PN} = "bash zlib ncurses-libtinfo"

do_install(){
	cd ${S}
    	install -d ${D}/usr
    	cp -r ${S}/usr/ ${D}/
}
