#!/bin/sh
#Notes:
#MUST DO
#switchback FORCEUPGRADETHEFACTORY





#https://www.kernel.org/doc/Documentation/filesystems/overlayfs.txt

#Create all the symlinks to /bin/busybox
#this is needed for most other scripts to run
#/bin/busybox --install -s
#Next generatation todos
#EXPORT all vars to the boot env
#pitstop ffiles
#	WIPE_XXX
#	get off of vfat
#	leave boot mounted
master_initscript_version=5
master_sub_version=012

okthisismore=1
debug=1
#---------------------------------------------------------------------------------------------------------------------------
# Globals
#---------------------------------------------------------------------------------------------------------------------------
devbuild=0;
specialmodecount=12;
buttoncount=0;
init="/sbin/init"
lastbutton=""
SCLK=""
SDATA=""
bname="up"
timout=5
locked=1
ledconfig=1
state="starting"
version=0
emmcready=0
sdboot=0
Iamsd=0
wasWiped_boot=0
wasWiped_factory=0
wasWiped_upgrade=0
wasWiped_user=0
wasWiped_userdata=0;
wasTMPFS_built=0;
haveusbmsg=0;

#version varriables
availableUboot=""
currentUboot=""
currentUbootBoard=""

#maybe someday when have downtime: https://github.com/fidian/ansi
NORM=$(echo -e "\033[00;39m" )
BLACK=$(echo -e "\033[00;30m")
RED=$(echo -e "\033[00;31m")
GREEN=$(echo -e "\033[00;32m")
YELLOW=$(echo -e "\033[00;33m")
BLUE=$(echo -e "\033[00;34m")
PURPLE=$(echo -e "\033[00;35m")
MAGENTA=$PURPLE;
#ORANGE=$(echo -e "\033[48:2:255:165:0m%s\033[m\n")
ORANGE=$YELLOW
CYAN=$(echo -e "\033[00;36m")
WHITE=$(echo -e "\033[00;37m")
LBLACK=$(echo -e "\033[01;30m")
LRED=$(echo -e "\033[01;31m")
LGREEN=$(echo -e "\033[01;32m")
LYELLOW=$(echo -e "\033[01;33m")
LBLUE=$(echo -e "\e[01;34m")
LPURPLE=$(echo -e "\033[01;35m")
LCYAN=$(echo -e "\033[01;36M")
WHITE2=$(echo -e "\033[01;37m")

BNORMAL=$(echo -e "\033[00;49m")
BBLACK=$(echo -e "\033[00;40m")
BRED=$(echo -e "\033[00;41m")
BGREEN=$(echo -e "\033[00;42m")
BYELLOW=$(echo -e "\033[00;43m")
BBLUE=$(echo -e "\033[00;44m")
BPURPLE=$(echo -e "\033[00;45m")
BCYAN=$(echo -e "\033[00;46m")
BWHITE=$(echo -e "\033[00;47m")
LBLACK=$(echo -e "\033[01;40m")
BLRED=$(echo -e "\033[01;41m")
BLGREEN=$(echo -e "\033[01;42m")
BLYELLOW=$(echo -e "\033[01;43m")
BLBLUE=$(echo -e "\e[01;44m")
BLPURPLE=$(echo -e "\033[01;45m")
BLCYAN=$(echo -e "\033[01;46M")
BWHITE2=$(echo -e "\033[01;47m")



#led setLED and states
LED_white="10 10 10"
LED_OFF="0 0 0"
LED_RED="20 0 0"
LED_reboot="0 7 0" #green
LED_wipe_user="18 3 0" #orange
LED_wipe_upgrade="12 0 12" #purple
#jjjLED_upgrade_wget="0 10 10" #cyan
LED_wipe_all_wget="0 10 10" #cyan
LED_shell="0 0 10" #blue
LED_remotepull="30 6 11" #pink
LED_LORANGE="5 1 0"
LED_LCYAN="0 1 1"
LED_LGREEN="0 1 0"
LED_LBLUE="0 0 1"
LED_LRED="1 0 0"
LED_LPINK="5 1 3"
LED_LWHITE="1 1 1"
LED_LPURPLE="1 0 2"

#devices and mountpoints
P1=""
P2=""
P3=""
P5=""
P6=""
dev_maindisk=""
dev_boot=""
dev_factory=""
dev_upgrade=""
dev_user=""
dev_userdata=""
dev_thismmc=""
dev_sda1="/dev/sda1"
dev_sdb1="/dev/sdb1"
tmpMP="/mnt/tmp"
commonMP="/mnt/.overlay"
bbmp_boot="/mnt/.boot"
bbmp_factory="$commonMP/factory"
bbmp_upgrade="$commonMP/upgrade"
bbmp_user="$commonMP/user"
bbmp_userdata="$commonMP/userdata"
bbmp_user_slash="$bbmp_user/slash"
bbmp_user_slash_wigwag="$bbmp_user/slash/wigwag"
bbmp_user_work="$bbmp_user/work"
bbmp_usb="/mnt/usb"
UGwwusbmsg="$bbmp_usb/wwusbmsg.sh"
UGdir="$bbmp_user_slash/upgrades"
UGscript=$UGdir"/upgrade.sh"
UGtarball=$UGdir"/upgrade.tar.gz"
Vfactory=$bbmp_factory"/wigwag/etc/versions.json"
Vupgrade=$bbmp_upgrade"/wigwag/etc/versions.json"
newr="/newroot"
newr_boot="$newr"$bbmp_boot
newr_factory="$newr"$bbmp_factory
newr_upgrade="$newr"$bbmp_upgrade
newr_user="$newr"$bbmp_user
newr_userdata="$newr"/userdata
current_factory_version=""
current_upgrade_version=""
potential_next_factory_version=""
potential_next_upgrade_version=""
UGPARTITION_DO_IT_ALL=0;
UGFACTORY_DOIT_ALL=0;
UGUSERDATA_DOIT_ALL=0;
UGUPGRADE_DOIT_ALL=0;
UGUSER_DOIT_ALL=0;
UGBOOT_DOIT_ALL=0;
UGUBOOT_DOIT_ALL=0;
UGBOOT_DOIT_KERNEL=0;
UGLOG="";

#---------------------------------------------------------------------------------------------------------------------------
# Overideable variables
#---------------------------------------------------------------------------------------------------------------------------
#Erases anything inthe /upgrades directory just before exiting "CheckOSMessages"
ERASETHEUPGRADEFILES=1
WDKEEPALIVE="/var/deviceOSkeepalive"
WDPID_PATH="/var/run"

latestimg=https://code.wigwag.com/ugs/latestsafe.tar.gz
BootPartitionOrginalSize=20
tempfsSize=409600K
#our current new partitioning scheme.  This can be overidden from upgrade.sh
BOOT_MiB="50"
FACTORY_MiB="2500"
UPGRADE_MiB="2000"
EXTENDED_PROTECTED_MiB="2010"
USER_MiB="1000"
USERDATA_MiB="1000"
PROTECTED="4096"


#These are the defaults that are set in the int script anways.  To overide them, you can set them to something different

#wipes the factory partition (only use this in specail cases. 
# if we are upgrading the factory partition, it is automatically wiped),
#  if you specify this, and nothing else, you will brick your relay.
WIPETHEFACTORY=0
#upgrades the factory only if the factory differs in version number from the current factory partition. 
#We always wipe the factory clean and never copy over during an upgrade
UPGRADETHEFACTORYWHENNEWER=1
#upgrades the factory regardless of what is currently on the factory partition. 
FORCEUPGRADETHEFACTORY=0
#repartition the emmc to the new parition table (only during a factory upgrade, and only if its needed)
REPARTITIONEMMC=1
#Example:
#	UPGRADETHEFACTORY=0
#	FORCEUPGRADETHEFACTORY=0
#	REPARTITIONEMMC=1
#	Resut, nothing happens
#Example:
#	UPGRADETHEFACTORY=1
#	FORCEUPGRADETHEFACTORY=0
#	REPARTITIONEMMC=1
#	Resut, The drive will be repartitioned (if and only if) the desired partition size does not match the 
#	current partition size, and only if the Upgrade is deemed necessary by having a newer factory available than
#  	what is currnetly installed
#Example:
#	UPGRADETHEFACTORY=0
#	FORCEUPGRADETHEFACTORY=1
#	REPARTITIONEMMC=1
#	Resut, The drive will be repartitioned (if and only if) the desired partition size does not match the 
#	current partition size, A new factory image is forced installed even
#wipes the upgrade partition (only use this in specail cases.  
#if we are upgrading the upgrade partition, it is automatically wiped)
#if you specifiy this, and nothing else, you will brick your relay
WIPETHEUPGRADE=0
#upgrades the upgrade only if the upgrade differs in version number for the current upgrade partition
#we alaways wipe the upgrade partition before installing.  never copy over. 
UPGRADETHEUPGRADEWHENNEWER=1
#upgrades the upgrade regarless of what is currently on the upgrade parition
FORCEUPGRADETHEUPGRADE=0
#wipes the user partition clean
#Note we don't "automatically" wipe the user, userdata, or boot partitions as those parititons hold userdata"
#if you want them wiped in the upgrade, you must call the following
WIPETHEUSER_PARTITION=0
#upgrades the user partition with user.tar.xz.  (an unforseen preventive condition)
#strategy is copyover unless WIPETHEUSER_PARTITION is set.
UPGRADETHEUSER_PARTITIONWHENNEWER=0
#forces the upgrade of the user partition. 
FORCEUPGRADETHEUSER_PARTITION=0
#wipes the userdata partition clean
#Note we don't "automatically" wipe the user, userdata, or boot partitions as those parititons hold userdata"
#if you want them wiped in the upgrade, you must call the following
WIPETHEUSERDATA=0
#upgrades the user partition with userdata.tar.xx.  (an unforseen preventive condition)
#strategy is copyover unless WIPETHEUSERDATA is set.
UPGRADETHEUSERDATAWHENNEWER=0
#forces the upgrade of the user partition. 
FORCEUPGRADETHEUSERDATA=0

#wipes the boot partition clean
#Note we don't "automatically" wipe the user, userdata, or boot partitions as those parititons hold userdata"
#if you want them wiped in the upgrade, you must call the following
WIPETHEBOOT=0
#upgrades the boot partition with boot.tar.xz.  (an unforseen preventive condition)
#strategy is copyover unless WIPETHEBOOT is set.
UPGRADETHEBOOTWHENNEWER=1
#upgrades the boot whenever there is a file different in the upgrade
UPGRADEKERNELWHENDIFFERENT=1
#upgrades the kernel whenever there is file size difference in the kernel
UPGRADETHEBOOTWHENDIFFERENT=0
#forces the upgrade of the boot partition. 
FORCEUPGRADETHEBOOT=0
#wipes the u-boot section clean
#Note we don't "automatically" wipe the u-boot.  This could be catestrophic unless you immedatly install a new-uboot
#Note we have tremendous success with just overwritting the uboot. so just do that usually
WIPETHEU_BOOT=0
#upgrades the uboot sector with the u-boot.bin located on the boot partitition
#strategy is copyover unless WIPETHEU_BOOT is set. (Which happens to be exteremly dangerous)
UPGRADETHEU_BOOTWHENNEWER=1
#forces the upgrade of the ubootf
FORCEUPGRADETHEU_BOOT=0
#Set the partition schema to be used.
PARTITIONSCHEMA=2
#if the partition schema does not match, the following flag will upgrade the partition schema, and will also 
#set the Factory Upgrade to force and Upgrade partition to force automatically because those partitions are wiped 
#during a re-schema
REPARTITIONEMMC=1

#---------------------------------------------------------------------------------------------------------------------------
# basic utils (common tools)
#---------------------------------------------------------------------------------------------------------------------------

#Function for parsing command line options with "=" in them
#/	Ver:	.1
#/	$1:		string
#/	$2:		name1
#/	$3:		name1
#/	Out:	get_opt("init=/sbin/init") will return "/sbin/init"
get_opt() {
	echo "$@" | cut -d "=" -f 2
}

