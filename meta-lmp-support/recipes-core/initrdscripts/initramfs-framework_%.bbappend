FILESEXTRAPATHS_prepend := "${THISDIR}/${PN}:"

SRC_URI += " \
	file://ostreecheck \
"

PACKAGES_append = " initramfs-module-ostreecheck"

SUMMARY_initramfs-module-ostreecheck = "initramfs support for ostree based filesystems"
RDEPENDS_initramfs-module-ostreecheck = "${PN}-base ostree"
FILES_initramfs-module-ostreecheck = "/init.d/985-ostreecheck"

do_install_append() {
	install -m 0755 ${WORKDIR}/ostreecheck ${D}/init.d/985-ostreecheck
}
