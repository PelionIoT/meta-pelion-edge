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

if md5sum -c /userdata/mbed/version_checksum.md5 | grep -q 'FAILED'; then
  VALUE=$(cp /userdata/extended/header.bin $HEADER)
  echo $HEADER
  echo "-------------------- Build version changed, returning header --------------------";
  exit $VALUE
else
  echo "-------------------- Build version same, returning null --------------------";
  exit
fi
