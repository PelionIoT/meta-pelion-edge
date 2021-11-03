#!/bin/bash

# ----------- Values --------------
NODE_IP="172.21.1.0"
POD_CIDR="172.21.2.0/24"
POD_CIDR_GW="172.21.2.1"
# ---------------------------------

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
