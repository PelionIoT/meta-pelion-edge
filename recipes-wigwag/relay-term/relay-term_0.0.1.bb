DESCRIPTION = "Pelion Relay-Terminal for web based ssh sessions"

LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/Apache-2.0;md5=89aea4e17d99a7cacdbeed46a0096b10"

RT_SERVICE_FILE = "pelion-relay-term.service"
PR = "r1"
S = "${WORKDIR}/git/relay-term"

FILES_${PN} += "\
    ${systemd_system_unitdir}/${PN}-watcher.service\
    ${systemd_system_unitdir}/${PN}-watcher.path\
    /wigwag/wigwag-core-modules/*\
    "

SRC_URI = "git://git@github.com/armPelionEdge/edge-node-modules.git;protocol=https \
npmsw://${THISDIR}/files/npm-shrinkwrap.json \
file://${RT_SERVICE_FILE} \
file://${BPN}-watcher.service \
file://${BPN}-watcher.path \
"

SRCREV = "b72acd69a13c4cb474e6ff96eb78bb9de7e99c45"

inherit pkgconfig systemd npm

SYSTEMD_PACKAGES = "${PN}"
SYSTEMD_SERVICE_${PN} = "${RT_SERVICE_FILE} \
${PN}-watcher.service \
${PN}-watcher.path"

SYSTEMD_AUTO_ENABLE_${PN} = "enable"

DEPENDS ="nodejs-native libuv"
RDEPENDS_${PN} += "bash nodejs"

do_install() {
    cd ${S}
    install -d ${D}${systemd_system_unitdir}
    install -m 755 ${WORKDIR}/${RT_SERVICE_FILE} ${D}${systemd_system_unitdir}/${RT_SERVICE_FILE}
    install -m 755 ${WORKDIR}/${PN}-watcher.service ${D}${systemd_system_unitdir}/${PN}-watcher.service
    install -m 755 ${WORKDIR}/${PN}-watcher.path ${D}${systemd_system_unitdir}/${PN}-watcher.path

    install -d ${D}/wigwag/wigwag-core-modules
    cp -r ${S} ${D}/wigwag/wigwag-core-modules/relay-term

    # Reference - https://www.yoctoproject.org/docs/3.0.1/dev-manual/dev-manual.html#npm-package-creation-requirements
    # devtool cannot detect native libraries in module dependencies. Consequently, you must manually add packages to your recipe.
    # While deploying NPM packages, devtool cannot determine which dependent packages are missing on the target (e.g. the node runtime nodejs).
    # Consequently, you need to find out what files are missing and be sure they are on the target.

    install -d ${D}/wigwag/wigwag-core-modules/relay-term/node_modules/node-pty/build
    cp -r ${S}/../../npm-build/lib/node_modules/relay-term/node_modules/node-pty/build/* ${D}/wigwag/wigwag-core-modules/relay-term/node_modules/node-pty/build/
}