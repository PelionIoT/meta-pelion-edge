FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"

SRC_URI += " \
	file://ostreecheck \
"

PACKAGES:append = " initramfs-module-ostreecheck"

SUMMARY:initramfs-module-ostreecheck = "initramfs support for ostree based filesystems"
RDEPENDS:initramfs-module-ostreecheck = "${PN}-base ostree"
FILES:initramfs-module-ostreecheck = "/init.d/985-ostreecheck"

do_install:append() {
	install -m 0755 ${WORKDIR}/ostreecheck ${D}/init.d/985-ostreecheck
}