#/	Desc:	makes a full path directory
#/	Ver:	.1
#/	$1:		path
#/	Expl:	mkdirectory /tmp/overlay/include
mkdirectory() {
	if [[ ! -d $1 ]]; then
		mkdir -p $1
	fi
}

_filesizeDiff(){
	f1=$(wc -c $1  | awk '{print $1}');
	f2=$(wc -c $2 | awk '{print $1}');
	if [[ "$f1" = "$f2" ]]; then
		echo 0
	else
		echo 1
	fi
}

#---------------------------------------------------------------------------------------------------------------------------
# Core utils (tools specific to this script)
#---------------------------------------------------------------------------------------------------------------------------
#/	Desc:	makes a formated echo onto the screen
#/	Ver:	.1
#/	$1:		string with formating in it
#/	Out:	echo to screen
#/	Expl:	say_general "I love WigwWag"

say(){
	echo -en "$1:\t$2\n"
}
say_init(){
	say "dOS INIT" "\t$1"
}
say_button(){
	say "dOS RECOVERY" "$1"
}
say_upgradescript(){
	say "dOSupgradescript" "$1"
}

say_update(){
	say "dOS" "$1"
}

say_update2(){
	if [[ "$1" = "PARTITION" ]]; then
		say "dOS" "${CYAN}[PARTITION SCHEMA]${NORM}: $2"
	elif [[ "$1" = "FACTORY" ]]; then
		say "dOS" "${BLUE}[FACTORY PARTITION]${NORM}: $2"
	elif [[ "$1" = "UPGRADE" ]]; then
		say "dOS" "${GREEN}[UPGRADE PARTITION]${NORM}: $2"
	elif [[ "$1" = "USER" ]]; then
		say "dOS" "${MAGENTA}[USER PARTITION]${NORM}: $2"
	elif [[ "$1" = "USERDATA" ]]; then
		say "dOS" "${YELLOW}[USERDATA PARTITION]${NORM}: $2"
	elif [[ "$1" = "BOOT" ]]; then
		say "dOS" "${MAGENTA}[BOOT PARTITION]${NORM}: $2"
	elif [[ "$1" = "BOOT-KERNEL" ]]; then
		say "dOS" "${MAGENTA}[BOOT-KERNEL]${NORM}: $2"
	elif [[ "$1" = "DATABASE" ]]; then
		say "dOS" "${WHITE}[DATABASE]${NORM}: $2"
	elif [[ "$1" = "UBOOT" ]]; then
		say "dOS" "${RED}[UBOOT]${NORM}: $2"
	else
		say "dOS-not-called-right" "$2"
	fi
}

say_update_sub(){
	echo -en "\t$1\n"
}

say_update_sub2(){
	echo -en "\t$1\n"
}

say_updatec(){
	case "$1" in 
		y) say_update "${YELLOW}$2${NORM}"; ;;
		#
		c) say_update "${CYAN}$2${NORM}"; ;;
		#
		r) say_update "${RED}$2${NORM}"; ;;
		#
		g) say_update "${GREEN}$2${NORM}"; ;;
		#
		b) say_update "${BLUE}$2${NORM}"; ;;
		#
		m) say_update "${Magenta}$2${NORM}"; ;;
		#
		w) say_update "${NORM}$2${NORM}"; ;;
esac
}

say_updatenoe (){
	echo  "dOS:$1"
}

say_general() {
	say_init "$1"
}
say_error() {
	say "dOS ERROR:" "\t$1"
}
_decho(){
	if [[ $debug -eq 1 ]]; then 
		#echo -en "deviceOS_debug:\t\t$1\n" > /dev/ttyS0
		echo -en "dOS DEBUG:\t\t$1\n"
	fi
}

_dechof(){
	_decho "function: $1"
}

evaldebug(){
	if [[ $debug -eq 1 ]]; then
		_decho "evaling [$1]"
		eval "$1"
	fi

}

#_general/	Desc:	sets the led color
#/	Ver:	.1
#/	$1:		red
#/	$2:		green
#/	$3:		blue
#/  $4:     sleeptime in units of beatcounters.  Each beat is .1, so to sleep for 1/2 sec, you need 5 beatcounters, so 5
#/	$5:		(alt red)
#/	$6:		(alt blue)
#/	$7:		(alt green)
#/	Out:	led color outputed
#/	Expl:	setLED 10 10 10 3 10 0 0 <-- blinks the led every 1/3 second
setLED() {
	echo "Setting LED here"
############################
#  No LED on Rpi - for now do nothing
############################

#	altred=0;
#	altblue=0;
#	altgreen=0;
#	ltime=0;
#	if [[ "$4" != "" ]]; then
#		ltime="$4"
#	fi
#	if [[ "$5" != "" ]]; then
#		altred="$5"
#	fi
#	if [[ "$6" != "" ]]; then
#		altgreen="$6"
#	fi
#	if [[ "$7" != "" ]]; then
#		altblue="$7"
#	fi
	
	#_decho "$1 $2 $3 [$ltime] $altred $altgreen $altblue > thepipe" 
	#2=RBG
#	if [[ $ledconfig -eq 2 ]]; then
#		echo "$1 $3 $2 $ltime $altred $altblue $altgreen" > /led.pipe
#	else
#		echo "$1 $2 $3 $ltime $altred $altgreen $altblue" > /led.pipe
#	fi
}

setLED_macro(){
	case $1 in
		"lm_expandTMPFS" )
			#
			setLED $LED_LCYAN 4 $LED_LORANGE
			;;
		#
		"lm_ugfactory")
			#
			setLED $LED_LCYAN 4 $LED_LBLUE
			;;
		#
		"lm_ugupgrade")
			#
			setLED $LED_LCYAN 4 $LED_LGREEN
			;;
		#
		"lm_ugboot")
			#
			setLED $LED_LCYAN 4 $LED_LPURPLE
			;;
		#
		"lm_uguser")
			#
			setLED $LED_LCYAN 4 $LED_LPINK
			;;	
		#
		"lm_uguserdata")
			#
			setLED $LED_LCYAN 4 $LED_LWHITE
			;;
		#
		"lm_wget")
			#
			setLED $LED_LPINK 8 $LED_LORANGE
			;;
		#
		"lm_uguboot")
			#
			setLED $LED_LCYAN 8 $LED_RED
			;;
		#
	esac
}
#legacy scripts may use color
color(){
	setLED $1 $2 $3
}



