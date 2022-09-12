# The MIT License (MIT)
#
# Copyright (c) 2015 Jumpnow Technologies, LLC
#
# Permission is hereby granted, free of charge, to any person obtaining a copy
# of this software and associated documentation files (the "Software"), to deal
# in the Software without restriction, including without limitation the rights
# to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
# copies of the Software, and to permit persons to whom the Software is
# furnished to do so, subject to the following conditions:
#
# The above copyright notice and this permission notice shall be included in all
# copies or substantial portions of the Software.
#
# THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
# IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
# FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
# AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
# LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
# OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
# SOFTWARE.

# Used for image level changes to user password expiry information.
#
# Below is an example showing how to use this functionality.
# This will force a password change for root on first login.
#
# INHERIT += "chageusers"
# CHAGE_USERS_PARAMS = "chage -d0 root;"
#
# Other chage commands probably work, but this is the only one I tested.

PACKAGE_INSTALL:append = " ${@['', 'base-passwd shadow'][bool(d.getVar('CHAGE_USERS_PARAMS'))]}"

ROOTFS_POSTPROCESS_COMMAND:append = " chage_user;"

chage_user () {
	chage_user_settings="${CHAGE_USERS_PARAMS}"
	export PSEUDO="${FAKEROOTENV} ${STAGING_DIR_NATIVE}${bindir}/pseudo"

	setting=$(echo $chage_user_settings | cut -d ';' -f1)
	remaining=$(echo $chage_user_settings | cut -d ';' -f2-)

	while [ -n "$setting" ]; do
		cmd=$(echo $setting | cut -d ' ' -f1)

		if [ "$cmd" = "chage" ]; then
			opts=$(echo $setting | cut -d ' ' -f2-)
			perform_chage "${IMAGE_ROOTFS}" "-R ${IMAGE_ROOTFS} $opts"
		else
			bbfatal "Invalid command in CHAGE_USERS_PARAMS: $cmd"
		fi

		# Avoid infinite loop if the last parameter doesn't end with ';'
		if [ "$setting" = "$remaining" ]; then
			break
		fi

		# iterate to the next setting
		setting=$(echo $remaining | cut -d ';' -f1)
		remaining=$(echo $remaining | cut -d ';' -f2-)
	done
}

perform_chage () {
	set +e
	local rootdir="$1"
	local opts="$2"
	bbnote "${PN}: Performing chage with [$opts]"
	local username=$(echo "$opts" | awk '{ print $NF }')
	grep -q "^$username:" $rootdir/etc/passwd

	if [ $? -eq 0 ]; then
		eval flock -x $rootdir${sysconfdir} -c \"$PSEUDO chage \$opts\" || true
		if [ $? -ne 0 ]; then
			bbfatal "${PN}: chage command did not succeed"
		fi
	else
		bbwarn "${PN}: user $username does not exist, unable to modify"
	fi
	set -e
}
