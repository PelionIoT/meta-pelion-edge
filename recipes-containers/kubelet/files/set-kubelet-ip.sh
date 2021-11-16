#!/bin/bash
# ----------------------------------------------------------------------------
# Copyright (c) 2021, Pelion and affiliates.
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

# -------- INSTRUCTIONS --------
# Change the Pelion kubelet IP and Pod CIDR.
#
# 1. NODE_IP sets the kubelet IP by modifying the `kaas0` interface.
# 2. POD_CIDR and POD_CIDR_GW sets the IP range for Pods. The POD_CIDR_GW must 
#    correspond to the gateway of the interface created from the POD_CIDR 
#    subnet. For /24 subnet, this would normally be the IP with the last number 
#    set to "1".
# 3. Run the script.

NODE_IP="172.21.1.0"
POD_CIDR="172.21.2.0/24"
POD_CIDR_GW="172.21.2.1"
# ------------------------------

if [[ $UID != 0 ]]; then
    echo "Requires sudo"
    exit 1
fi

validate_ip() {
    IP="${1}"
    if ! [[ ${IP} =~ ^[0-9]+\.[0-9]+\.[0-9]+\.[0-9]+$ ]]; then
        echo "Invalid IP ${IP}"
        exit
    fi
}

validate_pod_cidr() {
    ip="${1%/*}"
    if ! [ $? -eq 0 ]; then
        echo "Invalid podCIDR ${1}"
        exit
    fi

    validate_ip ${ip}
    subnet=${1#*/}
    if [ ${subnet} -ge 32 ]; then
        echo "PodCIDR subnet too small ${1}"
    fi
}

validate_ip ${NODE_IP}
validate_ip ${POD_CIDR_GW}
validate_pod_cidr ${POD_CIDR}

# -------- Replace IPs in all relevant files --------
sed -i  "s/NODE_IP=.* */NODE_IP=$(echo ${NODE_IP} | sed -e 's/\\/\\\\/g; s/\//\\\//g; s/&/\\\&/g')/g" "EDGE_BIN/launch-kubelet.sh"
sed -i  "s/POD_CIDR_GW=.* */POD_CIDR_GW=$(echo ${POD_CIDR_GW} | sed -e 's/\\/\\\\/g; s/\//\\\//g; s/&/\\\&/g')/g" "EDGE_BIN/launch-kubelet.sh"
sed -i  "s/POD_CIDR=.* */POD_CIDR=$(echo ${POD_CIDR} | sed -e 's/\\/\\\\/g; s/\//\\\//g; s/&/\\\&/g')/g" "EDGE_BIN/launch-kube-router.sh"
sed -i  "s/POD_CIDR=.* */POD_CIDR=$(echo ${POD_CIDR} | sed -e 's/\\/\\\\/g; s/\//\\\//g; s/&/\\\&/g')/g" "EDGE_BIN/launch-coredns.sh"
sed -i  "s/bind.* */bind $(echo ${POD_CIDR_GW} | sed -e 's/\\/\\\\/g; s/\//\\\//g; s/&/\\\&/g')/g" "EDGE_STATE/coredns/corefile"

# -------- Restart services --------
systemctl stop kube-router kubelet coredns

for name in $(ip -o link show | grep -e 'master kube-bridge' | awk -F': ' '{print $2}' | grep -e 'veth' | cut -d "@" -f 1)
do
    ip link delete ${name}
done

ip link set dev kube-bridge down
ip link delete kube-bridge
ip link delete kaas0
systemctl start kube-router kubelet coredns

echo "Done! Please allow a few minutes for the services to startup."