typeit(){
	foo="$1"
	slowdown="$2"
	enter="$3"
	for i in $(seq 1 ${#foo}); do
		echo -n "${foo:$i:1}"
		sleep $slowdown
	done
	if [[ $enter -eq 1 ]]; then
		echo ""
	fi
}
#/	Desc:	Displays a wigwag banner
#/	Ver:	.1
#/	Out:	banner
#/	Expl:	displayBanner
displayBanner(){
	_decho "func displayBanner"
	sleep 1
	clear

	echo "${LBLUE}"
	echo "#-----------------------------------------------------------------------------------------------------#"
	echo "#                                      ____      _ _                                                  #"
	echo "#                                     |  _ \ ___| (_) ___  _ __                                       #"
	echo "#                                     | |_) / _ \ | |/ _ \| '_ \                                      #"
	echo "#                                     |  __/  __/ | | (_) | | | |                                     #"
	echo "#                                     |_|   \___|_|_|\___/|_| |_|                                     #"
	echo "#                                                                                                     #"
	echo "#                   A unique combination of IoT connectivity and device management                    #"
	echo "#-----------------------------------------------------------------------------------------------------#"
	echo "${NORM}"
	typeit "   initRamfs initializer version:${LBLUE} "  .02 0
	typeit " $master_initscript_version.$master_sub_version" .02 0
	echo "${NORM}"
	sleep 1
}

funccall(){
	type $1 > /dev/null 2>&1
	if [[ $? -eq 0 ]]; then
		$1 "$@"
	fi
}

stopwatchdog(){
	say_button "stopping the watchdog and killing deviceOSWD"
	STOP_DEVICEOSWD_CMD="echo -e \"stop\" | socat unix-sendto:$WDKEEPALIVE STDIO"
	eval "$STOP_DEVICEOSWD_CMD"
	killall deviceOSWD
}

watchdog(){
	wtime=$1
	wdname=deviceOSWD
	wd=wigwag/system/bin/$wdname
	startit=0
	if [[ -e /deviceOSWD ]]; then
		say_init "killing the watchdog with a killall"
		killall deviceOSWD
		startit=1
	else
		_decho "mounting at watchdog"
		mountfactory_ro
		mountupgrade_ro
		mountboot_ro
		ls $bbmp_boot
		_decho "checking $bbmp_boot/$wd"
		if [ -e $bbmp_boot/$wdname ]; then
			_decho "located watchdog on $bbmp_boot"
			cp $bbmp_boot/$wdname /
			startit=1
		elif [ -e $bbmp_upgrade/$wd ]; then
			_decho "located watchdog on $bbmp_upgrade"
			cp $bbmp_upgrade/$wd /
			startit=1
		elif  [ -e $bbmp_factory/$wd ]; then
			_decho "located watcdog on $bbmp_factory"
			cp $bbmp_factory/$wd /
			startit=1
		else
			say_init "could not locate deviceOSWD..."
		fi
		umountfactory
		umountupgrade
		umountboot
	fi
	if [[ $startit -eq 1 ]]; then
		cd /
		say_init "enabling the watchdog with -w $wtime -d"
		#Watchdog is hw dependent and must be build specially for each platform
		#ignore errors until orperational
		mkdirectory $WDPID_PATH
		/deviceOSWD -w $wtime -d -s $WDKEEPALIVE -p $WDPID_PATH 2>/dev/null
	else
		say_error "watchdog not started"
	fi
}



#---------------------------------------------------------------------------------------------------------------------------
# utils  version info
#---------------------------------------------------------------------------------------------------------------------------


extractVersion(){
	file="$1"
	if [[ ! -f $file ]]; then
		echo 0.0.0
	else
		echo $(cat $file | grep version | tail -1 | awk '{ print $3 }' | sed s/\"//g | sed s/,//)
	fi
}


determinebuildversions(){
	mountfactory_ro
	mountupgrade_ro
	mountuser_ro
	current_factory_version=$(extractVersion $Vfactory)
	current_upgrade_version=$(extractVersion $Vupgrade)
	potential_next_factory_version=$(extractVersion /mnt/.overlay/user/slash/upgrades/factoryversions.json)
	potential_next_upgrade_version=$(extractVersion /mnt/.overlay/user/slash/upgrades/upgradeversions.json)
	_decho "the current_factory_version: $current_factory_version"
	_decho "current_upgrade_version: $current_upgrade_version"
	_decho "potential_next_factory_version: $potential_next_factory_version"
	_decho "potential_next_upgrade_version: $potential_next_upgrade_version"
	umountfactory
	umountuser
	umountupgrade
}

determineUbootVersions(){
	mountboot_ro
	availableUboot=$(cat /mnt/.boot/u-boot.bin | grep -a "WigWag-U-boot-version_id" | tail -1 | awk '{print $2}')
	if [[ "$availableUboot" = "" ]]; then
		availableUboot=0;
	fi
	dd if=/dev/mmcblk0 of=/uboot.img seek=8 bs=1024 count=100 >> /dev/null 2>&1
	currentUboot=$(cat /uboot.img | grep -a "WigWag-U-boot-version_id" | tail -1 | awk '{print $2}')
	if [[ "$currentUboot" = "" ]]; then
		currentUboot=0;
	fi
	_decho "currentUboot='$currentUboot'"
	_decho "availableUboot='$availableUboot'"
	rm /uboot.img
	umountboot
}




determineUbootBoard(){
	mountboot_ro
	dd if=/dev/mmcblk0 of=/uboot.img seek=8 bs=1024 count=100
	currentUbootBoard=$(cat /uboot.img | grep -a "WigWag-Board-Support" | tail -1 | awk '{print $2}')
	_decho "currentUbootBoard=$currentUbootBoard"
	rm /uboot.img
	umountboot
}



#---------------------------------------------------------------------------------------------------------------------------
# utils  disk managment
#---------------------------------------------------------------------------------------------------------------------------


mounttool(){
	mt="$1"
	dev="$2"
	mp="$3"
	mount|grep $dev > /dev/null 2>&1
	if [[ $? -eq 0 ]]; then
		#check if it is ro vs rw
		out=$(mount | grep $dev | awk '{print $6}' | awk -F ',' '{print $1}'| sed s/\(//)
		if [[ "$out" != "$mt" ]]; then
			say_general "remounting $dev as $mt"
			umount $dev
			mount -o $mt $dev $mp
		fi
	else
		mount -o $mt $dev $mp
	fi
}

umounttool(){
	dev="$1"
	mount|grep $dev > /dev/null 2>&1
	if [[ $? -eq 0 ]]; then
		umount $dev
	fi
}

#/	Desc:	mounts the boot partiton as read-write
mountboot_rw(){
	mounttool rw $dev_boot $bbmp_boot 
}
#/	Desc:	mounts the factory partition as read-write
mountfactory_rw() {
	mounttool rw $dev_factory $bbmp_factory
}

#/	Desc:	mounts the upgrade partition as read-write
mountupgrade_rw(){
	mounttool rw $dev_upgrade $bbmp_upgrade
}

#/	Desc:	mounts the user partition as read-write
mountuser_rw(){
	mounttool rw $dev_user $bbmp_user
}

#/	Desc:	mounts the userdata partition as read-write
mountuserdata_rw(){
	mounttool rw $dev_userdata $bbmp_userdata
}

#/	Desc:	mounts the sda1 drive as read-write
mountsda1_rw(){
	mounttool rw $dev_sda1 $bbmp_usb
}

#/	Desc:	mounts the sda1 drive as read-write
mountsdb1_rw(){
	mounttool rw $dev_sdb1 $bbmp_usb
}
#/	Desc:	mounts the boot partition as read-only
mountboot_ro(){
	mounttool ro $dev_boot $bbmp_boot
}

#/	Desc:	mounts the factory partition as read-only
mountfactory_ro() {
	mounttool ro $dev_factory $bbmp_factory
}

#/	Desc:	mounts the upgrade partition as read-only
mountupgrade_ro(){
	mounttool ro $dev_upgrade $bbmp_upgrade
}

#/	Desc:	mounts the user partition as read-only
mountuser_ro(){
	mounttool ro $dev_user $bbmp_user
}

#/	Desc:	mounts the userdata partition as read-only
mountuserdata_ro(){
	mounttool ro $dev_userdata $bbmp_userdata
}

#/	Desc:	mounts the sda1 drive as read-write
mountsda1_ro(){
	mounttool ro $dev_sda1 $bbmp_usb 2>/dev/null
}

#/	Desc:	mounts the sda1 drive as read-write
mountsdb1_ro(){
	mounttool ro $dev_sdb1 $bbmp_usb 2>/dev/null
}

#/	Desc:	unmounts the boot partition
umountboot(){
	umounttool $dev_boot
}

#/	Desc:	unmounts the factory partition
umountfactory() {
	umounttool $dev_factory
}

#/	Desc:	unmounts the upgrade partition
umountupgrade(){
	umounttool $dev_upgrade
}

#/	Desc:	unmounts the user partition
umountuser(){
	umounttool $dev_user
}

#/	Desc:	unmounts the userdata partition
umountuserdata(){
	umounttool $dev_userdata
}

#/	Desc:	unmounts the usb partition
umountsda1(){
	umounttool $dev_sda1
}

#/	Desc:	unmounts the usb partition
umountsdb1(){
	umounttool $dev_sdb1
}

#/	Desc:  unmounts all partitions
umountall() {
	umountboot
	umountfactory
	umountupgrade
	umountuser
	umountuserdata
}

#/	Desc:	mounts all parititons as read-write
mountall_rw() {
	mountboot_rw
	mountfactory_rw
	mountupgrade_rw
	mountuser_rw
	mountuserdata_rw
}

#/	Desc:	mounts all partitions as read-only
mountall_ro(){
	mountboot_ro
	mountfactory_ro
	mountupgrade_ro
	mountuser_ro
	mountuserdata_ro
}

#/	Desc:	xxx
#/	Ver:	.1
#/	$1:		name1
#/	$2:		name1
#/	$3:		name1
#/	Out:	xxx
#/	Expl:	xxx
wipe() {
	device=$1
	cd /
	device=$1
	if [[ "$device" = "$dev_boot" ]]; then
		if [[ $wasWiped_boot -eq 0 ]]; then
			say_update2 "BOOT" "wiping Boot: $device"
			umountboot
			mkfs.vfat -n "boot" -S 512 $device
			wasWiped_boot=1
		fi
	elif [[ "$device" = "$dev_user" ]]; then
		if [[ $wasWiped_user -eq 0 ]]; then
			say_update2 "USER" "wiping User: $device"
			umountuser
			mkfs.ext4 -F -i 4096 -L "user" $device
			mountuser_rw
			mkdir -p $bbmp_user_slash
			mkdir -p $bbmp_user_work
			umountuser
			wasWiped_user=1
		fi

	elif [[ "$device" = "$dev_upgrade" ]]; then
		if [[ $wasWiped_upgrade -eq 0 ]]; then
			umountupgrade
			say_update2 "UPGRADE" "wiping Upgrade: $device"
			mkfs.ext4 -F -i 4096 -L "upgrades" $device
			wasWiped_upgrade=1
		fi
	elif [[ "$device" = "$dev_userdata" ]]; then
		if [[ $wasWiped_userdata -eq 0 ]]; then
			umountuserdata
			say_update2 "USERDATA" "wiping Userdata: $device"
			mkfs.ext4 -F -i 4096 -L "userdata" $device
			wasWiped_userdata=1;
		fi
	else
		if [[ $wasWiped_factory -eq 0 ]]; then
			umountfactory
			say_update2 "FACTORY" "wiping Factory: $device"
			mkfs.ext4 -F -i 4096 -L "factory" $device
			wasWiped_factory=1;
		fi
	fi
}

#/	Desc:	xxx
#/	Ver:	.1
#/	$1:		name1
#/	$2:		name1
#/	$3:		name1
#/	Out:	xxx
#/	Expl:	xxx
wipe_all(){
	wipe $dev_boot
	wipe $dev_factory
	wipe $dev_upgrade
	wipe $dev_user
	wipe $dev_userdata
}



ddbootsector(){
	_decho "entered the ddbootsector"
	cd /
	mountboot_ro
	cp /mnt/.boot/uboot.bin /
	umountboot
	say_update2 "UBOOT" "reapplying the uboot" 
	_decho "dd if=/uboot.bin of=/dev/mmcblk0 seek=8 bs=1024"
	dd if=/uboot.bin of=/dev/mmcblk0 seek=8 bs=1024
}

#---------------------------------------------------------------------------------------------------------------------------
# Reparititioning utils
#---------------------------------------------------------------------------------------------------------------------------

#/	Desc:	partitions a drive
#/	Ver:	.1
#/	$1:		the device to parititon
#/	Out:	none
#/	Expl:	partitionDrive  "/dev/mmcblk0"
partitionDrive(){
	TARGET="$1"
	PROTECTED="4096"
	BOOT_KiB="$(($BOOT_MiB*1024))"
	FACTORY_KiB="$(($FACTORY_MiB*1024))"
	UPGRADE_KiB="$(($UPGRADE_MiB*1024))"
	EXTENDED_PROTECTED_KiB="$(($EXTENDED_PROTECTED_MiB*1024))"
	USER_KiB="$(($USER_MiB*1024))"
	USERDATA_KiB="$(($USERDATA_MiB*1024))"
	BOOT_SECTORS="$(($BOOT_MiB*1024*2))"
	FACTORY_SECTORS="$(($FACTORY_MiB*1024*2))"
	UPGRADE_SECTORS="$(($UPGRADE_MiB*1024*2))"
	EXTENDED_PROTECTED_SECTORS="$(($EXTENDED_PROTECTED_MiB*1024*2))"
	USER_SECTORS="$(($USER_MiB*1024*2))"
	USERDATA_SECTORS="$(($USERDATA_MiB*1024*2))"
	BOOTs="$PROTECTED"
	BOOTe="$(( $BOOTs + $BOOT_SECTORS-1 ))"
	BOOTt="c"
	FACTORYs="$((BOOTe+1))"
	FACTORYe="$(($FACTORYs + FACTORY_SECTORS-1))"
	FACTORYt="83"
	UPGRADEs="$((FACTORYe+1))"
	UPGRADEe="$(($UPGRADEs+$UPGRADE_SECTORS-1))"
	UPGRADEt="83"
	EXTENDEDs="$((UPGRADEe+1))"
	EXTENDEDe="$(($EXTENDEDs+$EXTENDED_PROTECTED_SECTORS-1))"
	EXTENDEDt="f"
	USERs="$(($EXTENDEDs+2048))"
	USERe="$(($USERs+$USER_SECTORS-1))"
	USERt="83"
	USERDATAs="$(($USERe+2048+1))"
	USERDATAe="$(($USERDATAs+$USERDATA_SECTORS-1))"
	USERDATAt="83"
	OVERALL_SECTORS=$EXTENDEDe
	OVERALL_KiB=$(($OVERALL_SECTORS / 2))
	OVERALL_MiB=$(($OVERALL_KiB / 1024 ))
	#OVERALL_KiB="$(((((($USERDATAe+1))/2))+16384))"
	OVERALL_BS="$(($OVERALL_KiB*1024))"
	SDIMG_SIZE="$((expr ${BOOT_SPACE_ALIGNED} + ${ROOT_PART_SIZE} + ${READONLY_SIZE} + ${USER_DATA_PART_SIZE} + ${CUSTOM_SIZE} + 16384))"
	PART1="$BOOTs"
	PART2="$FACTORYs"
	PART3="$UPGRADEs"
	PART5="$USERs"
	PART6="$USERDATAs"
	BOOTx="o\nn\np\n1\n$BOOTs\n$BOOTe\n"
	FACTORYx="n\np\n2\n$FACTORYs\n$FACTORYe\n"
	UPGRADEx="n\np\n3\n$UPGRADEs\n$UPGRADEe\n"
	EXTENDEDx="n\ne\n$EXTENDEDs\n$EXTENDEDe\n"
	USERx="n\n$USERs\n$USERe\n"
	USERDATAx="n\n$USERDATAs\n$USERDATAe\n"
	BOOTy="t\n1\n$BOOTt\n"
	FACTORYy="t\n2\n$FACTORYt\n"
	UPGRADEy="t\n3\n$UPGRADEt\n"
	EXTENDEDy="t\n4\n$EXTENDEDt\n"
	USERy="t\n5\n$USERt\n"
	USERDATAy="t\n6\n$USERDATAt\n"
	BOOTz="a\n1\nw\n"
	cmd="$BOOTx$FACTORYx$UPGRADEx$EXTENDEDx$USERx$USERDATAx$BOOTy$FACTORYy$UPGRADEy$USERy$USERDATAy$BOOTz"
	
	if [[ 1 -eq 0 ]]; then
		echo "BOOTs $BOOTs"
		echo "BOOTe $BOOTe"
		echo "FACTORY $FACTORYs"
		echo "FACTORY $FACTORYe"
		echo "UPGRADEs $UPGRADEs"
		echo "UPGRADEe $UPGRADEe "
		echo "EXTENDEDs $EXTENDEDs"
		echo "EXTENDEDe $EXTENDEDe"
		echo "USERs $USERs"
		echo "USERe $USERe"
		echo "USERDATAs $USERDATAs"
		echo "USERDATAe $USERDATAe"
		echo "OVERALL_MiB" "$OVERALL_MiB"
		echo "OVERALL_KiB" "$OVERALL_KiB"
		echo "OVERALL_BS" "$OVERALL_BS"
		echo "PART1 $PART1"
		echo "PART2 $PART2"
		echo "PART3 $PART3"
		echo "PART5 $PART5"
		echo "PART6 $PART6"
	fi
	say_updatenoe "repartitioning command: $cmd"
	echo "$cmd"
	echo -e "$cmd" | /sbin/fdisk -u $TARGET
	sync
}

#/	Desc:	xxx
#/	Ver:	.1
#/	$1:		name1
#/	$2:		name1
#/	$3:		name1
#/	Out:	xxx
#/	Expl:	xxx
backupVitals(){
	say_update "backing up vital system information"
	cd /tmpfs
	mountall_ro
	cp -a $bbmp_boot /tmpfs/
	cp -a $bbmp_userdata /tmpfs/
	sync
	backupVitals2 #this call is here to call a function named the same thing in the upgrade.sh utility if needed
	umountall
}

#/	Desc:	xxx
#/	Ver:	.1
#/	$1:		name1
#/	$2:		name1
#/	$3:		name1
#/	Out:	xxx
#/	Expl:	xxx
restoreVitals(){
	say_update "restoring vial system information"
	cd /
	mountall_rw
	cp -a /tmpfs/.boot /mnt/
	_decho "ls -al #2 $bbmp_boot"
	#ls -al $bbmp_boot
	cp -a /tmpfs/userdata /mnt/.overlay/
	_decho "ls -al #2  $bbmp_userdata"
	#ls -al $bbmp_userdata/
	sync
	umountall
	sync
}

#/	Desc:	xxx
#/	Ver:	.1
#/	$1:		name1
#/	$2:		name1
#/	$3:		name1
#/	Out:	xxx
#/	Expl:	xxx
copydown(){
	from_name=$1
	to_name=$2
	erase=$3
	ready=1
	if [[ "$from_name" = "user" ]]; then
		from="$bbmp_user_slash"
	elif [[ "$from_name" = "upgrade" ]]; then
		from="$bbmp_upgrade"
	elif [[ "$from_name" = "factory" ]]; then
		from="$bbmp_factory"
	else
		ready=0
	fi
	if [[ "$to_name" = "user" ]]; then
		to="$bbmp_user_slash"
	elif [[ "$to_name" = "upgrade" ]]; then
		to="$bbmp_upgrade"
	elif [[ "$to_name" = "factory" ]]; then
		to="$bbmp_factory"
	else
		ready=0
	fi
	if [[ "$erase" = "" ]]; then
		ready=0
	fi
	if [[ "$ready" -eq 1 ]]; then
		say_update "Copying the $from/. to the $to"
		cp -ar $from/. $to
		if [[ "$erase" -eq 1 ]]; then
			rm -rf $from/*
			rm -rf $from/.*
			rm -rf $from/*.*
		fi
	else
		echo -e "Useage $0 [from_directory] [to_directory] [erase from_directory]"	  
		echo -e "Useage $0 [user|upgrade|factory] [user|upgrade|factory] [0|1]"
	fi
}

#---------------------------------------------------------------------------------------------------------------------------
# Upgrading
#---------------------------------------------------------------------------------------------------------------------------

erase_UPGRADEFILES(){
	if [[ $ERASETHEUPGRADEFILES -eq 1 ]]; then
		mountuser_rw
			# rm -rf $UGscript
			# rm -rf $UGtarball
	  #   	rm -rf $UGscript
	  #   	rm -rf $UGdir/factoryversions.json
	  #   	rm -rf $UGdir/upgradeversions.json
	  rm -rf $UGdir/*
	  rm -rf $UGdir/.*
	  umountuser
	fi
}

#/	Desc:	xxx
#/	Ver:	.1
#/	$1:		name1
#/	$2:		name1
#/	$3:		name1
#/	Out:	xxx
#/	Expl:	xxx
#check for messages from the host OS such as an upgrade
#factory nuke
#anything
check_OSmessages() {
	say_update "Checking for messages from DeviceOS"
	cd /
	umountall
	mountuser_ro
	if [[ -e $UGscript ]]; then
		say_update "Received an upgrade message from the OS"
		watchdog 1200
	#if [[ -e $UGscript  && -e $UGtarball ]]; then
		cp $UGscript /
		source /upgrade.sh
		_decho "DONE: sourced /upgrade.sh, lets erase the upgrade file $ERASETHEUPGRADEFILES"
		erase_UPGRADEFILES
		rebootit
	else
		umountuser
		say_update "No messages from DeviceOS"
	fi
	_decho "done with check_OSmessages"
}

#/	Desc:	xxx
#/	Ver:	.1
#/	$1:		name1
#/	$2:		name1
#/	$3:		name1
#/	Out:	xxx
#/	Expl:	xxx
#check for messages from the host OS such as an upgrade
#factory nuke
#anything
check_USBmessages() {
	say_update "Checking for messages from the USB"
	cd /
	thesda="no /dev/sd*"
	umountall
	mdev -s
	sync
	evaldebug "ls -al /dev/s*"
	if [[ -e $dev_sda1 ]]; then
		mountsda1_ro
		_decho "insda searching $UGwwusbmsg"
		if [[ -e $UGwwusbmsg ]]; then
			cp $UGwwusbmsg /
			thesda=$dev_sda1
		fi
		umountsda1
	fi
	if [[ -e $dev_sdb1 ]]; then
		mountsdb1_ro
		if [[ -e $UGwwusbmsg ]]; then
			cp $UGwwusbmsg /
			thesda=$dev_sdb1
		fi
		umountsdb1
	fi
	_decho "the usb is found at $thesda"
	if [[ "$thesda" != "no /dev/sd*" ]]; then
		haveusbmsg=1
		watchdog 600
		source /wwusbmsg.sh
		_decho "DONE: sourced /wwusbmsg.sh, lets erase the upgrade file $ERASETHEUPGRADEFILES"
		if [[ $ERASETHEUPGRADEFILES -eq 1 ]]; then
			if [[ $thesda = $dev_sda1 ]]; then
				mountsda1_rw
				rm -rf $UGwwusbmsg
				umountsda1
			else
				mountsdb1_rw
				rm -rf $UGwwusbmsg
				umountsdb1
			fi
		fi
		rebootit
	else
		say_update "No messages from the USB available"
	fi
	_decho "done with check_USBmessages"
}


#accepts a version number in the format of x.y.z and compares them
isGreaterThan(){
	a="$1"
	b="$2"
	a=$(echo $a | sed -e "s/\.//g");
	b=$(echo $b | sed -e "s/\.//g");
	if [[ $a -gt $b ]]; then
		echo 1
	else
		echo 0
	fi
}
#/	Desc:	tests if an upgrade should occur
#/	Ver:	.1
#/	$1:		base (whats installed)
#/	$2:		test (what you might want to install)
#/	$3:		name1
#/	Out:	if test is newer than base, returns doupgrade. otherwise returns noupgrade
#/	Expl:	xxx
testifupgradeshouldoccur(){
	base="$1"
	testing="$2"
	baseV=$(cat "$1" | grep version | tail -1 | awk '{ print $3 }' | sed s/\"//g | sed s/,//)
	testingV=$(cat "$2" | grep version | tail -1 | awk '{ print $3 }' | sed s/\"//g | sed s/,//)
	#dont do echos inside functions that return echos!
	#_decho "base=$baseV test=$testingV"
	if [[ "$baseV" != "" && "$testingV" != "" ]]; then
		if [[ "$baseV" = "$testingV" ]]; then
			echo "noupgrade"
		else
			baseVc=$(echo $baseV | sed -e "s/\.//g")
			testingVc=$(echo $testingV | sed -e "s/\.//g")
			if [[ $baseVc -lt $testingVc ]]; then 
				echo "doupgrade"
			else 
				echo "noupgrade"
			fi
		fi
	else
		echo "doupgrade"
	fi
}

#/	Desc:	xxx
#/	Ver:	.1
#/	$1:		name1
#/	$2:		name1
#/	$3:		name1
#/	Out:	xxx
#/	Expl:	xxx
expandTMPFS(){
	if [[ $wasTMPFS_built -eq 0 && ! -e /tmpfs ]]; then
		say_update "expanding the tempfs with LED ${CYAN}CYAN${NORM} & ${ORANGE}ORANGE${NORM}"
		setLED_macro "lm_expandTMPFS"
		cd /
		mountuser_ro
		mkdir /tmpfs;
		mount -t tmpfs -o size=$tempfsSize,mode=700 tmpfs /tmpfs
		_decho "expanding $UGtarball to /tmpfs"
		tar xzf $UGtarball -C /tmpfs
		wasTMPFS_built=1;
		umountuser
	else
		_decho "/tmpfs already exists"
	fi
}




#/	Desc:	xxx
#/	Ver:	.1
#/	$1:		name1
#/	$2:		name1
#/	$3:		name1
#/	Out:	xxx
#/	Expl:	xxx

dq(){
	mainmsg=$1
	fv=$2
	sv=$3
	color=$4
	if [[ $2 -eq $3 ]]; then
		msg="$2\t\t$3"
	else
		msg="$2\t\t$color$3${NORM}"
	fi
	say_update_sub "$1$msg"
}

displayUpgradeStrategy(){
	say_update_sub "${CYAN}Update Strategy\t\t\t\t${CYAN}SET\t\tDO IT${NORM}"
	say_update_sub "${MAGENTA}BOOT (1)${NORM}"
	dq "Wipe the boot partition?\t\t" $WIPETHEBOOT $WIPETHEBOOT "${MAGENTA}"
	dq "${WHITE}Upgrade the boot partition if new?\t" $UPGRADETHEBOOTWHENNEWER $UGBOOT_DOIT_ALL "${MAGENTA}"
	dq "Upgrade the boot partition if different\t" $UPGRADETHEBOOTWHENDIFFERENT $UGBOOT_DOIT_ALL "${MAGENTA}" 
	dq "${WHITE}Force upgrade the boot partition?\t" $FORCEUPGRADETHEBOOT $UGBOOT_DOIT_ALL "${MAGENTA}"
	dq "${WHITE}Upgrade the kernel if different?\t" $UPGRADEKERNELWHENDIFFERENT $UGBOOT_DOIT_KERNEL "${MAGENTA}"
	dq "${BLUE}FACTORY PARTITION (2)${NORM}"
	dq "${WHITE}Wipe the factory partition?\t\t" $WIPETHEFACTORY $WIPETHEFACTORY "${BLUE}"
	dq "${WHITE}Upgrade the factory partition if new?\t" $UPGRADETHEFACTORYWHENNEWER $UGFACTORY_DOIT_ALL "${BLUE}"
	dq "${WHITE}Force upgrade factory partition?\t" $FORCEUPGRADETHEFACTORY $UGFACTORY_DOIT_ALL "${BLUE}"
	dq "${GREEN}UPGRADE PARTITION (3)${NORM}"
	dq "${WHITE}Wipe the upgrade partition?\t\t" $WIPETHEUPGRADE $WIPETHEUPGRADE "${GREEN}"
	dq "${WHITE}Upgrade the upgrade partition if new?\t" $UPGRADETHEUPGRADEWHENNEWER $UGUPGRADE_DOIT_ALL "${GREEN}"
	dq "${WHITE}Force Upgrade the upgrade partition?\t" $FORCEUPGRADETHEUPGRADE $UGUPGRADE_DOIT_ALL "${GREEN}"
	dq "${MAGENTA}USER (5)${NORM}"
	dq "${WHITE}Wipe the user partition?\t\t" $WIPETHEUSER_PARTITION $WIPETHEUSER_PARTITION "${MAGENTA}"
	dq "${WHITE}Upgrade the user partition if new?\t" $UPGRADETHEUSER_PARTITIONWHENNEWER $UGUSER_DOIT_ALL "${MAGENTA}"
	dq "${WHITE}Force upgrade the user partition?\t" $FORCEUPGRADETHEUSER_PARTITION $UGUSER_DOIT_ALL "${MAGENTA}"
	dq "${YELLOW}USERDATA (6)${NORM}"
	dq "${WHITE}Wipe the userdata partition?\t\t" $WIPETHEUSERDATA $WIPETHEUSERDATA "${YELLOW}"
	dq "${WHITE}Upgrade the userdata partition if new?\t" $UPGRADETHEUSERDATAWHENNEWER $UGUSERDATA_DOIT_ALL "${YELLOW}"
	dq "${WHITE}Force Upgrade the userdata partition?\t" $FORCEUPGRADETHEUSERDATA $UGUSERDATA_DOIT_ALL "${YELLOW}"
	dq "${RED}UBOOT${NORM}"
	dq "${WHITE}Wipe the U-Boot?\t\t\t" $WIPETHEU_BOOT $WIPETHEU_BOOT "${RED}"
	dq "${WHITE}Upgrade the U-Boot if new?\t\t" $UPGRADETHEU_BOOTWHENNEWER $UGUBOOT_DOIT_ALL "${RED}"
	dq "${WHITE}Force Upgrade the U-Boot?\t\t" $FORCEUPGRADETHEU_BOOT $UGUBOOT_DOIT_ALL "${RED}"
	dq "${WHITE}OTHER${NORM}"
	dq "${WHITE}Repartition the EMMC?\t\t\t" $REPARTITIONEMMC $UGPARTITION_DO_IT_ALL "${GREEN}"
	
	funccall displayUpgradeStrategy2
}



Strategy_UGpartition(){
	_decho "expected partition scheme is $PARTITIONSCHEMA"
	case $PARTITIONSCHEMA in
		1) expectedsize=$BootPartitionOrginalSize;
		#
		;;
		2) expectedsize=128;
		#
		;;	
	esac
	bootParitionSize=$(fdisk -l /dev/mmcblk0p1 | xargs | awk '{print $3}');
	_decho "$bootParitionSize -ne $expectedsize && $REPARTITIONEMMC -eq 1"
	if [[ $bootParitionSize -ne $expectedsize && $REPARTITIONEMMC -eq 1 ]]; then
		UGPARTITION_DO_IT_ALL=1;
		FORCEUPGRADETHEFACTORY=1
		FORCEUPGRADETHEUPGRADE=1
		say_update2 "PARTITION" "update needed."
	else
		say_update2 "PARTITION" "update not needed."
	fi
}
UGpartition(){
	if [[ "$UGPARTITION_DO_IT_ALL" -eq 1 ]]; then
		say_update2 "PARTITION" "update started."
		expandTMPFS
		backupVitals
		partitionDrive /dev/mmcblk0
		wipe_all
		restoreVitals
	fi
}

#/	Desc:	xxx
#/	Ver:	.1
#/	$1:		name1
#/	$2:		name1
#/	$3:		name1
#/	Out:	xxx
#/	Expl:	xxx
Strategy_UGfactory(){
	if [[ "$FORCEUPGRADETHEFACTORY" -eq 1 ]]; then
		UGFACTORY_DOIT_ALL=1
		say_update2 "FACTORY" "update needed. (forced)"
	elif [[ "$UPGRADETHEFACTORYWHENNEWER" -eq 1 ]]; then
		mountfactory_ro
		mountuser_ro
		if [[ -e /mnt/.overlay/user/slash/upgrades/factoryversions.json ]]; then
			ftres=$(testifupgradeshouldoccur /mnt/.overlay/factory/wigwag/etc/versions.json /mnt/.overlay/user/slash/upgrades/factoryversions.json)
			umountfactory
			if [[ "$ftres" = "doupgrade" ]]; then
				UGFACTORY_DOIT_ALL=1
				say_update2 "FACTORY" "update needed. (current [$current_factory_version available] : [$potential_next_factory_version])"
			else
				say_update2 "FACTORY" "update not needed. (current [ $current_factory_version] available : [$potential_next_factory_version])"

			fi
		else 
			say_update2 "FACTORY" "will not update because factoryversions.json does not exist."
		fi
	fi
}
UGfactory(){
	if [[ "$WIPETHEFACTORY" -eq 1 ]]; then
		wipe $dev_factory
	fi
	if [[ "$UGFACTORY_DOIT_ALL" -eq 1 ]]; then
		say_update2 "FACTORY" "update started."
		expandTMPFS
		say_update2 "FACTORY" "LED Sechema ${CYAN}CYAN${NORM} & ${BLUE}BLUE${NORM}"
		setLED_macro "lm_ugfactory"
		cd /tmpfs
		wipe $dev_factory
		#writes the factory.tar.xz to the factory partition (upgrades it)
		mountfactory_rw
		say_update2 "FACTORY" "pushing factory.tar.xz to $bbmp_factory"
		cd /tmpfs
		tar xJf factory.tar.xz -C $bbmp_factory/
		if [[ $? -eq 0 ]]; then
			say_update2 "FACTORY" "update ${GREEN}success${NORM}"
		else
			say_update2 "FACTORY" "update ${RED}failure${NORM}"
		fi
		cd /
		# a function call that can be hooked by the upgrade.sh script
		funccall UGfactory2
		umountfactory
		e2fsck -y $dev_factory
	fi
}



#o\nn\np\n1\n4096\n106495\nn\np\n2\n106496\n5226495\nn\np\n3\n5226496\n9322495\nn\ne\n9322496\n13438975\nn\n9324544\nn
#/	Desc:	xxx
#/	Ver:	.1
#/	$1:		name1
#/	$2:		name1
#/	$3:		name1
#/	Out:	xxx
#/	Expl:	xxx
Strategy_UGupgrade(){
	if [[ "$FORCEUPGRADETHEUPGRADE" -eq 1 ]]; then
		UGUPGRADE_DOIT_ALL=1
		say_update2 "UPGRADE" "udate needed. (forced)"
	elif [[ "$UPGRADETHEUPGRADEWHENNEWER" -eq 1 ]]; then
		mountupgrade_ro
		mountuser_ro
		if [[ -e /mnt/.overlay/user/slash/upgrades/upgradeversions.json ]]; then
			utres=$(testifupgradeshouldoccur /mnt/.overlay/upgrade/wigwag/etc/versions.json /mnt/.overlay/user/slash/upgrades/upgradeversions.json)
			_decho "utres $utres"
			umountupgrade
			if [[ "$utres" = "doupgrade" ]]; then
				say_update2 "UPGRADE" "update needed. (current [$current_upgrade_version] : available [$potential_next_upgrade_version])"
				UGUPGRADE_DOIT_ALL=1
			else 
				say_update2 "UPGRADE" "update not needed. (current [$current_upgrade_version] : available [$potential_next_upgrade_version])"
				
			fi
		else 
			say_update2 "UPGRADE" "will not update because upgradeversions.json does not exist."
		fi
	fi
}
UGupgrade(){
	if [[ "$WIPETHEUPGRADE" -eq 1 ]]; then
		wipe $dev_upgrade
	fi
	if [[ "$UGUPGRADE_DOIT_ALL" -eq 1 ]]; then
		say_update2 "UPGRADE" "update started."
		expandTMPFS
		say_update2 "UPGRADE" "LED Sechema ${CYAN}CYAN${NORM} & ${GREEN}GREEN${NORM}"
		setLED_macro "lm_ugupgrade"
		wipe $dev_upgrade
		cd /tmpfs
		mountupgrade_rw
		say_update2 "UPGRADE" "pushing upgrade.tar.xz to $bbmp_upgrade"
		cd /tmpfs
		tar xJf upgrade.tar.xz -C $bbmp_upgrade/
		if [[ $? -eq 0 ]]; then
			say_update2 "UPGRADE" "update ${GREEN}success${NORM}"
		else
			say_update2 "UPGRADE" "update ${RED}failure${NORM}"
		fi
		cd /
		# a function call that can be hooked by the upgrade.sh script
		funccall UGupgrade2
		umountupgrade
		e2fsck -y $dev_upgrade
	fi
}

#/	Desc:	xxx
#/	Ver:	.1
#/	$1:		name1
#/	$2:		name1
#/	$3:		name1
#/	Out:	xxx
#/	Expl:	xxx
Strategy_UGuser(){
	if [[ "$UPGRADETHEUSER_PARTITIONWHENNEWER" -eq 1 ]]; then
		UGUSER_DOIT_ALL=1
		say_update2 "USER" "updated needed. (partition is always newer)"
	fi
	if [[ "$FORCEUPGRADETHEUSER_PARTITION" -eq 1 ]]; then
		UGUSER_DOIT_ALL=1
		say_update2 "USER" "update needed. (forced)"
	fi
	if [[ $UGUSER_DOIT_ALL -ne 1 ]]; then
		say_update2 "USER" "update not needed." 
	fi
}
UGuser(){
	if [[ "$WIPETHEUSER_PARTITION" -eq 1 ]]; then
		wipe $dev_user
	fi
	if [[ "$UGUSER_DOIT_ALL" -eq 1 ]]; then
		say_update2 "USER" "update started."
		expandTMPFS
		say_update2 "USER" "LED Sechema ${CYAN}CYAN${NORM} & ${MAGENTA}PINK${NORM}"
		setLED_macro "lm_uguser"
		cd /tmpfs
		mountuser_rw
		say_update2 "USER" "pushing user.tar.xz to $bbmp_user"
		cd /tmpfs
		tar xJf user.tar.xz -C $bbmp_user/
		if [[ $? -eq 0 ]]; then
			say_update2 "USER" "update ${GREEN}success${NORM}"
		else
			say_update2 "USER" "update ${RED}failure${NORM}"
		fi
		cd /
		# a function call that can be hooked by the upgrade.sh script
		funccall UGuser2
		umountuser
	fi
}

#/	Desc:	xxx
#/	Ver:	.1
#/	$1:		name1
#/	$2:		name1
#/	$3:		name1
#/	Out:	xxx
#/	Expl:	xxx
Strategy_UGuserdata(){
	_decho "c2"
	if [[ "$UPGRADETHEUSERDATAWHENNEWER" -eq 1 ]]; then
		UGUSERDATA_DOIT_ALL=1
		say_update2 "USERDATA" "update needed. (userdata always newer)" 
	fi
	_decho "c3"
	if [[ "$FORCEUPGRADETHEUSERDATA" -eq 1 ]]; then
		UGUSERDATA_DOIT_ALL=1
		say_update2 "USERDATA" "update needed. (forced)" 
	fi
	if [[ $UGUSERDATA_DOIT_ALL -ne 1 ]]; then
		say_update2 "USERDATA" "update not needed." 
	fi
}
UGuserdata(){
	if [[ "$WIPETHEUSERDATA" -eq 1 ]]; then
		wipe $dev_userdata
	fi
	_dechof "UGuserdata"
	if [[ "$UGUSERDATA_DOIT_ALL" -eq 1 ]]; then
		say_update2 "USERDATA" "updating started."
		expandTMPFS
		say_update2 "USERDATA" "LED Sechema ${CYAN}CYAN${NORM} & ${YELLOW}YELLOW${NORM}"
		setLED "lm_uguserdata"
		cd /tmpfs
		mountuserdata_rw
		say_update2 "USERDATA" "pushing userdata.tar.xz to $bbmp_userdata"
		cd /tmpfs
		tar xJf userdata.tar.xz -C $bbmp_userdata/
		if [[ $? -eq 0 ]]; then
			say_update2 "USERDATA" "update ${GREEN}success${NORM}"
		else
			say_update2 "USERDATA" "update ${RED}failure${NORM}"
		fi
		cd /
		# a function call that can be hooked by the upgrade.sh script
		funccall UGuserdata2
		umountuserdata
	fi
}




#/	Desc:	xxx
#/	Ver:	.1
#/	$1:		name1
#/	$2:		name1
#/	$3:		name1
#/	Out:	xxx
#/	Expl:	xxx
Strategy_UGboot(){
	mountboot_ro
	mountuser_ro
	if [[ "$FORCEUPGRADETHEBOOT" -eq 1 ]]; then
		UGBOOT_DOIT_ALL=1
		say_update2 "BOOT" "update needed. (forced)"
	else
		if [[ "$UPGRADETHEBOOTWHENNEWER" -eq 1 ]]; then
			if [[ ! -e /mnt/.boot/version ]]; then
				UGBOOT_DOIT_ALL=1
				say_update2 "BOOT" "update needed. (boot version DNE.)"
			else
				source /mnt/.boot/version
				currentbootversion=$bootversion
				if [[ ! -e /mnt/.overlay/user/slash/upgrades/bootversion.sh ]]; then
					bootversion=1;
				else
					source /mnt/.overlay/user/slash/upgrades/bootversion.sh
				fi
				if [[ $currentbootversion -lt $bootversion ]]; then
					UGBOOT_DOIT_ALL=1
					say_update2 "BOOT" "update needed. (current version $currentbootversion -lt new version $bootversion)"
				fi

			fi
		fi
		if [[ "$UPGRADETHEBOOTWHENDIFFERENT" -eq 1 ]]; then
			expandTMPFS
			mkdir /tempboot
			tar xJf /tmpfs/boot.tar.xz -C /tempboot 
			list="zImage boot.scr version deviceOSWD sun*.dtb initramfs.img"
			for i in $list; do
				_decho "_filesizeDiff $bbmp_boot/$i /tempboot/$i)"
				#
				if [[ ! -e /tempboot/$i ]]; then
					check=0;
				else
					check=$(_filesizeDiff $bbmp_boot/$i /tempboot/$i)
				fi
				_decho "result is bigger?: $check"
				if [[ $check -eq 1 ]]; then
					_decho "UGBOOT_DOIT_ALL is set due to a file size difference"
					say_update2 "BOOT" "update needed. (a file size difference)"
					UGBOOT_DOIT_ALL=1
				fi
			done
		fi
		if [[ "$UPGRADEKERNELWHENDIFFERENT" -eq 1 ]]; then
			expandTMPFS
			if [[ ! -e /temp/boot/zImage ]]; then
				check=0
			else
				check=$(_filesizeDiff $bbmp_boot/zImage /tempboot/zImage)
				if [[ $check -eq 1 ]]; then
					say_update2 "BOOT-KERNEL" "update needed (kernel different filesize)"
					UGBOOT_DOIT_KERNEL=1;	
				fi
			fi
		fi
	fi
	umountboot
	umountuser
	if [[ $UGBOOT_DOIT_ALL -eq 1 ]]; then
		say_update2 "BOOT" "update needed."
	elif [[ $UGBOOT_DOIT_KERNEL -eq 1 ]]; then
		say_update2 "BOOT" "update not needed."
		say_update2 "BOOT-KERNEL" "update needed."
	else
		say_update2 "BOOT" "update not needed."
		say_update2 "BOOT-KERNEL" "update not needed."
	fi
}
UGboot(){
	if [[ "$WIPETHEBOOT" -eq 1 ]]; then
		mountboot_ro
		tar -cvzf ssl.tar.gz $bbmp_boot/.ssl
		umountboot
		wipe $dev_boot
		mountboot_rw
		tar -xvzf ssl.tar.gz -C $bbmp_boot
		umountboot
	fi
	if [[ "$UGBOOT_DOIT_ALL" -eq 1 ]]; then
		expandTMPFS
		say_update2 "BOOT" "update started."
		say_update2 "BOOT" "LED Sechema ${CYAN}CYAN${NORM} & ${MAGENTA}MAGENTA${NORM}"
		setLED "lm_ugboot"
		cd /tmpfs
		mountboot_rw
		say_update2 "BOOT" "pushing boot.tar.xz to $bbmp_boot"
		cd /tmpfs
		tar xJf boot.tar.xz -C $bbmp_boot/
		if [[ $? -eq 0 ]]; then
			say_update2 "BOOT" "update ${GREEN}success${NORM}"
		else
			say_update2 "BOOT" "update ${RED}failure${NORM}"
		fi
		cd /
		# a function call that can be hooked by the upgrade.sh script
		funccall UGboot2
		umountboot
	elif [[ "$UGBOOT_DOIT_KERNEL" -eq 1 ]]; then
		expandTMPFS
		say_update2 "BOOT-KERNEL" "update started."
		say_update2 "BOOT-KERNEL" "LED Sechema ${CYAN}CYAN${NORM} & ${MAGENTA}MAGENTA${NORM}"
		_decho "LED ${CYAN}CYAN${NORM} ${MAGENTA} & PURPLE"
		setLED "lm_ugboot"
		cd /tmpfs
		mountboot_rw
		say_update2 "BOOT-KERNEL" "pushing zImage to $bbmp_boot"
		cd /tmpfs
		mkdir /tempboot
		tar xJf boot.tar.xz -C /tempboot/
		cd /tempboot
		cp zImage $bbmp_boot/
		if [[ $? -eq 0 ]]; then
			say_update2 "BOOT-KERNEL" "update ${GREEN}success${NORM}"
		else
			say_update2 "BOOT-KERNEL" "update ${RED}failure${NORM}"
		fi
		cd /
		# a function call that can be hooked by the upgrade.sh script
		funccall UGboot2
		umountboot
	fi

}


#/	Desc:	xxx
#/	Ver:	.1
#/	$1:		name1
#/	$2:		name1
#/	$3:		name1
#/	Out:	xxx
#/	Expl:	xxx
Strategy_UGU_boot(){
	if [[ "$FORCEUPGRADETHEU_BOOT" -eq 1 ]]; then
		UGUBOOT_DOIT_ALL=1
	elif [[ "$UPGRADETHEU_BOOTWHENNEWER" -eq 1 ]]; then
		determineUbootVersions
		if [[ $availableUboot -gt $currentUboot ]]; then
			UGUBOOT_DOIT_ALL=1
			say_update2 "UBOOT" "update needed (current [$currentUboot] : avaiable [$availableUboot])"
		fi
	fi
}
UGU_boot(){
	if [[ "$WIPETHEU_BOOT" -eq 1 ]]; then
		dd if=/dev/zero of=$dev_maindisk bs=1024 seek=8 count=100
		say_update2 "UBOOT" "update needed. (wipe forced)"
		UGUBOOT_DOIT_ALL=1
	fi
	if [[ "$UGUBOOT_DOIT_ALL" -eq 1 ]]; then
		cd /
		mountboot_ro
		_decho "u-boot flashing"
		say_update2 "UBOOT" "LED Sechema ${CYAN}CYAN${NORM} & ${RED}RED${NORM}"
		setLED "lm_uguboot"
		dd if=$bbmp_boot/u-boot.bin of=$dev_maindisk bs=1024 seek=8
		if [[ $? -eq 0 ]]; then
			success=1
			say_update2 "UBOOT" "flashing ${GREEN}success${NORM}"
		else
			say_update2 "UBOOT" "flashing ${RED}failure${NORM}"
		fi
		funccall UGU_boot2
		umountboot
	fi
}


UGDByaml_path=$bbmp_user_slash_wigwag"/etc/devicejs"
UGDByaml_file=$bbmp_user_slash_wigwag"/etc/devicejs/devicedb.yaml"
UGDBcerts=$bbmp_user_slash_wigwag"/devicejs-core-modules/Runner/.ssl"
UGDBlegacy=$bbmp_userdata"/etc/devicejs/db"
UGDBlegacynewhome=$bbmp_userdata"/etc/devicejs/olddb"
UGDBlegacyuntouched=$bbmp_userdata"/etc/devicejs/olddb_true"
UGDBcurrent=$UGDBlegacy
UGdatabase(){
	devicedb20version=1.1.300
	runSSL="/mnt/.overlay/user/slash/wigwag/devicejs-core-modules/Runner/.ssl"
	cd /
	mountboot_ro
	mountuserdata_rw
	mountupgrade_ro
	mountfactory_ro
	mountuser_rw
	if [[ -d $UGlegacy ]]; then
		#current_factory_version=1.0.10
		#current_upgrade_version=0.0.0
		oldfv=$current_factory_version;
		olduv=$current_upgrade_version;
		_decho "old factory version:$oldfv"
		_decho "old upgrade version:$olduv"
		ov=$(isGreaterThan $current_factory_version $current_upgrade_version)
		if [[ $ov -eq 1 ]]; then
			oldrun=$current_factory_version
		else
			oldrun=$current_upgrade_version
		fi
		_decho "our old running envirnement is $oldrun"
		ove=$(isGreaterThan $oldrun $devicedb20version)
		if [[ $ove -eq 1 ]]; then
			oldrundb=2
		else
			oldrundb=1
		fi
		_decho "our old running database is version $oldrundb"
		determinebuildversions
		cd /
		mountboot_ro
		mountuserdata_rw
		mountupgrade_ro
		mountfactory_ro
		mountuser_rw
		#current_upgrade_version=1.1.301
		nv=$(isGreaterThan $current_factory_version $current_upgrade_version)
		if [[ $nv -eq 1 ]]; then
			newrun=$current_factory_version
		else
			newrun=$current_upgrade_version
		fi
		_decho "our new running envirnement is $newrun"
		nve=$(isGreaterThan $newrun $devicedb20version)
		if [[ $nve -eq 1 ]]; then
			newrundb=2
		else
			newrundb=1
		fi
		_decho "our new running database is version $newrundb"
		#undo
		#oldrundb=1
		#newrundb=2
		#lets upgrade that db if its diff
		if [[ $oldrundb -eq 1 && $newrundb -eq 2 ]]; then
			_decho "inside the database update"
			ln -s $bbmp_user_slash_wigwag /wigwag
			ln -s $bbmp_userdata /userdata
			if [[ ! -d $runSSL ]]; then
				mkdir -p "$runSSL"
				cp /mnt/.boot/.ssl/* $runSSL/
			fi
			if [[ ! -e $UGDBcerts/ca-chain.cert.pem ]]; then
				cat $UGDBcerts/ca.cert.pem > $UGDBcerts/ca-chain.cert.pem
				cat $UGDBcerts/intermediate.cert.pem >> $UGDBcerts/ca-chain.cert.pem
			fi
			say_update2 "DATABASE" "Upgrading database..."
			_decho "mv $UGDBlegacy $UGDBlegacynewhome"
			#mv $UGDBlegacy $UGDBlegacynewhome
			#mkdir -p $UGDBlegacy
			cp -r $UGDBlegacy $UGDBlegacynewhome
			cp -r $UGDBlegacy $UGDBlegacyuntouched
			_decho "listing everything after the move."
			evaldebug "ls -al $UGDBlegacy/../"
			_decho "the $UGDBlegacy"
			evaldebug "ls -al $UGDBlegacy"
			_decho "the UGDBlegacynewhome"
			evaldebug "ls -al $UGDBlegacynewhome"
			_decho "the UGDBlegacyuntouched"
			evaldebug "ls -al $UGDBlegacyuntouched"
			_decho "mkdir -p $UGDByaml_path"
			mkdir -p $UGDByaml_path
			echo -e "db: /userdata/etc/devicejs/db" > $UGDByaml_file
			echo -e "port: 9000" >> $UGDByaml_file
			echo -e "syncSessionLimit: 2" >> $UGDByaml_file
			echo -e "syncSessionPeriod: 1000" >> $UGDByaml_file
			echo -e "syncPushBroadcastLimit: 0" >> $UGDByaml_file
			echo -e "gcInterval: 300000" >> $UGDByaml_file
			echo -e "gcPurgeAge: 600000" >> $UGDByaml_file
			echo -e "merkleDepth: 19" >> $UGDByaml_file
			echo -e "peers:" >> $UGDByaml_file
			echo -e "logLevel: info" >> $UGDByaml_file
			echo -e "cloud:" >> $UGDByaml_file
			echo -e "     noValidate: true" >> $UGDByaml_file
			echo -e "     host: devicedb1.wigwag.com" >> $UGDByaml_file
			echo -e "     port: 443" >> $UGDByaml_file
			echo -e "tls:" >> $UGDByaml_file
			echo -e "     clientCertificate: /wigwag/devicejs-core-modules/Runner/.ssl/client.cert.pem" >> $UGDByaml_file
			echo -e "     clientKey: /wigwag/devicejs-core-modules/Runner/.ssl/client.key.pem" >> $UGDByaml_file
			echo -e "     serverCertificate: /wigwag/devicejs-core-modules/Runner/.ssl/server.cert.pem" >> $UGDByaml_file
			echo -e "     serverKey: /wigwag/devicejs-core-modules/Runner/.ssl/server.key.pem" >> $UGDByaml_file
			echo -e "     rootCA: /wigwag/devicejs-core-modules/Runner/.ssl/ca-chain.cert.pem" >> $UGDByaml_file
			#cp /mnt/.overlay/factory/usr/bin/devicedb /device
			evaldebug "mount"
			evaldebug "ls /mnt/.overlay/factory/usr/bin/ | grep device"
			/mnt/.overlay/factory/usr/bin/devicedb upgrade -legacy=$UGDBlegacynewhome"/" -db=$UGDBcurrent"/" -conf=$UGDByaml_file
			#/mnt/.overlay/factory/usr/bin/devicedb upgrade -legacy=$UGDBcurrent"/" -db=$UGDBcurrent"/" -conf=$UGDByaml_file
			_decho "/mnt/.overlay/factory/usr/bin/devicedb upgrade -legacy=$UGDBlegacy"/" -db=$UGDBcurrent"/" -conf=$UGDByaml_file"
			_decho "rm -rf $UGDByaml_file"
			#undo
			#rm -rf $UGDByaml_file			
			evaldebug "cat $UGDByaml_file"
			_decho "listing everything after the move."
			evaldebug "ls -al $UGDBlegacy/../"
			_decho "the $UGDBlegacy"
			evaldebug "ls -al $UGDBlegacy"
			_decho "the UGDBlegacynewhome"
			evaldebug "ls -al $UGDBlegacynewhome"
			_decho "the UGDBlegacyuntouched"
			evaldebug "ls -al $UGDBlegacyuntouched"
		else
			say_update2 "DATABASE" "no database to upgrade"
		fi
	fi
	#umountall
}


STRATEGY_UPGRADE(){
	say_updatec y "Entering the upgrade process"
	say_updatec y "Determining current builds"
	determinebuildversions
	Strategy_UGpartition
	Strategy_UGfactory
	Strategy_UGupgrade
	Strategy_UGuser
	Strategy_UGuserdata
	Strategy_UGboot
	Strategy_UGU_boot
	displayUpgradeStrategy
}

EXECUTE_UPGRADE(){
	_decho "UPGRADE 1/8 partition"
	UGpartition
	_decho "UPGRADE 2/8 factory"
	UGfactory
	_decho "UPGRADE 3/8 upgrade"
	UGupgrade
	_decho "UPGRADE 4/8 user"
	UGuser
	_decho "UPGRADE 5/8 userdata"
	UGuserdata
	_decho "UPGRADE 6/8 boot"
	UGboot
	_decho "UPGRADE 7/8 u-boot"
	UGU_boot
	_decho "UPGRADE 8/8 database"
	UGdatabase
	_decho "UPGRADE DONE"
}

#/	Desc:	xxx
#/	Ver:	.1
#/	$1:		name1
#/	$2:		name1
#/	$3:		name1
#/	Out:	xxx
#/	Expl:	xxx
UPGRADE(){
	STRATEGY_UPGRADE
	EXECUTE_UPGRADE
}

#---------------------------------------------------------------------------------------------------------------------------
# INIT
#---------------------------------------------------------------------------------------------------------------------------

#/	Desc:	xxx
#/	Ver:	.1
#/	$1:		name1
#/	$2:		name1
#/	$3:		name1
#/	Out:	xxx
#/	Expl:	xxx
initGPIO() {
###########################################
# This routine is hardware specific, disabling
# for now
###########################################

echo "initGPIO would run here"

#	ledconfig=$(eeprog -f /dev/i2c-1 0x50 -r 0x61:0x1)
#	if [[ "$ledconfig" != "2" ]]; then
#		_decho "led config does not equal 2, force to 1"
#		ledconfig=1;
#	fi
#	_decho "ledconfig is $ledconfig"
#	versiontemp=$(eeprog -f /dev/i2c-1 0x50 -r 0xA:0x5)
#	if [[ "$versiontemp" = "0.0.4" ]]; then
#		version=4
#	elif [[ "$versiontemp" = "0.0.9" ]]; then
#		version=7
#		dev_factory="/dev/mmcblk1p2"
#		dev_upgrade="/dev/mmcblk1p3"
#		dev_user="/dev/mmcblk1p4"
#	elif [[ "$versiontem"="0.0.8" ]]; then
#		version=8
#	else
#		version=0
#	fi
#	say_init "HardwareVersion: $versiontemp found."
#	echo 236 > /sys/class/gpio/export
#	echo 37 > /sys/class/gpio/export
#	echo 38 > /sys/class/gpio/export
#	echo out > /sys/class/gpio/gpio37/direction
#	echo out > /sys/class/gpio/gpio38/direction
#	SCLK=/sys/class/gpio/gpio38/value
#	SDATA=/sys/class/gpio/gpio37/value
#
#	lastbutton=$(cat /sys/class/gpio/gpio236/value)
#	if [[ $lastbutton -eq 0 ]]; then
#		bname="depressed"
#	fi
lastbutton=1
}

#/	Desc:	xxx
#/	Ver:	.1
#/	$1:		name1
#/	$2:		name1
#/	$3:		name1
#/	Out:	xxx
#/	Expl:	xxx
initSelectRootDev() {
	_dechof "finitSelectRootDev"
	P1="mmcblk0p1"
	P2="mmcblk0p2"
	P3="mmcblk0p3"
	P5="mmcblk0p5"
	P6="mmcblk0p6"
	dev_maindisk="mmcblk0"
	_decho "selecting root"	
	mdev -s
	sync
	if [[ -e /dev/mmcblk1 ]]; then
		#echo "mmcblk1 exists, lets see if its a wwbooter"
		sdboot=1
	fi
	if [[ -e $dev_sda1 ]]; then
		_decho "sda detected"
		mountsda1_ro
		if [[ -e $bbmp_usb/sun7i-a20-wigwagrelayv4.dtb ]]; then
			say_init "WWBOOTER found - /dev/sda1 (switching mountpoints)"
			P1="sda1"
			P2="sda2"
			P3="sda3"
			P5="sda5"
			P6="sda6"
			dev_maindisk="/dev/sda"
		fi
		umountsda1
	fi
	if [[ -e $dev_sdb1 ]]; then
		_decho "sdb detected"
		mountsdb1_ro
		if [[ -e $bbmp_usb/sun7i-a20-wigwagrelayv4.dtb ]]; then
			say_init "WWBOOTER found - /dev/sdb1 (switching mountpoints)"
			P1="sdb1"
			P2="sdb2"
			P3="sdb3"
			P5="sdb5"
			P6="sdb6"
			dev_maindisk="/dev/sdb"
		fi
		umountsdb1
	fi
	dev_boot="/dev/$P1"
	dev_factory="/dev/$P2"
	dev_upgrade="/dev/$P3"
	dev_user="/dev/$P5"
	dev_userdata="/dev/$P6"

	# if [[ -d $dev_boot && -d $dev_factory && -d $dev_upgrade && -d $dev_user ]]; then
	# 	emmcready=1
	# fi
}


#/	Desc:	xxx
#/	Ver:	.1
#/	$1:		name1
#/	$2:		name1
#/	$3:		name1
#/	Out:	xxx
#/	Expl:	xxx
initmkpaths() {
	mkdirectory $bbmp_user
	mkdirectory $bbmp_factory
	mkdirectory $bbmp_upgrade
	mkdirectory $bbmp_userdata
	mkdirectory $bbmp_boot
	mkdirectory $newr
	mkdirectory $bbmp_usb
}

#/	Desc:	xxx
#/	Ver:	.1
#/	$1:		name1
#/	$2:		name1
#/	$3:		name1
#/	Out:	xxx
#/	Expl:	xxx
initFS() {
	_decho "initFS is called"
	#Mount things needed by this script
        #for now, explicitly making /proc and /sys before mount - they instead should be part of the fs
        mkdir -p /proc
        mkdir -p /sys
        mount -t proc proc /proc
        mount -t sysfs sysfs /sys
	#mount -t devtmpfs devtmpfs /dev
	#Disable kernel messages from popping onto the screen
	echo 0 > /proc/sys/kernel/printk
	#Clear the screen
#	clear
	#Create device nodes: historical comment, adding mount -t devtmpfs fixed the need to do this
	mknod /dev/null c 1 3
	mknod /dev/tty c 5 0
	mknod /dev/sda b 8 0
	mknod /dev/sda1 b 8 1
	mknod /dev/sdb b 16 0
	mknod /dev/sdb1 b 16 1
	mdev -s
	sync
}

initLED() {
	say_init "Led not presetnt"
#	/led.sh &
#	/heartbeat.sh &
}

stopled(){
	_decho "func: stopled"
#	setLED 10 5 0
#	sleep .5
#	setLED 10 5 0
#	sleep .5
#	setLED 10 5 0
#	killall heartbeat.sh
#	setLED 10 5 0
#	sleep .5
#	killall led.sh
#	rm -rf /led.pipe
}

#---------------------------------------------------------------------------------------------------------------------------
# Button Programs aka "recovery"
#---------------------------------------------------------------------------------------------------------------------------
#/	Desc:	xxx
#/	Ver:	.1
#/	$1:		name1
#/	$2:		name1
#/	$3:		name1
#/	Out:	xxx
#/	Expl:	xxx
rebootit() {
	cd /
	umountall
	stopled
	killall udhcpc
	killall dropbear
	say_general "rebooting now..."
	watchdog 1
	#evaldebug "ps ax"
	reboot -f
}

wipeuser(){
	wipe $dev_user
	wipe $dev_userdata
}

factoryit(){
	wipe $dev_upgrade
	wipeuser
}

#gets the latest code from the cloud and installs it overtop, just like a upgrade x.x.xxx
cloudlatest(){
	wgetlatest
}

cloudfactory(){
	wipe $dev_upgrade
	wipe $dev_factory
	wipe $dev_user
	wipe $dev_userdata
	wgetlatest
	# FORCEUPGRADETHEBOOT=1
	# FORCEUPGRADETHEUBOOT=1
	# UPGRADE
	# erase_UPGRADEFILES
}

#http://www.killdisk.com/dod.htm
#https://www.marksanborn.net/howto/wiping-a-hard-drive-with-dd/
#does a 7-pass DoD 5220.22-M
DOD7(){
	for i in {1..7}; do
		tr '\0' '\377' < /dev/zero | dd bs=8b of=$dev_maindisk;
		dd if=/dev/urandom of=$dev_maindisk bs=8b conv=notrunc;
		dd if=/dev/zero of=$dev_maindisk bs=8b conv=notrunc;
		#todo destroy the eeprom here too
	done
}



#/	Desc:	xxx
#/	Ver:	.1
#/	$1:		name1
#/	$2:		name1
#/	$3:		name1
#/	Out:	xxx
#/	Expl:	xxx
do_state_command() {
	case $state in                                                                                                                                                                                                                                    
		"reboot")
#green
setLED $LED_reboot                                                                                                                                                                                                                       
rebootit                                                                                                                                                                                                         
;;                                                                                                                                                                                                                                    
"wipe_user")
#orange
setLED $LED_wipe_user                                                                                                                                                                                                                               
say_button "wipe user partition"
wipeuser
rebootit
;;                                                                                                                                                                                                                                    
"wipe_upgrade")
#magenta
setLED $LED_wipe_upgrade
say_button "wipe upgrade and user (back to factory)" 
factoryit
rebootit
;;                                                                                                                                                                                                                                    
"wipe_all_wget")
#pink
setLED $LED_wipe_all_wget
say_button "wipe_all_wget nuking your stuff"
cloudfactory
rebootit                                                                                                                                                                                    
;;  
"shell")
#blue
setLED $LED_shell
say_button "Falling to Shell"
shell "withmount"
;;
"remotepull")
#cyan
setLED $LED_remotepull
say_button "remote update wget-pull"
cloudlatest
rebootit
;;                                                                                                                                                                                                                             
esac 
}

#/	Desc:	xxx
#/	Ver:	.1
#/	$1:		name1
#/	$2:		name1
#/	$3:		name1
#/	Out:	xxx
#/	Expl:	xxx
#Reminder: This state table looks 1 step ahead.  your state is your current state.
button_up() {
	case $state in                                                                                                                                                                                                                               
		"starting")    
say_button  "${RED}red:\t\tpress the button to begin${NORM}"                                                                                                                                                                                                                            
setLED $LED_RED
state="pausedbegin"                                                                                                                                                                                                        
;;   
"pausedbegin")
say_button  "${GREEN}green:\t\treboot${NORM}"
setLED $LED_reboot
state="reboot"
;;                                                                                                                                                                                                                           
"reboot")                                                                                                                                                                                                                                   
say_button  "${ORANGE}orange:\t\twipe user${NORM}"                                                                                                                                                                                                                            
setLED $LED_wipe_user
state="wipe_user"                                                                                                                                                                                                                 
;;                                                                                                                                                                                                                                    
"wipe_user")                                                                                                                                                                                                                                 
say_button  "${MAGENTA}magenta:\twipe upgrade${NORM}"                                                                                                                                                                                                                            
setLED $LED_wipe_upgrade 
state="wipe_upgrade"                                                                                                                                                                                        
;;                                                                                                                                                                                                                                      
"wipe_upgrade")  
if [[ $devbuild -eq 1 || $buttoncount -gt $specialmodecount ]]; then
	say_button "${CYAN}cyan:\t\twipe all and wget last cloud available build${NORM}"                                                                                                                                                                                                                            
	setLED $LED_wipe_all_wget
	state="wipe_all_wget"                                  
else
	say_button "${GREEN}green:\t\treboot${NORM}"                                                                                                                                                                                                                            
	setLED $LED_reboot
	state="reboot"  
fi                                                                                                                                                                                                             
;;                                                                                                                                                                                                                                    
"wipe_all_wget")   
say_button "${LBLUE}blue:\t\tdeveloper: fall to busybox shell${NORM}"                                                                                                                                                                                                                            
setLED $LED_shell
state="shell"                                                                                                                                                                                     
;;
"shell")
say_button "${PINK}pink:\t\tjust wget the last cloud upgrade${NORM}"                                                                                                                                                                                                                            
setLED $LED_remotepull
state="remotepull"                                                                                                                                                                                     
;;
"remotepull")
say_button "${GREEN}green:\t\treboot${NORM}"                                                                                                                                                                                                                            
setLED $LED_reboot
state="reboot"	
esac
}

#/	Desc:	xxx
#/	Ver:	.1
#/	$1:		name1
#/	$2:		name1
#/	$3:		name1
#/	Out:	xxx
#/	Expl:	xxx
state_blink() {
	case $state in                                                                                                                                                                                                                                 
		"reboot")                                                                                                                                                                                                                                   
setLED $LED_reboot 1                                                                                                                                                                                                          
;;                                                                                                                                                                                                                                    
"wipe_user")                                                                                                                                                                                                                                 
setLED $LED_wipe_user 1                                                                                                                                                                   
;;                                                                                                                                                                                                                                    
"wipe_upgrade")                                                                                                                                                                                                                                
setLED $LED_wipe_upgrade 1                                                                                                                                                                                        
;;                                                                                                                                                                                                                                    
"wipe_all_wget")                                                                                                                                                                                                                                 
setLED $LED_wipe_all_wget 1                                                                                                                                                                                         
;;
"shell")                                                                                                                                                                                                                                 
setLED $LED_shell 1                                                                                                                                                                              
;;
"remotepull")                                                                                                                                                                                                                                 
setLED $LED_remotepull 1                                                                                                                                                                                 
;;                                                                                                                                                                                                                                    
esac
   # setLED $LED_OFF
}

#/	Desc:	xxx
#/	Ver:	.1
#/	$1:		name1
#/	$2:		name1
#/	$3:		name1
#/	Out:	xxx
#/	Expl:	xxx
wgetlatest(){
	_decho "LED ${CYAN}CYAN${NORM} & WHITE"
	setLED_macro "lm_wget"
	ipthis
	stopwatchdog
	_decho "func: wgetlatest"
	cd /
	mountuser_rw
	if [[ ! -e $UGdir ]]; then
		mkdir -p $UGdir
	fi
	sleep 2
	_decho "wget --no-check-certificate $latestimg -O $UGdir/latest.tar.gz"
	wget --no-check-certificate $latestimg -O $UGdir/latest.tar.gz
	_decho "tar -xvzf $UGdir/latest.tar.gz -C $UGdir/"
	tar -xvzf $UGdir/latest.tar.gz -C $UGdir/
	umountuser
	#rebootit
}

#/	Desc:	xxx
#/	Ver:	.1
#/	$1:		name1
#/	$2:		name1
#/	$3:		name1
#/	Out:	xxx
#/	Expl:	xxx
recovery() {
	state="starting"
	say_button "lets recover"
	lastbutton=$(cat /sys/class/gpio/gpio236/value)
	secnow=$(date +%s)
	event_start=$secnow
	secnow=$(date +%s)
	elapsed=$(( $secnow - $event_start ))
	buttonstate=0
	setLED $LED_RED #go red cause we entered
	locked=1;
	firstenteredlock=0;
	while [ $elapsed -lt $timout ]; do 
		secnow=$(date +%s)
		if [[ "$state" = "starting" || "$state" = "pausedbegin" ]]; then
			elapsed=0
		else
			elapsed=$(( $secnow - $event_start ))
		fi
		newbutton=$(cat /sys/class/gpio/gpio236/value)
		if [[ $newbutton -ne $lastbutton ]]; then
			lastbutton=$newbutton
			event_start=$secnow
			if [[ $newbutton -eq 1 ]]; then
				button_up
				buttoncount=$(( $buttoncount + 1 ))
			fi
			#echo "got a button, starting timer over ($elapsed)"
		fi
	done
	say_button "Go\tPress the button to execute OR pull the power to abort"
	while [ $locked -eq 1 ]; do
		if [[ $firstenteredlock -eq 0 ]]; then
			state_blink
			firstenteredlock=1;
		fi
		newbutton=$(cat /sys/class/gpio/gpio236/value)
		if [[ $newbutton -ne $lastbutton ]]; then
			lastbutton=$newbutton
			if [[ $newbutton -eq 1 ]]; then
				do_state_command
				locked=0
			fi
		fi	
	done
}

ipthis(){
	udhcpc -b -s /etc/udhcpc.conf
}
#/	Desc:	xxx
#/	Ver:	.1
#/	$1:		name1
#/	$2:		name1
#/	$3:		name1
#/	Out:	xxx
#/	Expl:	xxx
shell() {
	setLED $LED_shell
	if [[ "$1" = "withmount" ]]; then
		mountall_rw
	fi
	#This will only be run if the exec above failed
	say_button "dropping to a busybox shell"
	#ifup eth0
	say_button "backgrounding udhcpc"
	ipthis
	dropbear -R -E -a
	stopwatchdog
	export expandTMPFS="./init commandline expandTMPFS"
	export umountall="./init commandline umountall"
	export mountall_rw="./init commandline mountall_rw"
	export mountall_ro="./init commandline mountall_ro"
	export rebootit="./init commandline rebootit"
	export dev_factory="$dev_factory"
	export dev_upgrade="$dev_upgrade"
	export dev_user="$dev_user"
	export dev_boot="$dev_boot"
	export dev_userdata="$dev_userdata"
	export bbmp_factory="$bbmp_factory"
	export bbmp_upgrade="$bbmp_upgrade"
	export bbmp_user="$bbmp_user"
	export bbmp_boot="$bbmp_boot"
	export bbmp_userdata="$bbmp_userdata"
	#http://www.busybox.net/FAQ.html#job_control
	mdev -s
	say_button "pivot to shell"
	setsid sh -c 'exec sh </dev/ttyS0 >/dev/ttyS0 2>&1'
}

#---------------------------------------------------------------------------------------------------------------------------
# Passtrhough Booting
#---------------------------------------------------------------------------------------------------------------------------

#/	Desc:	xxx
#/	Ver:	.1
#/	$1:		name1
#/	$2:		name1
#/	$3:		name1
#/	Out:	xxx
#/	Expl:	xxx
pivotprep() {
	umountall
	if [[ $wasTMPFS_built -eq 1 ]]; then
		rebootit
	fi
	e2fsck -y $dev_user
	e2fsck -y $dev_userdata
	mountfactory_ro
	mountupgrade_ro
	mountuser_rw
	mountuserdata_rw
	mountboot_rw
	if [[ ! -d $bbmp_user_slash ]]; then
		mkdir -p $bbmp_user_slash
	fi
	if [[ ! -d $bbmp_user_work ]]; then
		mkdir -p $bbmp_user_work
	fi
	say_init "mount -t overlay -o lowerdir=$bbmp_upgrade:$bbmp_factory,upperdir=$bbmp_user_slash,workdir=$bbmp_user_work overlay $newr"
	mount -t overlay -o lowerdir=$bbmp_upgrade:$bbmp_factory,upperdir=$bbmp_user_slash,workdir=$bbmp_user_work overlay $newr
	mkdirectory $newr_factory
	mkdirectory $newr_upgrade
	mkdirectory $newr_user
	mkdirectory $newr_userdata
	mkdirectory $newr_boot
	mount --move $bbmp_upgrade $newr_upgrade
	mount --move $bbmp_factory $newr_factory
	mount --move $bbmp_user $newr_user
	mount --move $bbmp_userdata $newr_userdata
	mount --move $bbmp_boot $newr_boot	
}



#/	Desc:	xxx
#/	Ver:	.1
#/	$1:		name1
#/	$2:		name1
#/	$3:		name1
#/	Out:	xxx
#/	Expl:	xxx
fullpivot() {
	_decho "func: Full Pivot"
	if [[ -d $newr_user && -d $newr/etc ]]; then
	#Unmount all other mounts so that the ram used by
	#the initramfs can be cleared after switch_root
	echo 1 > /proc/sys/kernel/printk
	umount /sys /proc
	say_init "Switching to the new root"
	exec switch_root $newr "${init}"
fi
}

#---------------------------------------------------------------------------------------------------------------------------
# Main CLI work
#---------------------------------------------------------------------------------------------------------------------------

#/	Desc:	xxx
#/	Ver:	.1
#/	$1:		name1
#/	$2:		name1
#/	$3:		name1
#/	Out:	xxx
#/	Expl:	xxx
process_cli() {
#Process command line options
for i in $(cat /proc/cmdline); do
	case $i in
		root\=*)
root=$(get_opt $i)
;;
init\=*)
init=$(get_opt $i)
;;
esac
done
}

#/	Desc:	xxx
#/	Ver:	.1
#/	$1:		name1
#/	$2:		name1
#/	$3:		name1
#/	Out:	xxx
#/	Expl:	xxx
helpfunc() {
	echo "Available command line functions"
	echo -e "\t copydown <from [user|upgrade|factory]> <to [user|upgrade|factory]> <erase (from) [0|1]>" 
}

#/	Desc:	xxx
#/	Ver:	.1
#/	$1:		name1
#/	$2:		name1
#/	$3:		name1
#/	Out:	xxx
#/	Expl:	xxx
main() {
	_decho "func: MAIN called for BusyBox init script"
	initmkpaths
	initFS
	displayBanner
	initGPIO
	initSelectRootDev
	watchdog 60
	initLED
	if [[ $lastbutton -eq 1 ]]; then
		check_OSmessages
		check_USBmessages
		watchdog 300
		_decho "blinky!"
		setLED 10 5 0 1
		pivotprep
		stopled
		fullpivot
	else
		watchdog 600
		recovery
	fi
	_decho "exiting main"
}

_decho "bootdetect 1 : $1"
if [[ "$1" = "commandline" ]]; then
	cmd=$2;
	shift;
	shift;
	initmkpaths
	initSelectRootDev
	echo "running: $cmd '$@'"
	$cmd $@
elif [[ "$1" != "" ]]; then
	echo USEAGE: $0 commandline [functiontorun]
	helpfunc
	exit
else
	:
	main $@
fi
#shell
