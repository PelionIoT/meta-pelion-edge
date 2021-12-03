SUMMARY = "Adds needed paths to the profile for Pelion Edge programs"

LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/Apache-2.0;md5=89aea4e17d99a7cacdbeed46a0096b10"

SRC_URI = "file://pelionpath.sh"

PR = "r0"

inherit edge

S = "${WORKDIR}"

FILES_${PN} = "${sysconfdir}"

do_compile() {
    edge_replace_vars pelionpath.sh
}

do_install() {
    install -d ${D}${sysconfdir}/profile.d
    install -m 0755 pelionpath.sh ${D}${sysconfdir}/profile.d
}


