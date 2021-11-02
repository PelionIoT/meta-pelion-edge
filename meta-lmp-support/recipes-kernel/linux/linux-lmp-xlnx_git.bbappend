FILESEXTRAPATHS_prepend := "${THISDIR}/files:"
 
SRC_URI_append_uz = " file://xilinx_cpu_idle.cfg"
QB_KERNEL_CMDLINE_APPEND = 'systemd.log_level=debug'
