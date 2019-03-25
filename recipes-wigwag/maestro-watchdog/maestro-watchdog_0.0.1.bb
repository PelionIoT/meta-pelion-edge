DESCRIPTION = "maetro is a runtime / container manager for deviceOS"

LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=4336ad26bb93846e47581adc44c4514d"

inherit go pkgconfig gitpkgv

# when using a tag don't the following 3 varribles
PV = "1.0+git${SRCPV}"
PKGV = "1.0+git${GITPKGV}"
SRCREV = "${AUTOREV}"
# and ending here.  Comment the above out if using tag

PR = "r0"

#FILES_${PN} = "/wigwag/* /etc/init.d /etc/init.d/* /etc/wigwag /etc/wigwag/* /etc/rc?.d/* /usr/bin /usr/bin/*"
FILES_${PN} += "/wigwag/system/bin/* /wigwag/system/lib/*" 

SRC_URI="git://git@github.com/WigWagCo/rallypointwatchdogs.git;protocol=ssh;branch=master"

S = "${WORKDIR}/git"

WSB="/wigwag/system/bin"

#BBCLASSEXTEND = "native"
#INHIBIT_PACKAGE_STRIP = "1"  
DEPENDS +=" maestro"

do_package_qa () {
  echo "done"
}

_logit(){
	echo "$1" >> "/tmp/maestro-watchdog.log"
}

do_configure() {
	echo "a new build" > "/tmp/maestro-watchdog.log"
	_logit "$GOPATH"
    # TOP=`pwd`
    # # I am assuming we are in 'git' folder	
    # cd vendor/github.com/WigWagCo/greasego/deps/src/greaseLib/deps/
	      
    # this crap is for greaseLib's deps
    #export LD="${CXX}"
    # $TARGET_ARCH settings don't match --dest-cpu settings
    # if [ "${TARGET_ARCH}" = "arm" ]; then
    #     CONFIG_OPTIONS="--host=arm  ${ARCHFLAGS}"
    # elif [ "${TARGET_ARCH}" = "x86_64" ]; then
    #      CONFIG_OPTIONS="--host=x64  ${ARCHFLAGS}"
    # else
    #       CONFIG_OPTIONS="--host=ia32  ${ARCHFLAGS}"
    # fi
    # export CONFIG_OPTIONS="${CONFIG_OPTIONS}"

    #cd ${S}
#     ./install-deps.sh
#     echo "greaseLib deps built"

#     cd ..
#     echo "Building libgrease.a"
#     rm -f *.o *.a
#     # make the server version - which basically just bypasses checks for symbols
#     # on the client logging code
#     make libgrease.a-server
#     make libgrease.so.1
    
#     GREASEGO=$TOP/vendor/github.com/WigWagCo/greasego

#     # Ed can't get can't get static compilation to work for Go + libgrease.a
#     # See notes in greasego/bingings.go  - for now shared lib is fine.

#     # if [ -e libgrease.a ]; then
#     # 	# migrate all of the greaselib dependencies up to the folder Go will use
#     # 	cp -r deps/build/lib/* $GREASEGO/deps/lib
#     # 	cp -r deps/build/include/* $GREASEGO/deps/include
#     # 	# move our binary into lib - static is all we use
#     # 	cp libgrease.a $GREASEGO/deps/lib
#     # 	cp *.h $GREASEGO/deps/include
#     # 	echo ">>>>>>>>> Success. libgrease.a ready."
#     # else
#     # 	echo ">>>>>>>>> ERROR: libgrease.a missing or not built."
#     # fi


#     if [ -e libgrease.so.1 ]; then
# 	# migrate all of the greaselib dependencies up to the folder Go will use
# 	cp -r deps/build/lib/* $GREASEGO/deps/lib
# 	cp -r deps/build/include/* $GREASEGO/deps/include
# 	cp $GREASEGO/deps/src/greaseLib/deps/libuv-v1.10.1/include/uv* $GREASEGO/deps/include
# 	# move our binary into lib - static is all we use
# 	cp libgrease.so.1 $GREASEGO/deps/lib
# 	cp *.h $GREASEGO/deps/include
# 	echo ">>>>>>>>> Success. libgrease.so.1 ready."
# 	cd $GREASEGO/deps/lib
# 	ln -s libgrease.so.1 libgrease.so
# 	echo ">>>>>>>>> Success. libgrease.so link ready."
#     else
# 	echo ">>>>>>>>> ERROR: libgrease.so.1 missing or not built."
#     fi


#     # NOTE: MUST INSTALL libgrease.so.1 to Yocto


# #    cd $TOP/vendor/github.com/WigWagCo/greasego
# # 
#     cd $TOP/vendor/github.com/WigWagCo/greasego/deps/src/greaseLib
#     make grease_echo
#     make standalone_test_logsink
#     echo "greaseLib and utils built."

#     cd $TOP/vendor/github.com/WigWagCo/greasego
#     DEBUG=1 ./build.sh preprocess_only
#     rm -rf src

#     cd $TOP
#     DEBUG=1 DEBUG2=1 ./build.sh preprocess_only
#     rm -rf src

#     # wipe out the src directories, seems to cause confusion with Go compiler in 
#     # Yocto build
}

