DESCRIPTION = "maetro is a runtime / container manager for deviceOS"

LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=4336ad26bb93846e47581adc44c4514d"

inherit go pkgconfig gitpkgv

PV = "1.0+git${SRCPV}"
PKGV = "1.0+git${GITPKGV}"
SRCREV = "${AUTOREV}"

PR = "r0"

#FILES_${PN} = "/wigwag/* /etc/init.d /etc/init.d/* /etc/wigwag /etc/wigwag/* /etc/rc?.d/* /usr/bin /usr/bin/*"
FILES_${PN} += "/wigwag/system/bin/*" 


SRC_URI="git://git@github.com/WigWagCo/maestro-shell.git;protocol=ssh;branch=master"
S= "${WORKDIR}/git"
WSB="/wigwag/system/bin"

DEPENDS +=" maestro"

do_package_qa () {
  echo "done"
}


do_configure() {
  export LD="${CXX}"
  if [ "${TARGET_ARCH}" = "arm" ]; then
    CONFIG_OPTIONS="--host=arm  ${ARCHFLAGS}"
  elif [ "${TARGET_ARCH}" = "x86_64" ]; then
   CONFIG_OPTIONS="--host=x64  ${ARCHFLAGS}"
 else
  CONFIG_OPTIONS="--host=ia32  ${ARCHFLAGS}"
fi
export CONFIG_OPTIONS="${CONFIG_OPTIONS}"

}

do_compile() {
  cd ..
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
 install -d ${DWBIN}
 install -m 0755 "${WORKSPACE}/src/github.com/WigWagCo/maestro-shell/maestro-shell" "${D}/${WBIN}"
}

