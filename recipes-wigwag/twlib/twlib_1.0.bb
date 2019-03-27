DESCRIPTION = "Tupperware container and utility library"

LICENSE = "GPLv2+"
LIC_FILES_CHKSUM = "file://${WORKDIR}/git/licenses.txt;md5=b3eefb4d5edda4b40fc7c92d4ece034b"

inherit pkgconfig gitpkgv


PV = "1.0+git${SRCPV}"
PKGV = "1.0+git${GITPKGV}"
PR = "r1"

SRCREV = "${AUTOREV}"
SRC_URI = "git://github.com/WigWagCo/twlib;protocol=git;branch=master"

DEBUG_OPTIONS = "-rdynamic -D_TW_TASK_DEBUG_THREADS_"
GLIBCFLAG = "-D_USING_GLIBC_"
CFLAGS = "${DEBUG_OPTIONS} ${GLIBCFLAG} -D_TW_DEBUG -I./include  -D__DEBUG"

S = "${WORKDIR}/git"

FILES_lib = "${D}${libdir}/*"
FILES_inc = "${D}${includedir}/TW/*"

do_compile_prepend () {
    cd ${WORKDIR}/git/deps
    cd sparsehash
    ./configure --host=x86 --prefix=${WORKDIR}/git/deps
    make
    make install
    cd ..
    cd googletest
    ./configure --host=x86 --prefix=${WORKDIR}/git/deps
    make
    make install
}

do_compile() {
    cd ${WORKDIR}/git
    make tw_lib
}
do_install() {
    cd ${WORKDIR}/git
    install  libTW.so.1.0.1 ${WORKDIR}/git/deps/lib
    install  -d ${D}${libdir}
    install  libTW.so.1.0.1 ${D}${libdir}
    install  libTW.so.1  ${D}${libdir}
    install  libTW.a  ${D}${libdir}
    install -d ${D}${includedir} 
    install -d ${D}${includedir}/TW
    install include/TW/* ${D}${includedir}/TW
    #also install the sparsehash headers
    install -d ${D}${includedir}/google
    cp -r deps/include/google/* ${D}${includedir}/google
    pushd ${WORKDIR}/git/deps/lib
    if [ ! -e libTW.so ] ; then
        ln -s libTW.so.1.0.1 libTW.so
    fi
    if [ ! -e libTW.so.1 ] ; then
     ln -s libTW.so.1.0.1 libTW.so.1
 fi
}

