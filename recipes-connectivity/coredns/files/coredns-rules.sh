#!/bin/sh
​
usage (){
    echo "Manage iptable rule for ARGUS with CoreDNS on any port"
    echo "  $0 add/del <PodCIDR> <Forward IP> <CoreDNS Port>"
    echo "Example"
    echo "  $0 add 10.240.0.1/24 10.0.2.15 54"
    exit
}
​
if ! [ "$(id -u)" -eq 0 ]; then
    echo "Must be run as root"
    exit
fi
​
if [ $# -le 3 ]; then
    usage
fi
​
# -------- START OF PROGRAM ----------
​
OP=$1
CIDR=$2
DST=$3
PORT=$4
​
# Rules for redirecting traffic to CoreDNS
REDIRECT_UDP="PREROUTING -s $CIDR -d $DST -p udp -m udp --dport 53 -j REDIRECT --to-ports $PORT -m comment --comment \"pelion-edge redirect to CoreDNS\""
REDIRECT_TCP="PREROUTING -s $CIDR -d $DST -p tcp -m tcp --dport 53 -j REDIRECT --to-ports $PORT -m comment --comment \"pelion-edge redirect to CoreDNS\""
​
# Allow traffic to CoreDNS even if kube-router has created egress rules (must be on top in iptables)
ALLOW_TO_COREDNS="PREROUTING -s $CIDR -d $DST -p udp --dport 53 -j MARK --set-mark 0x10000 -m comment --comment \"pelion-edge allow traffic to CoreDNS\""
​
########### IMPORTANT ###########
# Rule to ALWAYS allow outgoing traffic from the Node, mark 0x10000 avoids all kube-router rules
#   This is necessary as kube-router creates rules for pods on host network as if they were
#   behind the network interface "bridge", which can corrupt the host network stack, blocking regular traffic.
#   
#   Without this rule, egress policies can brake the host network if a Pod IP coincides with an interface IP
ALLOW_NODE_OUTPUT="OUTPUT -j MARK --set-mark 0x10000 -m comment --comment \"pelion-edge never block Node outgoing traffic with kube-router\""
​
add (){
    eval "iptables -t nat -C $REDIRECT_UDP" > /dev/null 2>&1
    if ! [ $? -eq 0 ]; then
        eval "iptables -t nat -I $REDIRECT_UDP"
    fi
​
    eval "iptables -t nat -C $REDIRECT_TCP" > /dev/null 2>&1
    if ! [ $? -eq 0 ]; then
        eval "iptables -t nat -I $REDIRECT_TCP"
    fi
​
    eval "iptables -t nat -C $ALLOW_TO_COREDNS" > /dev/null 2>&1
    if ! [ $? -eq 0 ]; then
        eval "iptables -t nat -I $ALLOW_TO_COREDNS"
    fi
​
    eval "iptables -t nat -C $ALLOW_NODE_OUTPUT" > /dev/null 2>&1
    if ! [ $? -eq 0 ]; then
        eval "iptables -t nat -I $ALLOW_NODE_OUTPUT"
    fi
​
    echo "Done adding iptable rules"
}
​
del (){
    echo "Del: $REDIRECT_UDP"
    eval "iptables -t nat -D $REDIRECT_UDP"
​
    echo "Del $REDIRECT_TCP"
    eval "iptables -t nat -D $REDIRECT_TCP"
​
    echo "Del $ALLOW_TO_COREDNS"
    eval "iptables -t nat -D $ALLOW_TO_COREDNS"
​
    echo "Del $ALLOW_NODE_OUTPUT"
    eval "iptables -t nat -D $ALLOW_NODE_OUTPUT"
}
​
if [ "$OP" = "add" ]; then
    add
elif [ "$OP" = "del" ]; then
    del
else
    usage
fi