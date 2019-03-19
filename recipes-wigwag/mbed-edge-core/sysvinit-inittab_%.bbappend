do_install_append() {

    #
    # add edge-core under sysvinit respawn to inittab
    #
    echo "ec:12345:respawn:/wigwag/mbed/edge-core --http-port=9101 >> /var/log/edge-core.log 2>&1" >> ${D}${sysconfdir}/inittab
}
