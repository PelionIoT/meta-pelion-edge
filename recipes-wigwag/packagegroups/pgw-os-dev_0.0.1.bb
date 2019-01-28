
# Copyright (C) 2007 OpenedHand Ltd.
# Copyright (C) 2012 Red Hat, Inc.
#

SUMMARY = "WigWag build dependencies"
LICENSE = "MIT"
PR = "r0"

inherit packagegroup

#include recipes-core/packagegroups/packagegroup-core-buildessential
#include recipes-core/packagegroups/packagegroup-core-buildessential.bb

RDEPENDS_packagegroup-wigwag-image-utils = "dosfstools e2fsprogs emacs "
#isc-dhclient gnutls-openssl hostap-conf hostap-utils iputils iputils-ping iputils-ping6 iputils-tracepath6 iputils-traceroute6 lsof nmap parted rsync screen socat strace sunxi-tools twlib update-rc.d usbutils util-linux-agetty util-linux-bash-completion util-linux-uuidd wget-locale-zh-cn wget-locale-zh-tw wireless-tools wpa-supplicant xz "



