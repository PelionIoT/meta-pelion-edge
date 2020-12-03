EXTRA_OECONF += "--enable-compat-libdns_sd"
FILES_${PN} += "${libdir}/*"
do_install_append () {
echo "libdir is at: ${libdir}" > /tmp/avahi_0.8.log
}
