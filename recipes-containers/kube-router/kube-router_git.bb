DESCRIPTION = "kube-router provides high performance networking for kubernetes"
LICENSE = "Apache-2.0"
IMPORT = "github.com/pelioniot/kube-router"


LIC_FILES_CHKSUM = "file://src/${GO_IMPORT}/LICENSE;md5=e3fc50a88d0a364313df4b21ef20c29e"
inherit go pkgconfig gitpkgv systemd edge
SRC_URI = "git://${IMPORT};protocol=https;branch=master;depth=1 \
file://kube-router.service \
file://kube-router-watcher.service \
file://kube-router.path \
file://launch-kube-router.sh \
file://10-kuberouter.conflist \
  "

SYSTEMD_PACKAGES = "${PN}"
SYSTEMD_SERVICE_${PN} = "kube-router.service"
SYSTEMD_AUTO_ENABLE_${PN} = "enable"

SRCREV = "533593ebd47315dff4cdcd1433567bc8d341b8ed"

PR = "r1"

DEPENDS = "git"
RDEPENDS_${PN} += "bash kubelet ipset"


FILES_${PN} =  "\
    ${EDGE_BIN}/kube-router \
    ${EDGE_BIN}/launch-kube-router.sh \
    ${EDGE_CNI}/10-kuberouter.conflist \
    ${systemd_system_unitdir}/kube-router.service \
    ${systemd_system_unitdir}/kube-router-watcher.service \
    ${systemd_system_unitdir}/kube-router.path \
    "
 
GO_IMPORT = "github.com/cloudnativelabs/kube-router"

do_compile() {
  cd src/github.com/cloudnativelabs/kube-router/
  export TMPDIR="${GOTMPDIR}"
  GIT_COMMIT="$(git describe --tags --dirty)"
  timestamp="$(date ${buildDate} -u +'%Y-%m-%dT%H:%M:%SZ')"
  ${GO} build -ldflags "-X github.com/cloudnativelabs/kube-router/pkg/version.Version=${GIT_COMMIT} -X github.com/cloudnativelabs/kube-router/pkg/version.BuildDate=${timestamp}" -o kube-router cmd/kube-router/kube-router.go
  #next 2 lines: workaround for permission error during yocto cleanup
  cd ${B}
  chmod -R u+w *
  cd ${S}/../
  
  edge_replace_vars launch-kube-router.sh kube-router.service kube-router.path
}

do_install() {
  install -d ${D}${EDGE_BIN}
  install -d ${D}${EDGE_CNI}
  install -d ${D}${systemd_system_unitdir}
  install -m 0755 ${S}/src/${GO_IMPORT}/kube-router ${D}${EDGE_BIN}/kube-router
  install -m 0755 ${S}/../launch-kube-router.sh ${D}${EDGE_BIN}/launch-kube-router.sh
  install -m 0644 ${S}/../10-kuberouter.conflist ${D}${EDGE_CNI}/10-kuberouter.conflist
  install -m 0644 ${S}/../kube-router.service ${D}${systemd_system_unitdir}/kube-router.service
  install -m 0644 ${S}/../kube-router-watcher.service ${D}${systemd_system_unitdir}/kube-router-watcher.service
  install -m 0644 ${S}/../kube-router.path ${D}${systemd_system_unitdir}/kube-router.path
}
