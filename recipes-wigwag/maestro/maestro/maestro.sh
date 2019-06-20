#!/bin/bash
# /etc/init.d/devicejs: starts maestro
# good article on runlevel info: http://www.thegeekstuff.com/2012/03/lsbinit-script/

### BEGIN INIT INFO
# Provides:             maestro
# Required-Start:       $remote_fs $time wwrelay
# Required-Stop:        $remote_fs $time
# Should-Start:         $network
# Should-Stop:          $network
# Default-Start:        5
# Default-Stop:         0 1 6
# Short-Description:    System logger
### END INIT INFO
WIGWAGROOT="/wigwag"

MAESTRO_DIR="${WIGWAGROOT}/system/bin"
GREASE_DYNAMIC_LIB="${WIGWAGROOT}/system/lib"
MAESTRO_CONFIG="${WIGWAGROOT}/wwrelay-utils/conf/maestro-conf/edge-config-rpi-production.yaml"

MAESTRO_START_CMD="$MAESTRO_DIR/maestro -config ${MAESTRO_CONFIG}"
MAESTRO_STOP_CMD="echo Need_Stop_Command"

MAESTRO_RUNTIME_LOG="${WIGWAGROOT}/log/maestro-runtime.log"
PIDROOT="/var/run"
maestroOK=$PIDROOT"/maestroOK"

config_cpu_scaling() {
    MAX_FREQ=/sys/devices/system/cpu/cpu0/cpufreq/scaling_max_freq
    MIN_FREQ=/sys/devices/system/cpu/cpu0/cpufreq/scaling_min_freq
    cat $MAX_FREQ > $MIN_FREQ
    MAX_FREQ=/sys/devices/system/cpu/cpu1/cpufreq/scaling_max_freq
    MIN_FREQ=/sys/devices/system/cpu/cpu1/cpufreq/scaling_min_freq
    cat $MAX_FREQ > $MIN_FREQ
}

stop_devjs() {
    $MAESTRO_STOP_CMD > $MAESTRO_RUNTIME_LOG 2>&1
}

START() {
    if [[ -e $maestroOK ]]; then
        echo "hey my maestro is ok (devicejs)" >> /wigwag/log/deviceOSWD.log
        eval $COLOR_BOLD
        eval $COLOR_NORMAL
        config_cpu_scaling
        ulimit -c unlimited
        echo "starting maestro"
        export LD_LIBRARY_PATH=$GREASE_DYNAMIC_LIB:$LD_LIBRARY_PATH  
        $MAESTRO_START_CMD >> $MAESTRO_RUNTIME_LOG 2>&1 &
    else
        #When the relay doesn't have its keys in the right places, we dont autostart devicejs or the watchdog...
        echo "did not start maestro because wwrelay said not to" >> $MAESTRO_RUNTIME_LOG
        exit 7
    fi
}

case "$1" in
    start) 
        START; 
        ;;
    stop)
        echo "Stopping DeviceJS"
        stop_devjs
        ;;
    restart)
        echo "Restarting DeviceJS"
        stop_devjs
        config_cpu_scaling
        run_devjs
        ;;
    *)
        echo "Usage: $0 {start|stop|restart}"
        exit 1
esac

exit 0

