SUMMARY = "panic module to intentionally crash the kernel"
LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=12f884d2ae1ff87c09e5b7ccc2c4ca7e"

inherit module

PR = "r1"
PV = "0.1"

SRC_URI = "file://Makefile \
file://panic.c \
file://COPYING \
"

S = "${WORKDIR}"
