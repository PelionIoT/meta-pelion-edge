
SUMMARY = "C binding automation"
HOMEPAGE = "http://github.com/MatthieuDartiailh/pyclibrary"
AUTHOR = "PyCLibrary Developers <m.dartiailh@gmail.com>"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=3e0779d1fc89e60083c1d63c61507990"

SRC_URI = "https://files.pythonhosted.org/packages/ef/4a/1ce545fc5451e22a2c0dacbc2f22c9b97ccfec2df7c7c9d27e52dca31452/pyclibrary-0.1.7.tar.gz"
SRC_URI[md5sum] = "bc233de3220898842f8570adba8c4541"
SRC_URI[sha256sum] = "91ed4479754ef21744d8056f8d6c2d269ae7832a90c1e1d4b1128ab0fa76ed18"

S = "${WORKDIR}/pyclibrary-0.1.7"

RDEPENDS_${PN} = "python3-future python3-pyparsing"

inherit setuptools3
