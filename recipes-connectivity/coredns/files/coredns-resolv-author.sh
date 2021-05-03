#!/bin/bash
# ----------------------------------------------------------------------------
# Copyright (c) 2021, Pelion  and affiliates.
#
# SPDX-License-Identifier: Apache-2.0
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#     http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
# ----------------------------------------------------------------------------
RCONF=EDGE_RUN/coredns/resolv.conf

usage (){
	echo "Constructs a resolv.conf file used by kubelet"
	echo "  $0 <NODEIP>"
	echo "Example"
	echo "  $0 10.0.2.15"
	exit
}
if ! [ "$(id -u)" -eq 0 ]; then
	echo "Must be run as root"
	exit
fi
​
if [ $# -ne 1 ]; then
	usage
fi

NODEIP="$1"
searchLine="hostname.local "

grabSearch(){
	local file="$1"
	if [[ -e "$file" ]]; then
		searchLine+="$(cat $file |  egrep ^search | sed 's/[^ ]* *//' )"
	fi
}

addNS(){
	local file="$1"
	if [[ -e $file ]]; then
		cat $file | grep nameserver >> $RCONF
	fi
	grabSearch "$file"
}


if [[ ! -e EDGE_RUN/coredns ]]; then
	mkdir -p EDGE_RUN/coredns
fi



echo "nameserver ${NODEIP}" > $RCONF
addNS /etc/resolv.conf
addNS /run/systemd/resolve/resolv.conf
addNS /etc/resolv-conf.NetworkManager
echo "search $searchLine" >> $RCONF

