DESCRIPTION = "maestro is a runtime / container manager for deviceOS"

LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://m/LICENSE;md5=4336ad26bb93846e47581adc44c4514d"


DEPENDS="deviceos-users"
RDEPENDS_${PN}+="bash twlib"
inherit go pkgconfig gitpkgv update-rc.d

INITSCRIPT_NAME = "maestro.sh"
INITSCRIPT_PARAMS = "defaults 85 15"


CGO_ENABLED = "1"

# when using a tag don't the following 3 varribles
PV = "1.0+git${SRCPV}"
PKGV = "1.0+git${GITPKGV}"
#SRCREV = "${AUTOREV}"
# and ending here.  Comment the above out if using tag

PR = "r0"

#FILES_${PN} = "/wigwag/* /etc/init.d /etc/init.d/* /etc/wigwag /etc/wigwag/* /etc/rc?.d/* /usr/bin /usr/bin/*"
FILES_${PN} += "/wigwag/system/bin/* /wigwag/system/lib/* ${INIT_D_DIR}/*"
#FILES_${PN} += "/wigwag/system/bin/* ${INIT_D_DIR}/*"

#INSANE_SKIP_${PN}+="installed-vs-shipped"

SRC_URI="git://git@github.com/WigWagCo/maestro.git;protocol=ssh;branch=master;name=m;destsuffix=git/m \
git://git@github.com/WigWagCo/rallypointwatchdogs.git;protocol=ssh;branch=master;name=wd;destsuffix=git/wd \
file://maestro.sh \
"
#git://git@github.com/WigWagCo/maestroSpecs.git;protocol=ssh;branch=master;name=specs;destsuffix=git/specs

SRCREV_FORMAT="m-wd"
SRCREV_m="${AUTOREV}"
#good
#SRCREV_m="4308e4684a5c0877f31f20736abbf132c9d82df0"
#bad
#SRCREV_m="8d6f945eb19b0611455b13332cc45976e471a8c9"
SRCREV_wd="${AUTOREV}"
#SRCREV_specs="${AUTOREV}"

S = "${WORKDIR}/git"

WSYS="/wigwag/system"
WSB="/wigwag/system/bin"
WSL="/wigwag/system/lib"

#BBCLASSEXTEND = "native"
#INHIBIT_PACKAGE_STRIP = "1"

LOG="/tmp/maestro_0.0.1.bb.log"

do_package_qa () {
  echo "done"
}

do_configure() {
         echo "Entered do_confgiure" > ${LOG}
    # you are in the TOP/build
    cd ../git
    TOP=`pwd`
    # TOP/git is
    # I am assuming we are in 'git' folder
#    echo "HERE: $TOP"
    echo "HEREHERE: $TOP"
#    ls



    S_WD="${TOP}/wd"
    S_M="${TOP}/m"
#    S_SPECS="${TOP}/specs"

    cd $S_M
    mkdir -p vendor/github.com/WigWagCo/greasego/deps/{lib,include,bin}
#    mkdir -p vendor/github.com/WigWagCo/greasego/deps/include
#    mkdir -p vendor/github.com/WigWagCo/greasego/deps/bin
    cd vendor/github.com/WigWagCo/greasego/deps/src/greaseLib/deps/

    # this crap is for greaseLib's deps
    export LD="${CXX}"
    # $TARGET_ARCH settings don't match --dest-cpu settings
    if [ "${TARGET_ARCH}" = "arm" ]; then
        CONFIG_OPTIONS="--host=arm ${ARCHFLAGS}"
    elif [ "${TARGET_ARCH}" = "x86_64" ]; then
         CONFIG_OPTIONS="--host=x64 ${ARCHFLAGS}"
    else
          CONFIG_OPTIONS="--host=ia32  ${ARCHFLAGS}"
    fi
    export CONFIG_OPTIONS="${CONFIG_OPTIONS}"
    #cd ${S}
    ./install-deps.sh
    echo "greaseLib deps built"

    cd ..
    echo "Building libgrease.a"
    rm -f *.o *.a
    # make the server version - which basically just bypasses checks for symbols
    # on the client logging code
    make libgrease.a-server
    make libgrease.so.1
    make grease_echo
    make standalone_test_logsink

    GREASEGO=$S_M/vendor/github.com/WigWagCo/greasego

    # Ed can't get can't get static compilation to work for Go + libgrease.a
    # See notes in greasego/bingings.go  - for now shared lib is fine.

    # if [ -e libgrease.a ]; then
    #   # migrate all of the greaselib dependencies up to the folder Go will use
    #   cp -r deps/build/lib/* $GREASEGO/deps/lib
    #   cp -r deps/build/include/* $GREASEGO/deps/include
    #   # move our binary into lib - static is all we use
    #   cp libgrease.a $GREASEGO/deps/lib
    #   cp *.h $GREASEGO/deps/include
    #   echo ">>>>>>>>> Success. libgrease.a ready."
    # else
    #   echo ">>>>>>>>> ERROR: libgrease.a missing or not built."
    # fi


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
  ln -s libgrease.so.1 libgrease.so
  echo ">>>>>>>>> Success. libgrease.so link ready."
    else
  echo ">>>>>>>>> ERROR: libgrease.so.1 missing or not built."
    fi


    # NOTE: MUST INSTALL libgrease.so.1 to Yocto


#    cd $TOP/vendor/github.com/WigWagCo/greasego
#
#    cd $S_M/vendor/github.com/WigWagCo/greasego/deps/src/greaseLib
#    make grease_echo
#    make standalone_test_logsink
    echo "greaseLib and utils built."

    cd $S_M/vendor/github.com/WigWagCo/greasego
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
  mkdir -p go-workspace/src/github.com/WigWagCo
  mv "${S_M}" go-workspace/src/github.com/WigWagCo/maestro
  mv go-workspace/src/github.com/WigWagCo/maestro/vendor/github.com/WigWagCo/maestroSpecs go-workspace/src/github.com/WigWagCo/maestroSpecs
  mv go-workspace/src/github.com/WigWagCo/maestro/vendor/github.com/WigWagCo/mustache go-workspace/src/github.com/WigWagCo/mustache
  rm -rf "${S_WD}/vendor/github.com/WigWagCo/maestroSpecs"
  mv "${S_WD}" go-workspace/src/github.com/WigWagCo/rallypointwatchdogs

}

