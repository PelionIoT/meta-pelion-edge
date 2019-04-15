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
# copy stored header to expected location
echo "-------------------- Executing active_details.sh -------------------------"
VALUE=$(cp /userdata/extended/header.bin $HEADER)

echo $HEADER
echo "-------------------- Finished active_details.sh -------------------------"

exit $VALUE

