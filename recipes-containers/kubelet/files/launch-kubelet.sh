#!/bin/bash
# ----------------------------------------------------------------------------
# Copyright (c) 2021, Pelion and affiliates.
# Copyright (c) 2020, Arm Limited and affiliates.
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

DEVICE_ID=`jq -r .deviceID /userdata/edge_gw_config/identity.json`
if [[ $? -ne 0 ]] || [[ $DEVICE_ID == null ]]; then
	echo "Unable to extract device ID from identity.json"
	exit 1
fi

RCONF=EDGE_RUN/coredns/resolv.conf
NODE_IP=EDGE_NODEIP
POD_CIDR_GW=EDGE_PODCIDR_GW

write_resolv_conf(){
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
			cat $file | grep nameserver | head -1 >> $RCONF
		fi
		grabSearch "$file"
	}

	write(){
		if [[ ! -e EDGE_RUN/coredns ]]; then
			mkdir -p EDGE_RUN/coredns
		fi
		echo "nameserver ${NODEIP}" > $RCONF
		addNS /etc/resolv.conf
		echo "search $searchLine" >> $RCONF
	}
	write
}

setup_local_kaas_network(){
    IP="${1}"
    ip link add kaas0 type dummy
    ip addr add ${IP}/30 dev kaas0
    ip link set dev kaas0 up

    # Lower priority by increasing metric
    kaasRoute=`ip route | grep kaas0`
    ip route del $kaasRoute
    ip route add $kaasRoute metric 999
}

write_resolv_conf ${POD_CIDR_GW}
setup_local_kaas_network ${NODE_IP}

# Move kuberouter CNI config
if [[ ! -f EDGE_RUN/10-kuberouter.conflist ]]; then
	cp EDGE_CNI_CONF/10-kuberouter.conflist EDGE_RUN/10-kuberouter.conflist
fi

exec EDGE_BIN/kubelet \
--node-ip=${NODE_IP} \
--root-dir=/var/lib/kubelet \
--offline-cache-path=EDGE_KUBELET_STATE/store \
--fail-swap-on=false \
--image-pull-progress-deadline=2m \
--hostname-override=${DEVICE_ID} \
--kubeconfig=EDGE_KUBELET_STATE/kubeconfig \
--cni-bin-dir=EDGE_OPT/cni/bin \
--cni-conf-dir=EDGE_RUN/ \
--network-plugin=cni \
--register-node=true \
--node-status-update-frequency=150s \
--runtime-cgroups=/systemd/system.slice \
--kubelet-cgroups=/systemd/system.slice \
--resolv-conf=EDGE_RUN/coredns/resolv.conf \
--hosts-path=EDGE_RUN/coredns/hosts
