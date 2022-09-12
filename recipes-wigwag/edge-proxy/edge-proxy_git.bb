DESCRIPTION = "Tunneling proxy for all FOG services"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://src/${GO_IMPORT}/LICENSE;md5=86d3f3a95c324c9479bd8986968f4327"

#As of go 1.16 go modules are required by default. The following line disables this requirement.
export GO111MODULE="auto"


inherit go pkgconfig gitpkgv systemd

PR = "r0"
SRC_URI = "git://git@github.com/PelionIoT/edge-proxy.git;protocol=https;name=ep;depth=1;branch=master \
           file://edge-proxy.service \
           file://edge-proxy-watcher.service \
           file://edge-proxy.path \
           file://launch-edge-proxy.sh \
           file://edge-proxy.conf.json \
           "

SYSTEMD_PACKAGES = "${PN}"
SYSTEMD_SERVICE:${PN} = "edge-proxy.service \
edge-proxy.path \
edge-proxy-watcher.service \
"

SYSTEMD_AUTO_ENABLE:${PN} = "enable"

SRCREV_FORMAT = "ep"
SRCREV_ep = "d2e0fcdab1481487cab243c0ed3b4dc41febc49f"
GO_IMPORT = "github.com/PelionIoT/edge-proxy"

RDEPENDS:${PN} = "jq bash"

wbindir = "/wigwag/system/bin"
wetcdir = "/wigwag/etc"
FILES:${PN} = "\
	${wbindir}/edge-proxy\
	${wbindir}/launch-edge-proxy.sh\
	${wetcdir}/edge-proxy.conf.json\
  ${systemd_system_unitdir}/edge-proxy.service\
  ${systemd_system_unitdir}/edge-proxy-watcher.service\
  ${systemd_system_unitdir}/edge-proxy.path\
	"

do_install () {
  install -d ${D}${wbindir}
  install -m 0755 ${B}/${GO_BUILD_BINDIR}/edge-proxy ${D}${wbindir}/
  install -m 0755 ${WORKDIR}/launch-edge-proxy.sh ${D}${wbindir}/
  install -d ${D}${wetcdir}
  install -m 0755 ${WORKDIR}/edge-proxy.conf.json ${D}${wetcdir}/
  install -d ${D}${systemd_system_unitdir}
  install -m 0644 ${WORKDIR}/edge-proxy.service ${D}${systemd_system_unitdir}
  install -m 0644 ${WORKDIR}/edge-proxy-watcher.service ${D}${systemd_system_unitdir}
  install -m 0644 ${WORKDIR}/edge-proxy.path ${D}${systemd_system_unitdir}
}

