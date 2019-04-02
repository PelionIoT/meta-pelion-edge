#!/bin/bash
# /etc/init.d/devicejs: starts DeviceJS & WigWag runtime
# good article on runlevel info: http://www.thegeekstuff.com/2012/03/lsbinit-script/

### BEGIN INIT INFO
# Provides:             devicejs
# Required-Start:       $remote_fs $time wwrelay
# Required-Stop:        $remote_fs $time
# Should-Start:         $network
# Should-Stop:          $network
# Default-Start:        5
# Default-Stop:         0 1 6
# Short-Description:    System logger
### END INIT INFO
WIGWAGROOT="/wigwag"

START_SOFTWARE="maestro" # runner or maestro

MAESTRO_DIR="${WIGWAGROOT}/system/bin"
GREASE_STATIC_LIB="${WIGWAGROOT}/system/lib"
MAESTRO_CONFIG="${WIGWAGROOT}/wwrelay-utils/conf/maestro-conf/edge-config-rpi-production.yaml"

MAESTRO_START_CMD="$MAESTRO_DIR/maestro -config ${MAESTRO_CONFIG}"
MAESTRO_STOP_CMD="echo Need_Stop_Command"


RUNNER_DIR="${WIGWAGROOT}/devicejs-core-modules/Runner"
RUNNER_START_CMD="./start -c ./relay.config.json"
RUNNER_STOP_CMD="echo Need_Stop_Command"

export NODE_PATH=${WIGWAGROOT}/devicejs-core-modules/node_modules

RUNNER_LOG="${WIGWAGROOT}/log/runner.log"
WWRELAY_LOG=${WIGWAGLOGROOT}"/wwrelay.log"

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
    if [[ $START_SOFTWARE = "maestro" ]]; then
        $MAESTRO_STOP_CMD > $MAESTRO_RUNTIME_LOG 2>&1
    else
        pushd $RUNNER_DIR
        $RUNNER_STOP_CMD > $RUNNER_LOG 2>&1
        popd
    fi
}
START() {
    if [[ -e $maestroOK ]]; then
        echo "hey my maestro is ok (devicejs)" >> /wigwag/log/deviceOSWD.log
        eval $COLOR_BOLD
        eval $COLOR_NORMAL
        config_cpu_scaling
        # do we need to store maestro runtime? maestro log is stored somewhere else (check maestro config file)
        # if [ -e $MAESTRO_RUNTIME_LOG ]; then
        #   mv $MAESTRO_RUNTIME_LOG $MAESTRO_RUNTIME_LOG.1
        # fi
        if [[ $START_SOFTWARE = "maestro" ]]; then
            ulimit -c unlimited
            LD_LIBRARY_PATH=$GREASE_STATIC_LIB:/$LD_LIBRARY_PATH
            export LD_LIBRARY_PATH
            echo "starting maestro"
            $MAESTRO_START_CMD >> $MAESTRO_RUNTIME_LOG 2>&1 &
        else
            if [ -e $RUNNER_LOG ]; then
                mv $RUNNER_LOG $RUNNER_LOG.1
            fi

            ulimit -c unlimited
            pushd $RUNNER_DIR
            echo "starting runner"
            $RUNNER_START_CMD > $RUNNER_LOG 2>&1 &
            popd
        fi
    else
        #When the relay doesn't have its keys in the right places, we dont autostart devicejs or the watchdog...
        if [[ $START_SOFTWARE = "maestro" ]]; then
            echo "did not start devicejs because wwrelay said not to" >> $MAESTRO_RUNTIME_LOG
        else
            echo "did not start devicejs because wwrelay said not to" >> $RUNNER_LOG
            echo "did not start devicejs because wwrelay said not to" >> $WWRELAY_LOG
        fi
        exit 7
     fi
    }



    case "$1" in
        start) START; ;;
    #
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

