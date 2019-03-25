SUMMARY = "A console development image with some C/C++ dev tools"

IMAGE_FEATURES += "package-management splash"
IMAGE_LINGUAS = "en-us"

inherit image

DEPENDS += "deviceos-users"

IMAGE_BOOT_FILES += "ww-console-image-initramfs-raspberrypi3.cpio.gz.u-boot;initramfs.img"

CORE_OS = " \
kernel-modules \
openssh openssh-keygen openssh-sftp-server \
packagegroup-core-boot \
packagegroup-core-full-cmdline \    
packagegroup-core-buildessential \
term-prompt \
tzdata \
"

WIFI_SUPPORT = " \
crda \
iw \
wpa-supplicant \
"

BLUETOOTH_SUPPORT = " \
bluez5 \
"

DEV_SDK_INSTALL = " \
binutils \
binutils-symlinks \
coreutils \
cpp \
cpp-symlinks \
diffutils \
elfutils elfutils-binutils \
file \
g++ \
g++-symlinks \
gcc \
gcc-symlinks \
gdb \
gdbserver \
gettext \
git \
ldd \
libstdc++ \
libstdc++-dev \
libtool \
ltrace \
make \
nodejs \
pkgconfig \
python3-modules \
strace \
openssl-dev \
zlib-dev \
"

DEV_EXTRAS = " \
"

EXTRA_TOOLS_INSTALL = " \
bzip2 \
devmem2 \
dosfstools \
ethtool \
fbset \
findutils \
grep \
i2c-tools \
iperf3 \
iproute2 \
iptables \
less \
nano \
netcat-openbsd \
nmap \
ntp ntp-tickadj \
procps \
rng-tools \
sysfsutils \
unzip \
util-linux \
wget \
zip \
"

RPI_STUFF = " \
userland \
"
EXTRA_WW_NOTWORKING = " \
cgroup-lite \
"

EXTRA_EXTRA = " \
perl \
"

EXTRA_WW = " \
bash-completion \
cmake \
curl \
daemontools \
dhcp-client \
dnsmasq \
dbus \
e2fsprogs \
git-perltools \
glibc-gconvs \
glibc-utils \
gnutls-openssl \
hostapd \
go \
iputils-ping \
jansson \
jansson-dev \
kernel-dev \
libevent \
libevent-dev \
liblockfile \
libmbim \
libnss-mdns \
libuv \
lsof \
ltp \
modemmanager \
minicom \
ncurses-dev \
parted \
ppp \
python-pip \
python3 \
readline \
rsync \
screen \
setserial \
socat \
start-stop-daemon \
sysstat \
tmux \
update-rc.d \
usb-modeswitch \
usbutils \
util-linux-agetty \
util-linux-bash-completion \
util-linux-uuidd \
valgrind \
wget-locale-zh-cn \
wget-locale-zh-tw \
xz \
libcrypto10 \
libssl10 \
openssl10 \
openssl-bin \
jansson \
jansson-dev \
deviceoswd \
"


WIGWAG_STUFF = " \
devicejs \
emacs \
fftw \
imagemagick \
lcms \
virtual/mbed-edge-core \
mbed-edge-examples \
mbed-devicejs-bridge \
mbed-edge-core-ww \
node-hotplug \
panic \
pps-tools \
pwgen \
twlib \
devicedb \
maestro \
deviceos-users \
global-node-modules \
wwrelay-utils \
mbed-fcc \
"

IMAGE_INSTALL += " \
${CORE_OS} \
${DEV_SDK_INSTALL} \
${DEV_EXTRAS} \
${EXTRA_TOOLS_INSTALL} \
${RPI_STUFF} \
${WIFI_SUPPORT} \
${BLUETOOTH_SUPPORT} \
${EXTRA_WW} \
${WIGWAG_STUFF} \
${MACHINE_EXTRA_RRECOMMENDS} \
"

set_local_timezone() {
    ln -sf /usr/share/zoneinfo/EST5EDT ${IMAGE_ROOTFS}/etc/localtime
}

disable_bootlogd() {
    echo BOOTLOGD_ENABLE=no > ${IMAGE_ROOTFS}/etc/default/bootlogd
}

ROOTFS_POSTPROCESS_COMMAND += " \
set_local_timezone ; \
disable_bootlogd ; \
"

export IMAGE_BASENAME = "console-image"
