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

/usr/bin/btuart
/usr/bin/bthelper hci0
