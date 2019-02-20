#!/bin/sh
echo "Executing init script from initramfs"
echo "Not doing anything for now.  Will sleep for 5 seconds and then switch_root"
sleep 5.0
#Switch to the new filesystem, and run /sbin/init out of it
exec switch_root -c /dev/console /mnt/rootfs /sbin/init
