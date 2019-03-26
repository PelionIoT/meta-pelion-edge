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
    coreutils \
    diffutils \
    elfutils elfutils-binutils \
    file \
    gdb \
    gdbserver \
    git \
    ldd \
    ltrace \
    nodejs \
    packagegroup-core-buildessential \
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
    firewall \
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

WIGWAG_STUFF = " \
    devicejs \
    emacs \
    fftw \
    imagemagick \
    lcms \
    virtual/mbed-edge-core \
    mbed-edge-examples \
    mbed-devicejs-bridge \
    node-hotplug \
    panic \
    pgw-os-dev \
    pgw-os-essentials \
    pps-tools \
    pwgen \
    su-exec \
    term-prompt \
    tsb \
    twlib \
    devicedb \
    maestro \
    deviceos-users \
    global-node-modules \
    wwrelay-utils \
    fcc \
"

OPENSSL_102 = " \
    libcrypto10 \
    libssl10 \
    openssl10 \
    openssl \
    openssl-bin \
"

IMAGE_INSTALL += " \
    ${CORE_OS} \
    ${DEV_SDK_INSTALL} \
    ${DEV_EXTRAS} \
    ${EXTRA_TOOLS_INSTALL} \
    ${RPI_STUFF} \
    ${WIFI_SUPPORT} \
    ${BLUETOOTH_SUPPORT} \
    ${WIGWAG_STUFF} \
    ${OPENSSL_102} \
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
