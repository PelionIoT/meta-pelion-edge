EXTRA_OECONF += "--enable-compat-libdns_sd"
FILES:${PN} += "${libdir}/*"
do_install:append () {
echo "libdir is at: ${libdir}" > /tmp/avahi_0.8.log
}
