
# Copyright (C) 2007 OpenedHand Ltd.
# Copyright (C) 2012 Red Hat, Inc.
#

SUMMARY = "WigWag build dependencies"
LICENSE = "MIT"
PR = "r0"

inherit packagegroup


#include recipes-core/packagegroups/packagegroup-core-buildessential
#include recipes-core/packagegroups/packagegroup-core-buildessential.bb

RDEPENDS_packagegroup-wigwag-buildessentials = "packagegroup-core-buildessential git go kernel-dev"



