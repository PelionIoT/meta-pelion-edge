do_install_append() {

    #
    # pt-example under sysvinit respawn to inittab
    #

    echo 'ex:12345:respawn:/wigwag/mbed/pt-example -n pt-example --endpoint-postfix=-$(cat /sys/class/net/eth0/address) >> /var/log/pt-example.log' >> ${D}${sysconfdir}/inittab

    #
    # blept-example under sysvinit respawn to inittab
    #

    echo 'eb:12345:respawn:/wigwag/mbed/blept-example -n blept-example -e ble -c -d /wigwag/mbed/blept-devices.json >> /var/log/blept-example.log' >> ${D}${sysconfdir}/inittab
}
