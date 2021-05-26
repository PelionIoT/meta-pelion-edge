#!/bin/bash

usage (){
    echo "Manage iptable rule for ARGUS with CoreDNS on any port"
    echo "  $0 add/del <PodCIDR> <DNSPORT>"
    echo "Example"
    echo "  $0 add 10.240.0.0/24"
    exit
}

if ! [ "$(id -u)" -eq 0 ]; then
    echo "Must be run as root"
    exit
fi

if ! [ $# -eq 3 ]; then
    usage
fi

getGatewayAddress() {
    range=`echo $1 | cut -d '/' -f1`
    range=`echo ${range%?}1`
    echo $range
}

# -------- START OF PROGRAM ----------
OP=$1
CIDR=$2
DNSPORT=$3

# Allow traffic to CoreDNS even if kube-router has created egress rules (must be on top in iptables)
gateway=`getGatewayAddress $CIDR`
ALLOW_TO_COREDNS="PREROUTING -s $CIDR -d $gateway -p udp --dport ${DNSPORT} -j MARK --set-mark 0x10000 -m comment --comment \"pelion-edge allow traffic to CoreDNS\""

add (){
    eval "iptables -t nat -C $ALLOW_TO_COREDNS" > /dev/null 2>&1
    if ! [ $? -eq 0 ]; then
        eval "iptables -t nat -I $ALLOW_TO_COREDNS"
    fi
    
    echo "Done adding iptable rules"
}

del (){
    echo "Del $ALLOW_TO_COREDNS"
    eval "iptables -t nat -D $ALLOW_TO_COREDNS"
}

if [ "$OP" = "add" ]; then
    add
elif [ "$OP" = "del" ]; then
    del
else
    usage
fi