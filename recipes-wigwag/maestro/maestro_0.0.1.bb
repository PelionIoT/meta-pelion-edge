DESCRIPTION = "maestro is a runtime / container manager for deviceOS"

LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://m/LICENSE;md5=1dece7821bf3fd70fe1309eaa37d52a2"


DEPENDS="deviceos-users"
RDEPENDS_${PN}+="bash twlib"
inherit go pkgconfig gitpkgv update-rc.d

INITSCRIPT_NAME = "maestro.sh"
INITSCRIPT_PARAMS = "defaults 85 15"


CGO_ENABLED = "1"

PV = "1.0+git${SRCPV}"
PKGV = "1.0+git${GITPKGV}"


PR = "r0"

FILES_${PN} += "/wigwag/system/bin/* /wigwag/system/lib/* ${INIT_D_DIR}/*"

SRC_URI="git://git@github.com/armPelionEdge/maestro.git;protocol=ssh;branch=master;name=m;destsuffix=git/m \
git://git@github.com/armPelionEdge/rallypointwatchdogs.git;protocol=ssh;branch=master;name=wd;destsuffix=git/wd \
file://maestro.sh \
"

SRCREV_FORMAT="m-wd"
SRCREV_m="${AUTOREV}"
SRCREV_wd="${AUTOREV}"

S = "${WORKDIR}/git"

WSYS="/wigwag/system"
WSB="/wigwag/system/bin"
WSL="/wigwag/system/lib"


LOG="/tmp/maestro_0.0.1.bb.log"

do_package_qa () {
  echo "done"
}

