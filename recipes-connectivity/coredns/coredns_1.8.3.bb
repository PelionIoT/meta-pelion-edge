DESCRIPTION = "CoreDNS is a DNS server/forwarder, written in Go, that chains plugins."
LICENSE = "Apache-2.0"
GO_IMPORT = "github.com/coredns/coredns"


LIC_FILES_CHKSUM = "file://src/${GO_IMPORT}/LICENSE;md5=dbc4913e3e1413855af21786998a0c49"
inherit go pkgconfig gitpkgv systemd edge
SRC_URI = "git://${GO_IMPORT};protocol=https;branch=master;tag=v${PV};depth=1 \
file://coredns.service \
file://coredns-resolv-watcher.service \
file://coredns-resolv-author.sh \
file://corefile \
file://coredns-rules.sh \
file://coredns-resolv.path \
file://launch-coredns.sh \
  "

SYSTEMD_PACKAGES = "${PN}"
SYSTEMD_SERVICE_${PN} = "coredns.service"
SYSTEMD_AUTO_ENABLE_${PN} = "enable"

SRCREV = "v${PV}"

PR = "r1"

DEPENDS = "git"
RDEPENDS_${PN} += "bash kubelet kube-router"


FILES_${PN} =  "\
    ${EDGE_BIN}/coredns\
    ${EDGE_COREDNS_STATE}/corefile\
    ${EDGE_BIN}/launch-coredns.sh\
    ${EDGE_BIN}/coredns-rules.sh\
    ${systemd_system_unitdir}/coredns.service\
    ${systemd_system_unitdir}/coredns-resolv-watcher.service\
    ${systemd_system_unitdir}/coredns-resolv.path\
    "

do_configure(){
  cd ../coredns-1.8.3/src/github.com/coredns/coredns/
  GOARCH=${GOHOSTARCH} CGO_ENABLED=0 go generate 
  cd ${B}
  chmod -R u+w *
}
do_compile(){
  export TMPDIR="${GOTMPDIR}"
  export KUBE_GO_PACKAGE=${GO_IMPORT}
  cd ../coredns-1.8.3/src/github.com/coredns/coredns/
  make coredns BINARY=coredns SYSTEM="GOOS=${GOOS} GOARCH=${GOARCH}" CHECKS="" BUILDOPS=""

  cd ${S}/../
  edge_replace_vars corefile launch-coredns.sh coredns.service coredns-resolv-watcher.service coredns-resolv-author.sh
#}

do_install() {
  install -d ${D}${EDGE_BIN}
  install -d ${D}${EDGE_COREDNS_STATE}
  install -d ${D}${systemd_system_unitdir}
  install -m 0755 ${S}/src/${GO_IMPORT}/coredns ${D}${EDGE_BIN}/coredns
  install -m 0755 ${S}/../coredns-rules.sh ${D}${EDGE_BIN}/coredns-rules.sh
  install -m 0755 ${S}/../launch-coredns.sh ${D}${EDGE_BIN}/launch-coredns.sh
  install -m 0755 ${S}/../coredns-resolv-author.sh ${D}${EDGE_BIN}/coredns-resolv-author.sh
  install -m 0644 ${S}/../corefile ${D}${systemd_system_unitdir}/coredns.service
  install -m 0644 ${S}/../coredns.service ${D}${EDGE_COREDNS_STATE}/corefile
  install -m 0644 ${S}/../coredns-resolv-watcher.service ${D}${systemd_system_unitdir}/coredns-resolv-watcher.service
  install -m 0644 ${S}/../coredns-resolv.path ${D}${systemd_system_unitdir}/coredns-resolv.path
  #next 2 lines worksaround a permission error when cleanup runs
  cd ${B}
  chmod -R 777 *
}
