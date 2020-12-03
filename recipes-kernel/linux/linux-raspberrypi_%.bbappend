FILESEXTRAPATHS_prepend := "${THISDIR}/files:"

SRC_URI += " file://default-cpu-scaling-gov.cfg"
CMDLINE_append = 'cgroup_enable=cpuset cgroup_enable=memory cgroup_memory=1'
