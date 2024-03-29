DESCRIPTION = "CoreDNS is a DNS server/forwarder, written in Go, that chains plugins."
LICENSE = "Apache-2.0"
GO_IMPORT = "github.com/coredns/coredns"


LIC_FILES_CHKSUM = "file://src/${GO_IMPORT}/LICENSE;md5=dbc4913e3e1413855af21786998a0c49"
inherit go pkgconfig gitpkgv systemd edge
SRC_URI = "git://${GO_IMPORT};protocol=https;branch=master;tag=v${PV};depth=1 \
file://coredns.service \
file://corefile \
file://coredns-rules.sh \
file://launch-coredns.sh \
file://coredns-starter.sh \
file://coredns-starter.service \
  "

SYSTEMD_PACKAGES = "${PN}"
SYSTEMD_SERVICE_${PN} = "coredns-starter.service"
SYSTEMD_AUTO_ENABLE_${PN} = "enable"

SRCREV = "v${PV}"

PR = "r1"

DEPENDS = "git "
RDEPENDS_${PN} += "bash "


FILES_${PN} =  " \
    ${EDGE_BIN}/coredns\
    ${EDGE_BIN}/launch-coredns.sh \
    ${EDGE_BIN}/coredns-rules.sh \
    ${EDGE_BIN}/coredns-starter.sh \
    ${EDGE_COREDNS_STATE}/corefile \
    ${systemd_system_unitdir}/coredns.service \
    ${systemd_system_unitdir}/coredns-starter.service \
    "

do_configure(){
  cd ../coredns-1.8.3/src/github.com/coredns/coredns/
  GOARCH=${GOHOSTARCH} CGO_ENABLED=0 go generate 
  #next 2 lines: workaround for permission error during yocto cleanup
  cd ${B}
  chmod -R u+w *
}
do_compile(){
  export TMPDIR="${GOTMPDIR}"
  cd ../coredns-1.8.3/src/github.com/coredns/coredns/
  GITCOMMIT="$(git describe --tags --dirty)"
  BUILDTIME="$(date -u +'%Y-%m-%dT%H:%M:%SZ')"
  CGO_ENABLED=$(CGO_ENABLED) SYSTEM="GOOS=${GOOS} GOARCH=${GOARCH}" go build $(BUILDOPTS) -ldflags="-X github.com/coredns/coredns/coremain.GitCommit=${GITCOMMIT} -X github.com/coredns/coredns/coremain.gitShortStat=${BUILDTIME}" -o coredns
  #next 2 lines: workaround for permission error during yocto cleanup
  cd ${B}
  chmod -R u+w *
  cd ${S}/../
  edge_replace_vars corefile launch-coredns.sh coredns.service coredns-starter.service
}

do_install() {
  install -d ${D}${EDGE_BIN}
  install -d ${D}${EDGE_COREDNS_STATE}
  install -d ${D}${systemd_system_unitdir}
  install -m 0755 ${S}/src/${GO_IMPORT}/coredns ${D}${EDGE_BIN}/coredns
  install -m 0755 ${S}/../coredns-rules.sh ${D}${EDGE_BIN}/coredns-rules.sh
  install -m 0755 ${S}/../coredns-starter.sh ${D}${EDGE_BIN}/coredns-starter.sh  
  install -m 0755 ${S}/../launch-coredns.sh ${D}${EDGE_BIN}/launch-coredns.sh
  install -m 0644 ${S}/../coredns.service ${D}${systemd_system_unitdir}/coredns.service
  install -m 0644 ${S}/../coredns-starter.service ${D}${systemd_system_unitdir}/coredns-starter.service
  install -m 0644 ${S}/../corefile ${D}${EDGE_COREDNS_STATE}/corefile
}
