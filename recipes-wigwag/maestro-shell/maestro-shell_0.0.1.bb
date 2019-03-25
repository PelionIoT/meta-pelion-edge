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
FILES_${PN} += "/wigwag/system/bin/*" 


SRC_URI="git://git@github.com/WigWagCo/maestro-shell.git;protocol=ssh;branch=master"

S= "${WORKDIR}/git"

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
    export LD="${CXX}"
    # $TARGET_ARCH settings don't match --dest-cpu settings
    if [ "${TARGET_ARCH}" = "arm" ]; then
        CONFIG_OPTIONS="--host=arm  ${ARCHFLAGS}"
    elif [ "${TARGET_ARCH}" = "x86_64" ]; then
         CONFIG_OPTIONS="--host=x64  ${ARCHFLAGS}"
    else
          CONFIG_OPTIONS="--host=ia32  ${ARCHFLAGS}"
    fi
    export CONFIG_OPTIONS="${CONFIG_OPTIONS}"
    #cd ${S}

}

do_compile() {
  _logit "Where: `pwd`" 
  # you are here: /mnt/main2/ed/bigwork/wwrelay-rootfs/yocto/build/tmp/work/armv7a-vfp-neon-poky-linux-gnueabi/devicedb/0.3.2-r0/git
  # /builds/walt/42/wwrelay-rootfs/yocto/build/tmp/work/armv7a-vfp-neon-poky-linux-gnueabi/maestro/1.0+gitAUTOINC+ca07729f84-r0/git

  cd ..
  # the way this project works, with it's dependencies, creating this workspace directory is required.
  mkdir -p go-workspace/bin
  mkdir -p go-workspace/pkg
  mkdir -p go-workspace/src      
  WORKSPACE="`pwd`/go-workspace"
  export CGO_ENABLED=1
  export GOPATH="$WORKSPACE"
  export GOBIN="$WORKSPACE/bin"
  cd go-workspace
  mkdir -p src/github.com/WigWagCo
  

  mv "${S}" src/github.com/WigWagCo/maestro-shell
  cd src/github.com/WigWagCo/maestro-shell
  go build

}

do_install() {
     WORKSPACE=`pwd`/../go-workspace
     WBIN="/wigwag/system/bin"
     WLIB="/wigwag/system/lib"

     DWBIN="${D}/${WBIN}"
#     DWLIB="${D}/${WLIB}"
     install -d ${DWBIN}
#     install -d ${DWLIB}

     install -m 0755 "${WORKSPACE}/src/github.com/WigWagCo/maestro-shell/maestro-shell" "${D}/${WBIN}"
}

