
SUMMARY = "Python parsing module"
HOMEPAGE = "https://github.com/pyparsing/pyparsing/"
AUTHOR = "Paul McGuire <ptmcg@users.sourceforge.net>"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=657a566233888513e1f07ba13e2f47f1"

SRC_URI = "https://files.pythonhosted.org/packages/c1/47/dfc9c342c9842bbe0036c7f763d2d6686bcf5eb1808ba3e170afdb282210/pyparsing-2.4.7.tar.gz"
SRC_URI[md5sum] = "f0953e47a0112f7a65aec2305ffdf7b4"
SRC_URI[sha256sum] = "c203ec8783bf771a155b207279b9bccb8dea02d8f0c9e5f8ead507bc3246ecc1"

S = "${WORKDIR}/pyparsing-2.4.7"

RDEPENDS_${PN} = ""

inherit setuptools3