do_compile() {
  echo "Where: `pwd`" > /tmp/out
  ls >> /tmp/out
  # you are here: /mnt/main2/ed/bigwork/wwrelay-rootfs/yocto/build/tmp/work/armv7a-vfp-neon-poky-linux-gnueabi/devicedb/0.3.2-r0/git
  # /builds/walt/42/wwrelay-rootfs/yocto/build/tmp/work/armv7a-vfp-neon-poky-linux-gnueabi/maestro/1.0+gitAUTOINC+ca07729f84-r0/git

  TOP=`pwd`

  S_WD="${TOP}/wd"
  S_M="${TOP}/m"
  S_SPECS="${TOP}/specs"

  cd ..
  # the way this project works, with it's dependencies, creating this workspace directory is required.
  # mkdir -p go-workspace/bin
  # mkdir -p go-workspace/pkg
  # mkdir -p go-workspace/src
  WORKSPACE="`pwd`/go-workspace"
  export CGO_ENABLED=1
  export GOPATH="$WORKSPACE"
  export GOBIN="$WORKSPACE/bin"
  cd go-workspace/src
#  mkdir -p github.com/WigWagCo


#  mv "${S_M}" github.com/WigWagCo/maestro
  # use only the separetly download maestroSpecs
  #rm -rf github.com/WigWagCo/maestro/vendor/github.com/WigWagCo/maestroSpecs
#  mv github.com/WigWagCo/maestro/vendor/github.com/WigWagCo/maestroSpecs github.com/WigWagCo/maestroSpecs
#  mv github.com/WigWagCo/maestro/vendor/github.com/WigWagCo/mustache github.com/WigWagCo/mustache
#  mv "${S_SPECS}" github.com/WigWagCo/maestroSpecs
  # use only the separetly download maestroSpecs
#  rm -rf "${S_WD}/vendor/github.com/WigWagCo/maestroSpecs"
#  mv "${S_WD}" github.com/WigWagCo/rallypointwatchdogs

  cd github.com/WigWagCo/maestro/vendor/github.com/WigWagCo/greasego
  echo "Building greasego..."
  make clean
  make bindings.a-debug
  #     make bindings.a
#  go build github.com/WigWagCo/greasego
#  go install github.com/WigWagCo/greasego

#  DEBUG=1 ./build.sh
#  cd "$WORKSPACE"/src/github.com/WigWagCo/maestro
  echo "Building maestro..."
  # when not doing a debug - get rid of the DEBUG vars
  # On 'thud': for some reason the GOARCH is using the host not the target
  # let's unf--- that
  export GOARCH=`echo $AR | awk -F '-' '{print $1}'`
  go env
  cd "$WORKSPACE"/bin
  go build -x github.com/WigWagCo/maestro/maestro
  cd "$WORKSPACE"/src/github.com/WigWagCo/rallypointwatchdogs
  # TODO - really we should only build what we need for the platform
  ./build.sh

#  DEBUG=1 DEBUG2=1 ./build.sh removesrc

# the devicedb source is in the devicejs/deps/devicedb/src/devicedb directory
  #rm -f devicedb; ln -s ../../git/deps/devicedb/src/devicedb devicedb
  #cd $WORKSPACE
  #go env
  #go build devicedb/cmd/devicedb
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
    # install -d -m 644 ${D}/wigwag/system/mud
    # chown -R deviceos:deviceos ${D}/wigwag/system/mud
    # echo "chown -R deviceos:deviceos ${D}/wigwag/system/mud" >> /tmp/dwlib
    # install -d -m 644 ${D}/wigwag/system/mud2
    # chown -R deviceos:deviceos ${D}/wigwag/system/mud2
    # echo "chown -R deviceos:deviceos ${D}/wigwag/system/mud2" >> /tmp/dwlib

   # #TRM---
   #  #chown -R deviceos:deviceos ${D}/wigwag/system/lib/
   # echo "    chown -R deviceos:deviceos ${D}/wigwag/system/lib" >> /tmp/dwlib
   # #TRM---


    # install -m 755 ${S}/GPIO/led.sh ${D}/wigwag/system/bin/led
    # install -m 755 ${S}/lib/path/somewhere/{lib,lib2,lib3.so} ${D}/wigwag/system/lib/



    WORKSPACE=`pwd`/../go-workspace

    install -m 0755 "${WORKSPACE}/src/github.com/WigWagCo/rallypointwatchdogs/rp100/rp100wd.so" "${D}/wigwag/system/lib"
    install -m 0755 "${WORKSPACE}/src/github.com/WigWagCo/rallypointwatchdogs/dummy/dummywd.so" "${D}/wigwag/system/lib"

    install -m 0755 "${WORKSPACE}/bin/maestro" "${D}/wigwag/system/bin"
    install -m 0755 "${WORKSPACE}/src/github.com/WigWagCo/maestro/vendor/github.com/WigWagCo/greasego/deps/src/greaseLib/grease_echo" "${D}/wigwag/system/bin"
    install -m 0755 "${WORKSPACE}/src/github.com/WigWagCo/maestro/vendor/github.com/WigWagCo/greasego/deps/src/greaseLib/standalone_test_logsink" "${D}/wigwag/system/bin"
    # install all libs needed by greasego


    #ALL_LIBS="libTW.a libprofiler.a   libre2.so        libstacktrace.a   libtcmalloc.la libtcmalloc_debug.a    libtcmalloc_minimal.la        libuv.a libgrease.so    libprofiler.la  libre2.so.0      libstacktrace.la  libtcmalloc_and_profiler.a   libtcmalloc_debug.la   libtcmalloc_minimal_debug.a libgrease.so.1  libre2.a        libre2.so.0.0.0  libtcmalloc.a     libtcmalloc_and_profiler.la  libtcmalloc_minimal.a  libtcmalloc_minimal_debug.la"



MAESTRO_LIBS="${WORKSPACE}/src/github.com/WigWagCo/maestro/vendor/github.com/WigWagCo/greasego/deps/lib"
ALL_LIBS="libTW.a libprofiler.a libstacktrace.a libtcmalloc.la libtcmalloc_debug.a libtcmalloc_minimal.la libuv.a libgrease.so libprofiler.la libstacktrace.la libtcmalloc_and_profiler.a libtcmalloc_debug.la libtcmalloc_minimal_debug.a libgrease.so.1 libtcmalloc.a libtcmalloc_and_profiler.la  libtcmalloc_minimal.a libtcmalloc_minimal_debug.la"
    for f in $ALL_LIBS; do
  install -m 0755 -o deviceos -g deviceos $MAESTRO_LIBS/$f ${D}/wigwag/system/lib
  #chmod 0755 ${D}/wigwag/system/lib/$f
  #chown -R deviceos:deviceos ${D}/wigwag/system/lib/$f
    done






  # install -m 0755 "libgrease.so.1" "${D}/${libdir}"
  # install -m 0755 "${WORKSPACE}"/src/github.com/WigWagCo/vendor/github.com/greasego/deps/lib/libre2.so* "${D}/${libdir}"
  # install -m 0755 "${WORKSPACE}"/src/github.com/WigWagCo/vendor/github.com/greasego/deps/lib/libtcmalloc.so* "${D}/${libdir}"
  # install -m 0755 "${WORKSPACE}"/src/github.com/WigWagCo/vendor/github.com/greasego/deps/lib/libtcmalloc_debug.so* "${D}/${libdir}"
  # install -m 0755 "${WORKSPACE}"/src/github.com/WigWagCo/vendor/github.com/greasego/deps/lib/libtcmalloc_minimal.so* "${D}/${libdir}"
  # install -m 0755 "${WORKSPACE}"/src/github.com/WigWagCo/vendor/github.com/greasego/deps/lib/libprofiler.so* "${D}/${libdir}"
}
