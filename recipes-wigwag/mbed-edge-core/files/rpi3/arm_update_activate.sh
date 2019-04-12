#!/bin/sh
# ----------------------------------------------------------------------------

# Parse command line
#
# HEADER
# FIRMWARE
# LOCATION
# OFFSET
# SIZE
#
. /wigwag/mbed/update_scripts/arm_update_cmdline.sh

echo "-------------------- Executing activate.sh -------------------------"
mkdir -p /userdata/extended
# copy header to store
VALUE=$(cp $HEADER /userdata/extended/header.bin)

rm -rf /wigwag/log/*
killall maestro
/etc/init.d/maestro.sh start

mv $FIRMWARE /upgrades/firmware.tar.gz
tar -xzf /upgrades/firmware.tar.gz -C /upgrades/
/etc/init.d/deviceOS-watchdog start
reboot
echo "-------------------- Finished activate.sh -------------------------"
exit $VALUE

