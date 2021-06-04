
SUMMARY = "Clean single-source support for Python 3 and 2"
HOMEPAGE = "https://python-future.org"
AUTHOR = "Ed Schofield <ed@pythoncharmers.com>"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=a253924061f8ecc41ad7a2ba1560e8e7"

SRC_URI = "https://files.pythonhosted.org/packages/45/0b/38b06fd9b92dc2b68d58b75f900e97884c45bedd2ff83203d933cf5851c9/future-0.18.2.tar.gz"
SRC_URI[md5sum] = "e4579c836b9c025872efe230f6270349"
SRC_URI[sha256sum] = "b1bead90b70cf6ec3f0710ae53a525360fa360d306a86583adc6bf83a4db537d"

S = "${WORKDIR}/future-0.18.2"

RDEPENDS_${PN} = ""

inherit setuptools3
