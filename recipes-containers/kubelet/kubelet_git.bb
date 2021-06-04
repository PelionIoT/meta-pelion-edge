DESCRIPTION = "Kubernetes without all the extra stuff"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://src/${GO_IMPORT}/LICENSE;md5=3b83ef96387f14655fc854ddc3c6bd57"

inherit go pkgconfig gitpkgv systemd edge
SRC_URI = "git://git@github.com/armPelionEdge/edge-kubelet.git;protocol=https;branch=kube-router-support;depth=1 \
file://kubeconfig \
file://kubelet.service \
file://kubelet-watcher.service \
file://kubelet.path \
file://launch-kubelet.sh \
  "

SYSTEMD_PACKAGES = "${PN}"
SYSTEMD_SERVICE_${PN} = "kubelet.service"
SYSTEMD_AUTO_ENABLE_${PN} = "enable"

#SRCREV = "${AUTOREV}"
SRCREV = "f8ebb1afd620f6a29691a21656a1bc3b54283906"
PR = "r1"

DEPENDS = "libseccomp"
RDEPENDS_${PN} += " docker libseccomp cni bash jq"


FILES_${PN} =  "\
    ${EDGE_BIN}/kubelet\
    ${EDGE_BIN}/launch-kubelet.sh\
    ${EDGE_KUBELET_STATE}/kubeconfig\
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
  #note: reference 2.1 Tag for the bash only method to add version information
    
  #bash and dash version:
  timestamp="$(date ${buildDate} -u +'%Y-%m-%dT%H:%M:%SZ')"
  ldflagsstring="-X 'k8s.io/kubernetes/pkg/version.buildDate=${timestamp}' -X 'k8s.io/kubernetes/vendor/k8s.io/client-go/pkg/version.buildDate=${timestamp}' -X 'k8s.io/kubernetes/pkg/version.gitVersion=v1.13.2-argus' -X 'k8s.io/kubernetes/vendor/k8s.io/client-go/pkg/version.gitVersion=v1.13.2-argus' -X 'k8s.io/kubernetes/pkg/version.gitMajor=1' -X 'k8s.io/kubernetes/vendor/k8s.io/client-go/pkg/version.gitMajor=1' -X 'k8s.io/kubernetes/pkg/version.gitMinor=13' -X 'k8s.io/kubernetes/vendor/k8s.io/client-go/pkg/version.gitMinor=13' "
  ${GO} install -v -ldflags="$GO_RPATH $GO_LINKMODE -extldflags '$GO_EXTLDFLAGS' ${ldflagsstring}" ${GO_PACKAGES}
  cd ${S}/../
  edge_replace_vars launch-kubelet.sh kubelet.path kubelet.service
}

do_install() {
  install -d ${D}${EDGE_BIN}
  install -d ${D}${EDGE_KUBELET_STATE}
  install -d ${D}${systemd_system_unitdir}
  install -m 0755 ${B}/${GO_BUILD_BINDIR}/kubelet ${D}${EDGE_BIN}/kubelet
  install -m 0755 ${S}/../launch-kubelet.sh ${D}${EDGE_BIN}/launch-kubelet.sh
  install -m 0644 ${S}/../kubeconfig ${D}${EDGE_KUBELET_STATE}/kubeconfig
  install -m 0644 ${S}/../kubelet.service ${D}${systemd_system_unitdir}/kubelet.service
  install -m 0644 ${S}/../kubelet-watcher.service ${D}${systemd_system_unitdir}/kubelet-watcher.service
  install -m 0644 ${S}/../kubelet.path ${D}${systemd_system_unitdir}/kubelet.path
}
