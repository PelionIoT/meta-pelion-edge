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

    # Jiggle the hci interface to help it start.
    # Note: the next two lines aren't needed if the previous 2
    # commands are executed manually from the command line after
    # startup, so there seems to be a timing issue with the
    # bluetooth interface and this init script.
    # TODO: figure out the timing issue to understand why bthelper
    # alone isn't enough to bring up the interface the first time
    # it's called.
    /usr/bin/hciconfig hci0 down
    /usr/bin/hciconfig hci0 up
else
    echo >&2 "$0": function "$1" not supported.
fi
