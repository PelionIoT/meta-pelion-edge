DESCRIPTION = "devicedb Go version"
LICENSE = "DEVICEJS-1"
LICENSE_FLAGS = "WigWagCommericalDeviceJS"
LIC_FILES_CHKSUM = "file://ddb/LICENSE;md5=4011f5b49f62dc7a25bef33807edc4bd"

inherit go pkgconfig gitpkgv


SRCREV = "${AUTOREV}"
#SRCREV="330c8ceaa82abff7a15f5da83be1f6b0daaf7cd6"
#PV = "1.0+git${SRCPV}"
#PKGV = "1.0+git${GITPKGV}"
PR = "r5"
# Why are we using the destsuffix= option in SRC_URI here? B/c qapprently in "thud", the golang bbclass is a bit naive and 
# can't deal with SRCREV=hash or tags. This is do to some incomplete "help" it provides in the URI fetcher
# you can see the half-working code here: https://github.com/openembedded/openembedded-core/blob/thud/meta/classes/go.bbclass
SRC_URI="git://git@github.com/WigWagCo/devicejs-ng.git;protocol=ssh;branch=development;name=ddb;destsuffix=git/ddb"
#SRC_URI="git://git@github.com/WigWagCo/devicejs-ng.git;protocol=ssh;branch=development"

SRCREV_FORMAT="ddb"

SRCREV_ddb="330c8ceaa82abff7a15f5da83be1f6b0daaf7cd6"

S = "${WORKDIR}/git"

FILES_${PN}="/wigwag/system/bin/* "

do_package_qa () {
  echo "done"
}


do_compile() {
  cd ..
  TOP=`pwd`
  WORKSPACE="`pwd`/go-workspace"
  mkdir -p go-workspace/bin
  mkdir -p go-workspace/pkg
  mkdir -p go-workspace/src      
  
  export GOPATH="$WORKSPACE"
  cd go-workspace/src
  rm -f devicedb; ln -s ../../git/ddb/deps/devicedb/src/devicedb devicedb   
  cd $WORKSPACE
  # Fix GOARCH - having to do this may be the result of our "fix" above for the fether not working.
  export GOARCH=`echo $AR | awk -F '-' '{print $1}'`
  go env
  go build devicedb
}

do_install() {
  WORKSPACE=`pwd`/../go-workspace

  #install -d "${D}/${bindir}"
  install -d "${D}/wigwag/system/bin"
  #install -m 0755 "${WORKSPACE}/devicedb" "${D}/${bindir}"
  install -m 0755 "${WORKSPACE}/devicedb" "${D}/wigwag/system/bin"
}

