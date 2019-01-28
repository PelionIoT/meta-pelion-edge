DESCRIPTION = "Password Generator"
SUMMARY = "Pwgen is a small, GPL'ed password generator which creates passwords which can be easily memorized by a human."
LICENSE = "GPLv2"
SECTION = "passwd/security"
LIC_FILES_CHKSUM = "file://pwgen.c;md5=82a2b376af0e15fb90a734b9edf31334"

SRC_URI = "http://sourceforge.net/projects/${BPN}/files/${BPN}/${PV}/${BP}.tar.gz"
SRC_URI[md5sum] = "910b1008cdd86445e9e01305d21ee4c5"
SRC_URI[sha256sum] = "eb74593f58296c21c71cd07933e070492e9222b79cedf81d1a02ce09c0e11556"

S = "${WORKDIR}/${BP}"

inherit autotools-brokensep pkgconfig
DEPENDS_${PN} = "pwgen-native"

BBCLASSEXTEND = "native"
