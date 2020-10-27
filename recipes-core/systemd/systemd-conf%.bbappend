SUMMARY = "adds flags to journeld.conf in a simlar way to the metod used by systemd-conf found in the systemd bitbake reciepe"
do_install_append () {
   sed -i -e 's/.*Storage.*/Storage=persistent/' ${D}${sysconfdir}/systemd/journald.conf
   sed -i -e 's/.*SystemMaxUse.*/SystemMaxUse=64M/' ${D}${sysconfdir}/systemd/journald.conf
   sed -i -e 's/.*Seal.*/Seal=yes/' ${D}${sysconfdir}/systemd/journald.conf
}
