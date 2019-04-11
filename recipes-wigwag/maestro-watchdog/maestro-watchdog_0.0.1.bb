DESCRIPTION = "maetro-watchdog for ralypoint hw"

LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=4336ad26bb93846e47581adc44c4514d"

inherit go pkgconfig gitpkgv


PV = "1.0+git${SRCPV}"
PKGV = "1.0+git${GITPKGV}"
SRCREV = "${AUTOREV}"

PR = "r0"
FILES_${PN} += "/wigwag/system/bin/* /wigwag/system/lib/*" 
SRC_URI="git://git@github.com/armPelionEdge/rallypointwatchdogs.git;protocol=ssh;branch=master"
S = "${WORKDIR}/git"
WSB="/wigwag/system/bin"

DEPENDS +=" maestro"

do_package_qa () {
  echo "done"
}

do_configure() {
	echo "a new build" > "/tmp/maestro-watchdog.log"
	_logit "$GOPATH"
}

do_compile() {
 cd ..
 TOP=`pwd`
 mkdir -p go-workspace/bin
 mkdir -p go-workspace/pkg
 mkdir -p go-workspace/src      
 WORKSPACE="`pwd`/go-workspace"
 export CGO_ENABLED=1
 export GOPATH="$WORKSPACE"
 export GOBIN="$WORKSPACE/bin"
 cd go-workspace/src
 mkdir -p github.com/armPelionEdge
 mv $TOP/git github.com/armPelionEdge/rallypointwatchdogs
 cd github.com/armPelionEdge/rallypointwatchdogs
 ./build.sh
}

do_install() {
 WLIB="/wigwag/system/lib"
 DWLIB="${D}/${WLIB}"
 install -d ${DWLIB}
 WORKSPACE=`pwd`/../go-workspace
 install -m 0755 "${WORKSPACE}/src/github.com/armPelionEdge/rallypointwatchdogs/rp100/rp100wd.so" "${D}/${WLIB}"    
 install -m 0755 "${WORKSPACE}/src/github.com/armPelionEdge/rallypointwatchdogs/dummy/dummywd.so" "${D}/${WLIB}"    
}