do_configure() {
    cd ../git
    TOP=`pwd`
    S_WD="${TOP}/wd"
    S_M="${TOP}/m"

    cd $S_M
    mkdir -p vendor/github.com/armPelionEdge/greasego/deps/{lib,include,bin}
    cd vendor/github.com/armPelionEdge/greasego/deps/src/greaseLib/deps/

    export LD="${CXX}"
    if [ "${TARGET_ARCH}" = "arm" ]; then
        CONFIG_OPTIONS="--host=arm ${ARCHFLAGS}"
    elif [ "${TARGET_ARCH}" = "x86_64" ]; then
         CONFIG_OPTIONS="--host=x64 ${ARCHFLAGS}"
    else
          CONFIG_OPTIONS="--host=ia32  ${ARCHFLAGS}"
    fi
    export CONFIG_OPTIONS="${CONFIG_OPTIONS}"
    ./install-deps.sh
    echo "greaseLib deps built"

    cd ..
    echo "Building libgrease.a"
    rm -f *.o *.a
    make libgrease.a-server
    make libgrease.so.1
    make grease_echo
    make standalone_test_logsink

    GREASEGO=$S_M/vendor/github.com/armPelionEdge/greasego


  if [ -e libgrease.so.1 ]; then
  # migrate all of the greaselib dependencies up to the folder Go will use
  cp -r deps/build/lib/* $GREASEGO/deps/lib
  cp -r deps/build/include/* $GREASEGO/deps/include
  cp $GREASEGO/deps/src/greaseLib/deps/libuv-v1.10.1/include/uv* $GREASEGO/deps/include
  # move our binary into lib - static is all we use
  cp libgrease.so.1 $GREASEGO/deps/lib
  cp *.h $GREASEGO/deps/include
  echo ">>>>>>>>> Success. libgrease.so.1 ready."
  cd $GREASEGO/deps/lib
  if [ ! -e libgrease.so ]; then
    ln -s libgrease.so.1 libgrease.so
  fi
  echo ">>>>>>>>> Success. libgrease.so link ready."
    else
  echo ">>>>>>>>> ERROR: libgrease.so.1 missing or not built."
    fi


    # NOTE: MUST INSTALL libgrease.so.1 to Yocto

    cd $S_M/vendor/github.com/armPelionEdge/greasego
    DEBUG=1 ./build.sh preprocess_only
    rm -rf src

    # remove the /vendor/maestroSpecs dir, b/c we want this to use the same folder
    # as the plugins (watchdog, etc.)
    cd $S_M
    DEBUG=1 DEBUG2=1 ./build.sh preprocess_only
    # wipe out the src directories, seems to cause confusion with Go compiler in
    rm -rf src

    # Yocto build

  cd ../..
  mkdir -p go-workspace/bin
  mkdir -p go-workspace/pkg
  mkdir -p go-workspace/src
  mkdir -p go-workspace/src/github.com/armPelionEdge  
  mv "${S_M}" go-workspace/src/github.com/armPelionEdge/maestro
  mv go-workspace/src/github.com/armPelionEdge/maestro/vendor/github.com/armPelionEdge/maestroSpecs go-workspace/src/github.com/armPelionEdge/maestroSpecs
  mv go-workspace/src/github.com/armPelionEdge/maestro/vendor/github.com/armPelionEdge/mustache go-workspace/src/github.com/armPelionEdge/mustache
  rm -rf "${S_WD}/vendor/github.com/armPelionEdge/maestroSpecs"
  mv "${S_WD}" go-workspace/src/github.com/armPelionEdge/rallypointwatchdogs
}

do_compile() {
  TOP=`pwd`
  S_WD="${TOP}/wd"
  S_M="${TOP}/m"
  S_SPECS="${TOP}/specs"
  cd ..
  WORKSPACE="`pwd`/go-workspace"
  export CGO_ENABLED=1
  export GOPATH="$WORKSPACE"
  export GOBIN="$WORKSPACE/bin"
  cd go-workspace/src
  cd github.com/armPelionEdge/maestro/vendor/github.com/armPelionEdge/greasego
  make clean
  make bindings.a-debug
  # when not doing a debug - get rid of the DEBUG vars
  # On 'thud': for some reason the GOARCH is using the host not the target
  export GOARCH=`echo $AR | awk -F '-' '{print $1}'`
  go env
  cd "$WORKSPACE"/bin
  go build -x github.com/armPelionEdge/maestro/maestro
  cd "$WORKSPACE"/src/github.com/armPelionEdge/rallypointwatchdogs
  # TODO -  only build what we need for the platform
  ./build.sh
}

do_install() {
    echo ${D}/wigwag/system/lib > /tmp/dwlib
    echo ${D}/wigwag/system/bin >> /tmp/dwlib
    install -d ${D}/wigwag/
    install -d ${D}/wigwag/system
    install -d ${D}/wigwag/system/bin
    install -d ${D}/wigwag/system/lib
    install -d ${D}${INIT_D_DIR}
    install -m 0755 ${S}/../maestro.sh ${D}${INIT_D_DIR}/maestro.sh
    WORKSPACE=`pwd`/../go-workspace
    install -m 0755 "${WORKSPACE}/src/github.com/armPelionEdge/rallypointwatchdogs/rp100/rp100wd.so" "${D}/wigwag/system/lib"
    install -m 0755 "${WORKSPACE}/src/github.com/armPelionEdge/rallypointwatchdogs/dummy/dummywd.so" "${D}/wigwag/system/lib"
    install -m 0755 "${WORKSPACE}/bin/maestro" "${D}/wigwag/system/bin"
    install -m 0755 "${WORKSPACE}/src/github.com/armPelionEdge/maestro/vendor/github.com/armPelionEdge/greasego/deps/src/greaseLib/grease_echo" "${D}/wigwag/system/bin"
    install -m 0755 "${WORKSPACE}/src/github.com/armPelionEdge/maestro/vendor/github.com/armPelionEdge/greasego/deps/src/greaseLib/standalone_test_logsink" "${D}/wigwag/system/bin"
    # install all libs needed by greasego
    MAESTRO_LIBS="${WORKSPACE}/src/github.com/armPelionEdge/maestro/vendor/github.com/armPelionEdge/greasego/deps/lib"
    ALL_LIBS="libTW.a libprofiler.a libstacktrace.a libtcmalloc.la libtcmalloc_debug.a libtcmalloc_minimal.la libuv.a libgrease.so libprofiler.la libstacktrace.la libtcmalloc_and_profiler.a libtcmalloc_debug.la libtcmalloc_minimal_debug.a libgrease.so.1 libtcmalloc.a libtcmalloc_and_profiler.la  libtcmalloc_minimal.a libtcmalloc_minimal_debug.la"
    for f in $ALL_LIBS; do
      install -m 0755 -o deviceos -g deviceos $MAESTRO_LIBS/$f ${D}/wigwag/system/lib
    done
}
