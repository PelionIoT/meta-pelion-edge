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
. /wigwag/mbed/arm_update_cmdline.sh

echo "-------------------- Executing activate.sh -------------------------"
mkdir -p /userdata/extended
# copy header to store
VALUE=$(cp $HEADER /userdata/extended/header.bin)

mkdir -p /userdata/.logs-before-upgrade
cp -R /wigwag/log/* /userdata/.logs-before-upgrade/

systemctl stop maestro
systemctl start maestro

mv $FIRMWARE /upgrades/firmware.tar.gz
tar -xzf /upgrades/firmware.tar.gz -C /upgrades/
systemctl start deviceos-wd
reboot
echo "-------------------- Finished activate.sh -------------------------"
exit $VALUE

