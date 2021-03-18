inherit cargo

SRC_URI = "git://github.com/parallaxsecond/parsec-tool;rev=bae78ec41e03540ba5e96b1d4980f3445ab7d7af;protocol=https;branch=main"

LIC_FILES_CHKSUM = "file://${WORKDIR}/git/LICENSE;md5=3b83ef96387f14655fc854ddc3c6bd57"

SUMMARY = "Parsec tool"
HOMEPAGE = "https://github.com/parallaxsecond/parsec-tool"
LICENSE="Apache-2.0"

# Installed packages
PACKAGES = "${PN} ${PN}-dbg"
FILES_${PN} += "${bindir}/parsec-tool"
FILES_${PN}-dbg += "${bindir}/.debub/parsec-tool"


S = "${WORKDIR}/git"

BBCLASSEXTEND = "native"

TOOLCHAIN = "clang"

CARGO_DISABLE_BITBAKE_VENDORING = "1"
CARGO_VENDORING_DIRECTORY="${S}/vendor"

do_configure_append() {
    cd ${S}
    cargo vendor
}


do_install() {
    mkdir -p "${D}/${bindir}"
    install -m 755 "${B}/target/${TARGET_SYS}/release/parsec-tool" "${D}${bindir}/parsec-tool"

}


