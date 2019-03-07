#!/bin/sh
### BEGIN INIT INFO
# Provides:          hci-init-rpi.sh
# Required-Start:    $bluetooth
# Required-Stop:
# Default-Start:     2 3 4 5
# Default-Stop:
# Short-Description: bring up hci0
# Description:
### END INIT INFO

if [ "$1" = "start" ]; then
    /usr/bin/btuart
    /usr/bin/bthelper hci0
    /usr/bin/hciconfig hci0 down
    /usr/bin/hciconfig hci0 up
else
    echo >&2 "$0": function "$1" not supported.
fi
