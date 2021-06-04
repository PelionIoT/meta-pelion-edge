DESCRIPTION = "Tool to convert the development certificate to CBOR formatted object"

LICENSE="Apache-2.0"
LIC_FILES_CHKSUM = "file://${WORKDIR}/git/LICENSE;md5=1dece7821bf3fd70fe1309eaa37d52a2"

SRCREV = "bcaa49f8e8a4b7f7319d05bf9f16ff67e3d4912e"
SRC_URI = "git://github.com/ARMmbed/mbed-edge.git;branch=edge-tool-setup"

S = "${WORKDIR}/git/edge-tool"

FILES_${PN} = "/wigwag/mbed/edge-tool/*"

RDEPENDS_${PN} += "python3-cbor2 python3-cryptography python3-docopt python3-six python3-pyclibrary"

inherit setuptools3