do_compile() {
  _logit "Where: `pwd`" 
  # you are here: /mnt/main2/ed/bigwork/wwrelay-rootfs/yocto/build/tmp/work/armv7a-vfp-neon-poky-linux-gnueabi/devicedb/0.3.2-r0/git
  # /builds/walt/42/wwrelay-rootfs/yocto/build/tmp/work/armv7a-vfp-neon-poky-linux-gnueabi/maestro/1.0+gitAUTOINC+ca07729f84-r0/git
   cd ..
   TOP=`pwd`
# #   # the way this project works, with it's dependencies, creating this workspace directory is required.
   mkdir -p go-workspace/bin
   mkdir -p go-workspace/pkg
   mkdir -p go-workspace/src      
   WORKSPACE="`pwd`/go-workspace"
   export CGO_ENABLED=1
   export GOPATH="$WORKSPACE"
   export GOBIN="$WORKSPACE/bin"
   cd go-workspace/src
   mkdir -p github.com/WigWagCo
   mv $TOP/git github.com/WigWagCo/rallypointwatchdogs
   cd github.com/WigWagCo/rallypointwatchdogs
   ./build.sh

#   cd github.com/WigWagCo/maestro/vendor/github.com/WigWagCo/greasego
#   echo "Building greasego..."
#   make clean
#   make bindings.a-debug
#   #     make bindings.a
# #  go build github.com/WigWagCo/greasego
# #  go install github.com/WigWagCo/greasego

# #  DEBUG=1 ./build.sh
# #  cd "$WORKSPACE"/src/github.com/WigWagCo/maestro
#   echo "Building maestro..."
#   # when not doing a debug - get rid of the DEBUG vars
#   go env
#   cd "$WORKSPACE"/bin
#   go build -x github.com/WigWagCo/maestro/maestro

# #  DEBUG=1 DEBUG2=1 ./build.sh removesrc

# # the devicedb source is in the devicejs/deps/devicedb/src/devicedb directory
#   #rm -f devicedb; ln -s ../../git/deps/devicedb/src/devicedb devicedb   
#   #cd $WORKSPACE
#   #go env
#   #go build devicedb/cmd/devicedb
}

do_install() {
#     WBIN="/wigwag/system/bin"
     WLIB="/wigwag/system/lib"

     DWLIB="${D}/${WLIB}"
#     install -d ${DWBIN}
     install -d ${DWLIB}
#     # install -m 755 ${S}/GPIO/led.sh ${DWBIN}/led
#     # install -m 755 ${S}/lib/path/somewhere/{lib,lib2,lib3.so} ${DWLIB}/
    
     WORKSPACE=`pwd`/../go-workspace
#     #  echo "Where do_install: `pwd` - $WORKSPACE"
#     # install -d "${D}/${bindir}"
#     # install -d "${D}${WSB}"
#     install -m 0755 "${WORKSPACE}/bin/maestro" "${D}/${WBIN}"
     install -m 0755 "${WORKSPACE}/src/github.com/WigWagCo/rallypointwatchdogs/rp100/rp100wd.so" "${D}/${WLIB}"    
     install -m 0755 "${WORKSPACE}/src/github.com/WigWagCo/rallypointwatchdogs/dummy/dummywd.so" "${D}/${WLIB}"    
#     # install all libs needed by greasego
    
#     MAESTRO_LIBS="${WORKSPACE}/src/github.com/WigWagCo/maestro/vendor/github.com/WigWagCo/greasego/deps/lib"
    
#     #ALL_LIBS="libTW.a libprofiler.a   libre2.so        libstacktrace.a   libtcmalloc.la libtcmalloc_debug.a    libtcmalloc_minimal.la        libuv.a libgrease.so    libprofiler.la  libre2.so.0      libstacktrace.la  libtcmalloc_and_profiler.a   libtcmalloc_debug.la   libtcmalloc_minimal_debug.a libgrease.so.1  libre2.a        libre2.so.0.0.0  libtcmalloc.a     libtcmalloc_and_profiler.la  libtcmalloc_minimal.a  libtcmalloc_minimal_debug.la"

# ALL_LIBS="libTW.a libprofiler.a libstacktrace.a libtcmalloc.la libtcmalloc_debug.a libtcmalloc_minimal.la libuv.a libgrease.so libprofiler.la libstacktrace.la libtcmalloc_and_profiler.a libtcmalloc_debug.la libtcmalloc_minimal_debug.a libgrease.so.1 libtcmalloc.a libtcmalloc_and_profiler.la  libtcmalloc_minimal.a libtcmalloc_minimal_debug.la"
    
#     for f in $ALL_LIBS; do 
# 	cp $MAESTRO_LIBS/$f ${D}/${WLIB}
#     done
    
  # install -m 0755 "libgrease.so.1" "${D}/${libdir}"
  # install -m 0755 "${WORKSPACE}"/src/github.com/WigWagCo/vendor/github.com/greasego/deps/lib/libre2.so* "${D}/${libdir}"  
  # install -m 0755 "${WORKSPACE}"/src/github.com/WigWagCo/vendor/github.com/greasego/deps/lib/libtcmalloc.so* "${D}/${libdir}"  
  # install -m 0755 "${WORKSPACE}"/src/github.com/WigWagCo/vendor/github.com/greasego/deps/lib/libtcmalloc_debug.so* "${D}/${libdir}"  
  # install -m 0755 "${WORKSPACE}"/src/github.com/WigWagCo/vendor/github.com/greasego/deps/lib/libtcmalloc_minimal.so* "${D}/${libdir}"  
  # install -m 0755 "${WORKSPACE}"/src/github.com/WigWagCo/vendor/github.com/greasego/deps/lib/libprofiler.so* "${D}/${libdir}"  
}

