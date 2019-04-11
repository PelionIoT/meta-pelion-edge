DESCRIPTION = "devicedb distributed database"
LICENSE = "Appache-2.0"
LIC_FILES_CHKSUM = "file://ddb/LICENSE;md5=1dece7821bf3fd70fe1309eaa37d52a2"

inherit go pkgconfig gitpkgv

PR = "r5"
# Why are we using the destsuffix= option in SRC_URI here? B/c qapprently in "thud", the golang bbclass is a bit naive and 
# can't deal with SRCREV=hash or tags. This is do to some incomplete "help" it provides in the URI fetcher
# you can see the half-working code here: https://github.com/openembedded/openembedded-core/blob/thud/meta/classes/go.bbclass
SRC_URI = "git://git@github.com/armPelionEdge/devicejs-ng.git;protocol=ssh;branch=master;name=ddb;destsuffix=git/ddb \
file://devicedb \
"

SRCREV_FORMAT = "ddb"
SRCREV_ddb = "${AUTOREV}"
S = "${WORKDIR}/git"

DEPENDS = ""
RDEPENDS_${PN} += " logrotate"

FILES_${PN} = "/wigwag/system/bin/* /etc/logrotate.d/*"

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
  install -d "${D}/wigwag/system/bin"
  install -m 0755 "${WORKSPACE}/devicedb" "${D}/wigwag/system/bin"
  install -d "${D}${sysconfdir}/logrotate.d/"
  install -m 644 "${WORKDIR}/devicedb" "${D}${sysconfdir}/logrotate.d"
}

