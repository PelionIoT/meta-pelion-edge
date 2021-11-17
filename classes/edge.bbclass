#class for Pelion Edge variables and functionality
#Linux Foundation Filesystem Hierarchy Standard: https://refspecs.linuxfoundation.org/FHS_3.0/fhs-3.0.html
#
#Example usage found in edge-proxy.bb 
#    inherit edge
#    do_install function declaration
#       install -d ${D}${EDGE_BIN}
#       install -m 0755 ${B}/${GO_BUILD_BINDIR}/edge-proxy ${D}${EDGE_BIN}/
#
#
#
#
#-----------------------------------------------------------------------------------------------------#
#                                                  _   _                                              #
#                                      _ __   __ _| |_| |__  ___                                      #
#                                     | '_ \ / _` | __| '_ \/ __|                                     #
#                                     | |_) | (_| | |_| | | \__ \                                     #
#                                     | .__/ \__,_|\__|_| |_|___/                                     #
#                                     |_|                                                             #
#-----------------------------------------------------------------------------------------------------#
#/usr/bin
EDGE_BIN = "${bindir}"

#/usr/lib/edge
EDGE_LIB = "${libdir}/edge"

#/etc/edge
EDGE_ETC = "${sysconfdir}/edge"

#/var/rootdirs/userdata
EDGE_DATA = "${localstatedir}/rootdirs/userdata"

#/var/lib/edge
EDGE_STATE = "${localstatedir}/lib/edge"

#/opt
EDGE_OPT = "${base_prefix}/opt"

#/var/run/edge 
EDGE_RUN = "${localstatedir}/run/edge"

#TEMPORARY: The following lines keep the system as it was in 2.3 
EDGE_BIN = "/wigwag/system/bin"
EDGE_LIB = "/wigwag/system/lib"
EDGE_ETC = "/wigwag/system/etc"
EDGE_DATA = "/userdata"
EDGE_STATE = "/wigwag/system/var/lib"
EDGE_OPT = "/wigwag/system/opt"
#END TEMPORARY

EDGE_CNI_CONF = "${EDGE_ETC}/cni/net.d"
EDGE_CNI_BIN = "${EDGE_OPT}/cni/bin"
EDGE_KUBELET_STATE = "${EDGE_STATE}/kubelet"
EDGE_COREDNS_STATE = "${EDGE_STATE}/coredns"
#-----------------------------------------------------------------------------------------------------#
#                                              _       _     _                                        #
#                             __   ____ _ _ __(_) __ _| |__ | | ___  ___                              #
#                             \ \ / / _` | '__| |/ _` | '_ \| |/ _ \/ __|                             #
#                              \ V / (_| | |  | | (_| | |_) | |  __/\__ \                             #
#                               \_/ \__,_|_|  |_|\__,_|_.__/|_|\___||___/                             #
#                                                                                                     #
#-----------------------------------------------------------------------------------------------------#
EDGE_NODEIP = "10.0.0.50"
EDGE_PODCIDR = "10.240.0.0/24"
EDGE_PODCIDR_GW = "10.240.0.1"
EDGE_NODEDNSPORT = "53"



#*Note: comments exemplifying paths assume a standard version of yocto with an unmodified bitbake.conf

edge_replace_vars() {
	for file in "$@"; do
	    sed -i  "s/EDGE_BIN/$(echo ${EDGE_BIN} | sed -e 's/\\/\\\\/g; s/\//\\\//g; s/&/\\\&/g')/g" "$file"
	    sed -i  "s/EDGE_LIB/$(echo ${EDGE_LIB} | sed -e 's/\\/\\\\/g; s/\//\\\//g; s/&/\\\&/g')/g" "$file"
	    sed -i  "s/EDGE_ETC/$(echo ${EDGE_ETC} | sed -e 's/\\/\\\\/g; s/\//\\\//g; s/&/\\\&/g')/g" "$file"
	    sed -i  "s/EDGE_DATA/$(echo ${EDGE_DATA} | sed -e 's/\\/\\\\/g; s/\//\\\//g; s/&/\\\&/g')/g" "$file"
	    sed -i  "s/EDGE_STATE/$(echo ${EDGE_STATE} | sed -e 's/\\/\\\\/g; s/\//\\\//g; s/&/\\\&/g')/g" "$file"
	    sed -i  "s/EDGE_OPT/$(echo ${EDGE_OPT} | sed -e 's/\\/\\\\/g; s/\//\\\//g; s/&/\\\&/g')/g" "$file"
	    sed -i  "s/EDGE_RUN/$(echo ${EDGE_RUN} | sed -e 's/\\/\\\\/g; s/\//\\\//g; s/&/\\\&/g')/g" "$file"
	    
	    sed -i  "s/EDGE_CNI_CONF/$(echo ${EDGE_CNI_CONF} | sed -e 's/\\/\\\\/g; s/\//\\\//g; s/&/\\\&/g')/g" "$file"
	    sed -i  "s/EDGE_CNI_BIN/$(echo ${EDGE_CNI_BIN} | sed -e 's/\\/\\\\/g; s/\//\\\//g; s/&/\\\&/g')/g" "$file"
	    sed -i  "s/EDGE_KUBELET_STATE/$(echo ${EDGE_KUBELET_STATE} | sed -e 's/\\/\\\\/g; s/\//\\\//g; s/&/\\\&/g')/g" "$file"
	    sed -i  "s/EDGE_COREDNS_STATE/$(echo ${EDGE_COREDNS_STATE} | sed -e 's/\\/\\\\/g; s/\//\\\//g; s/&/\\\&/g')/g" "$file"
	    
	   	sed -i  "s/EDGE_NODEIP/$(echo ${EDGE_NODEIP} | sed -e 's/\\/\\\\/g; s/\//\\\//g; s/&/\\\&/g')/g" "$file"
		sed -i  "s/EDGE_PODCIDR_GW/$(echo ${EDGE_PODCIDR_GW} | sed -e 's/\\/\\\\/g; s/\//\\\//g; s/&/\\\&/g')/g" "$file"
	   	sed -i  "s/EDGE_PODCIDR/$(echo ${EDGE_PODCIDR} | sed -e 's/\\/\\\\/g; s/\//\\\//g; s/&/\\\&/g')/g" "$file"
	   	sed -i  "s/EDGE_NODEDNSPORT/$(echo ${EDGE_NODEDNSPORT} | sed -e 's/\\/\\\\/g; s/\//\\\//g; s/&/\\\&/g')/g" "$file"
	done
}