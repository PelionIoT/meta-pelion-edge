FILESEXTRAPATHS_prepend := "${THISDIR}/files:"

SRC_URI += "file://0001-Edge-increased-the-HCI_LE_AUTOCONN_TIMEOUT-to-20-sec.patch \
            file://default-cpu-scaling-gov.cfg"
CMDLINE_append = 'cgroup_enable=cpuset cgroup_enable=memory cgroup_memory=1'
