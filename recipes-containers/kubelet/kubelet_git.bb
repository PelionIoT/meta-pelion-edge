DESCRIPTION = "Kubernetes without all the extra stuff"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://src/${GO_IMPORT}/LICENSE;md5=3b83ef96387f14655fc854ddc3c6bd57"

inherit go pkgconfig gitpkgv systemd
SRC_URI = "git://git@github.com/armPelionEdge/edge-kubelet.git;protocol=ssh;branch=master;depth=1 \
file://10-c2d.conf \
file://99-loopback.conf \
file://kubeconfig \
file://kubelet.service \
file://kubelet-watcher.service \
file://kubelet.path \
file://launch-edgenet.sh \
file://launch-kubelet.sh \
  "

SYSTEMD_PACKAGES = "${PN}"
SYSTEMD_SERVICE_${PN} = "kubelet.service"
SYSTEMD_AUTO_ENABLE_${PN} = "enable"

#SRCREV = "${AUTOREV}"
SRCREV = "83b266ae6939012883611d6dbda745f2490a67c4"
PR = "r1"

DEPENDS = "libseccomp bash-native"
RDEPENDS_${PN} += " docker libseccomp cni bash jq"

bindir = "/wigwag/system/bin"
confdir = "/wigwag/system/var/lib/kubelet"
cnidir = "/wigwag/system/etc/cni/net.d"
FILES_${PN} =  "\
    ${bindir}/kubelet\
    ${bindir}/launch-kubelet.sh\
    ${bindir}/launch-edgenet.sh\
    ${confdir}/kubeconfig\
    ${cnidir}/10-c2d.conf\
    ${cnidir}/99-loopback.conf\
    ${systemd_system_unitdir}/kubelet.service\
    ${systemd_system_unitdir}/kubelet-watcher.service\
    ${systemd_system_unitdir}/kubelet.path\
    "
GO_IMPORT = "k8s.io/kubernetes"
GO_PACKAGES = "${GO_IMPORT}/cmd/kubelet"

do_compile() {
  export TMPDIR="${GOTMPDIR}"
  # KUBE_GO_PACKAGE is expected to be set by the version.sh script
  export KUBE_GO_PACKAGE=${GO_IMPORT}
  echo "${GO} install -v -ldflags=\"$GO_RPATH $GO_LINKMODE -extldflags '$GO_EXTLDFLAGS' $(kube_version_ldflags)\" ${GO_PACKAGES}" > /tmp/gostuff
  ${GO} install -v -ldflags="$GO_RPATH $GO_LINKMODE -extldflags '$GO_EXTLDFLAGS' $(kube_version_ldflags)" ${GO_PACKAGES}
}

do_install() {
  install -d ${D}${bindir}
  install -d ${D}${confdir}
  install -d ${D}${cnidir}
  install -d ${D}${systemd_system_unitdir}
  install -m 0755 ${B}/${GO_BUILD_BINDIR}/kubelet ${D}${bindir}/kubelet
  install -m 0755 ${S}/../launch-kubelet.sh ${D}${bindir}/launch-kubelet.sh
  install -m 0755 ${S}/../launch-edgenet.sh ${D}${bindir}/launch-edgenet.sh
  install -m 0644 ${S}/../kubeconfig ${D}${confdir}/kubeconfig
  install -m 0644 ${S}/../10-c2d.conf ${D}${cnidir}/10-c2d.conf
  install -m 0644 ${S}/../99-loopback.conf ${D}${cnidir}/99-loopback.conf
  install -m 0644 ${S}/../kubelet.service ${D}${systemd_system_unitdir}/kubelet.service
  install -m 0644 ${S}/../kubelet-watcher.service ${D}${systemd_system_unitdir}/kubelet-watcher.service
  install -m 0644 ${S}/../kubelet.path ${D}${systemd_system_unitdir}/kubelet.path
}
