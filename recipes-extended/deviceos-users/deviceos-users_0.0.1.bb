SUMMARY = "Example recipe for using inherit useradd"
DESCRIPTION = "This recipe serves as an example for using features from useradd.bbclass"
SECTION = "examples"
PR = "r1"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/COPYING.MIT;md5=3da9cfbcb788c80a0384361b4de20420"

SRC_URI = "file://file1 \
           file://file2 \
           file://file3 \
           file://file4"

S = "${WORKDIR}"

PACKAGES =+ "${PN}-developer2"

#EXCLUDE_FROM_WORLD = "1"

inherit useradd

# You must set USERADD_PACKAGES when you inherit useradd. This
# lists which output packages will include the user/group
# creation code.
USERADD_PACKAGES = "${PN} ${PN}-developer2"

USERADD_UID_TABLES += "files/pelion-passwd-table"
USERADD_GID_TABLES += "files/pelion-group-table"

# You must also set USERADD_PARAM and/or GROUPADD_PARAM when
# you inherit useradd.

# USERADD_PARAM specifies command line options to pass to the
# useradd command. Multiple users can be created by separating
# the commands with a semicolon. Here we'll create two users,
# user1 and user2:

# useradd -p 'VVVVV' developer; \
# useradd -p 'WWWWW' devjs; \
# useradd -p 'XXXXX' wigwaguser; \
# useradd -p 'YYYYY' devicejs; \
# useradd -p 'ZZZZZ' support; \
# useradd -G devicejs devicejs; \
# 
# 
# groupadd developers; \
# groupadd pulse; \
# groupadd devicejs; \
# useradd -G devicejs devicejs; \
# useradd -G dialout devicejs; \
# groupmod -g 1020 developers; \
# groupmod -g 1020 developers; \
# "


USERADD_PARAM:${PN} = "\
-u 1200 -r -s /bin/bash -P 'maestro' -g maestro -G dialout,tty maestro; \
-u 1201 -r -s /bin/bash -P 'deviceos' -g deviceos deviceos; \
-u 1202 -d /home/developer -m -r -s /bin/bash -P 'developer' -g developer developer; \
-u 1203 -d /home/support -m -r -s /bin/bash -P 'support' -g support support; \
-u 1204 -d /home/user -m -r -s /bin/bash -P 'user' -g user user; \
"

# user3 will be managed in the useradd-example-user3 pacakge:
# As an example, we use the -P option to set clear text password for user3
USERADD_PARAM:${PN}-developer2 = "-u 1212 -d /home/developer2 -m -r -s /bin/bash -P 'developer2' developer2"

# GROUPADD_PARAM works the same way, which you set to the options
# you'd normally pass to the groupadd command. This will create
# groups group1 and group2:

GROUPADD_PARAM:${PN} = "\
-g 900 maestro; \
-g 901 deviceos; \
-g 1022 developer; \
-g 1023 support; \
-g 1024 user; \
-g 890 pulse; \
"

# Likewise, we'll manage group3 in the useradd-example-user3 package:
GROUPADD_PARAM:${PN}-user3 = "-g 1032 developer2"

# datadir = /usr/share
#install -d (directory)
#install -m (mode permissions (like chmod))
#install -p (perserve timestamps)
do_install () {
	install -d -m 755 ${D}${datadir}/user1
	install -d -m 755 ${D}${datadir}/user2
	install -d -m 755 ${D}${datadir}/developer2

	install -p -m 644 file1 ${D}${datadir}/user1/
	install -p -m 644 file2 ${D}${datadir}/user1/

	install -p -m 644 file2 ${D}${datadir}/user2/
	install -p -m 644 file3 ${D}${datadir}/user2/

	install -p -m 644 file3 ${D}${datadir}/developer2/
	install -p -m 644 file4 ${D}${datadir}/developer2/

	# The new users and groups are created before the do_install
	# step, so you are now free to make use of them:
	chown -R developer ${D}${datadir}/user1
	chown -R support ${D}${datadir}/user2
	chown -R user ${D}${datadir}/developer2

	chgrp -R developer ${D}${datadir}/user1
	chgrp -R pulse ${D}${datadir}/user2
	chgrp -R developer ${D}${datadir}/developer2
}

FILES:${PN} = "${datadir}/user1/* ${datadir}/user2/*"
FILES:${PN}-developer2 = "${datadir}/developer2/*"

# Prevents do_package failures with:
# debugsources.list: No such file or directory:
INHIBIT_PACKAGE_DEBUG_SPLIT = "1"
