DESCRIPTION = "maestro is a runtime / container manager for deviceOS"

LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://src/${GO_IMPORT}/LICENSE;md5=1dece7821bf3fd70fe1309eaa37d52a2"


DEPENDS="deviceos-users"
RDEPENDS_${PN}+="bash twlib"
inherit go pkgconfig gitpkgv update-rc.d systemd

INITSCRIPT_NAME = "maestro.sh"
INITSCRIPT_PARAMS = "defaults 85 15"

SYSTEMD_PACKAGES = "${PN}"
SYSTEMD_SERVICE_${PN} = "maestro.service"
SYSTEMD_AUTO_ENABLE_${PN} = "enable"

CGO_ENABLED = "1"

PV = "1.0+git${SRCPV}"
PKGV = "1.0+git${GITPKGV}"

PR = "r0"

FILES_${PN} += "\
    /wigwag/system/bin/*\
    /wigwag/system/lib/*\
    ${INIT_D_DIR}/*\
    ${systemd_system_unitdir}/maestro.service\
    "

SRC_URI="git://git@github.com/armPelionEdge/maestro.git;protocol=ssh;branch=master;name=m \
file://maestro.sh \
file://maestro.service \
"

SRCREV_FORMAT="m"
SRCREV_m="d3699da8293250a42502927a9f4fc82df8077b66"

GO_IMPORT = "github.com/armPelionEdge/maestro"

GREASEGO = "vendor/github.com/armPelionEdge/greasego"
GREASE_SRC = "src/${GO_IMPORT}/${GREASEGO}"
CGREASE_SRC = "${GREASE_SRC}/deps/src/greaseLib"

do_configure_append() {
  # build dependencies of greaseGo
  pushd ${GREASE_SRC}/deps/src/greaseLib/deps/
    export LD="${CXX}"
    if [ "${TARGET_ARCH}" = "arm" ]; then
      CONFIG_OPTIONS="--host=arm ${ARCHFLAGS}"
    elif [ "${TARGET_ARCH}" = "x86_64" ]; then
      CONFIG_OPTIONS="--host=x64 ${ARCHFLAGS}"
    else
      CONFIG_OPTIONS="--host=ia32 ${ARCHFLAGS}"
    fi
    export CONFIG_OPTIONS="${CONFIG_OPTIONS}"
    ./install-deps.sh
  popd

  # build greaseGo
  pushd ${CGREASE_SRC}
    rm -f *.o *.a
    make libgrease.a-server
    make libgrease.so.1
    make grease_echo
    make standalone_test_logsink
  popd

  mkdir -p ${GREASE_SRC}/deps/{lib,include}

  # migrate all of the greaseGo dependencies up to the folder Go will use
  cp -r ${CGREASE_SRC}/deps/build/lib/* ${GREASE_SRC}/deps/lib
  cp -r ${CGREASE_SRC}/deps/build/include/* ${GREASE_SRC}/deps/include
  cp ${CGREASE_SRC}/deps/libuv-v1.10.1/include/uv* ${GREASE_SRC}/deps/include
  cp ${CGREASE_SRC}/libgrease.so.1 ${GREASE_SRC}/deps/lib
  cp ${CGREASE_SRC}/*.h ${GREASE_SRC}/deps/include
  pushd ${GREASE_SRC}/deps/lib
    ln -sf libgrease.so.1 libgrease.so
  popd

  # Build greaseGo bindings
  pushd ${GREASE_SRC}
    DEBUG=1 ./build.sh preprocess_only
    rm -rf src
    make bindings.a
  popd

  # Put version information into 2 go files
  pushd src/${GO_IMPORT}
    ./build.sh preprocess_only
  popd
}

do_compile() {
	export TMPDIR="${GOTMPDIR}"
  ${GO} install ${GOBUILDFLAGS} ${GO_IMPORT}/maestro
}

WSB="wigwag/system/bin"
WSL="wigwag/system/lib"

do_install() {
  install -d ${D}/${WSB}
  install -d ${D}/${WSL}
  install -d ${D}/${INIT_D_DIR}
  install -d ${D}${systemd_system_unitdir}
  install -m 0755 ${WORKDIR}/maestro.sh ${D}${INIT_D_DIR}/maestro.sh
  install -m 0755 "${B}/${GO_BUILD_BINDIR}/maestro" "${D}/${WSB}"
  install -m 0755 "${B}/${CGREASE_SRC}/grease_echo" "${D}/${WSB}"
  install -m 0755 "${B}/${CGREASE_SRC}/standalone_test_logsink" "${D}/${WSB}"
  install -m 0755 -o deviceos -g deviceos ${B}/${GREASE_SRC}/deps/lib/libgrease.so ${D}/${WSL}
  install -m 0755 -o deviceos -g deviceos ${B}/${GREASE_SRC}/deps/lib/libgrease.so.1 ${D}/${WSL}
  install -m 0644 ${WORKDIR}/maestro.service ${D}${systemd_system_unitdir}/maestro.service
}
