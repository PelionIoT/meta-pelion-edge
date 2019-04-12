#!/bin/sh
# ----------------------------------------------------------------------------

# default values
LOCATION=0
OFFSET=0
SIZE=0

# parse command line arguments
while [ $# -gt 1 ]
do
key="$1"
case $key in
    -h|--header)
    HEADER="$2"
    shift # past argument=value
    ;;
    -f|--firmware)
    FIRMWARE="$2"
    shift # past argument=value
    ;;
    -l|--location)
    LOCATION="$2"
    shift # past argument=value
    ;;
    -o|--offset)
    OFFSET="$2"
    shift # past argument=value
    ;;
    -s|--size)
    SIZE="$2"
    shift # past argument=value
    ;;
    *)
            # unknown option
    ;;
esac
shift
done

# echo "header:   ${HEADER}"
# echo "firmware: ${FIRMWARE}"
# echo "location: ${LOCATION}"
# echo "offset:   ${OFFSET}"
# echo "size:     ${SIZE}"

