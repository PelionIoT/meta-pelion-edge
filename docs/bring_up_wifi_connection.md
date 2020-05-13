# Setting up a Wi-Fi connection

**Disclaimer:** This is not an officially supported feature. It has been tested with consumer or standard Wi-Fi networks and not with corporate networks or mobile hotspots. Recommended way is to connect your Pi with Ethernet cable to a network with Internet access.

Follow these setups to configure wpa_supplicant
1. Shell or serial into the gateway. There is only one login user by default, root. The default password is set to `redmbed`.
1. Create wpa_supplicant directory, run -
    ```
    mkdir /etc/wpa_supplicant
    ```
1. Use any editor of your preference to edit this file, in this example we are using nano -
    ```
    nano /etc/wpa_supplicant/wpa_supplicant-wlan0.conf
    ```
1. Copy one of the sections as per your Wi-Fi network settings,
    ```
    # If the Wi-Fi network is password protected then copy the following content and replace <ssid> and <passphrase> with your network credentials.
    network={
        key_mgmt=WPA-PSK
        ssid="<ssid>"
        psk="<passphrase>"
    }
    ```

    or,

    ```
    # If the Wi-Fi network is open and doesn't require password, then copy the following content and replace <ssid> with your Wi-Fi name.
    network={
        key_mgmt=NONE
        ssid="<ssid>"
    }
    ```

    Save and exit, with `nano` run the commands -

    ```
    Ctrl+X, Y, Enter
    ```
1. Run this command to enable the wpa_supplicant service and configure systemd to start this service on boot -
    ```
    systemctl enable --now wpa_supplicant@wlan0
    ```
1. It will take some time for maestro dhcp_client to acquire an IP address. Run this command to know if your Pi has acquired an IP on wlan0 interface -
    ```
    ifconfig wlan0
    ```
1. Ping an external IP address to test your connectivity.
    ```
    ping 8.8.8.8
    ```

    If successful, you should see something like this -

    ```
    PING 8.8.8.8 (8.8.8.8) 56(84) bytes of data.
    64 bytes from 8.8.8.8: icmp_seq=1 ttl=56 time=3.10 ms
    64 bytes from 8.8.8.8: icmp_seq=2 ttl=56 time=4.27 ms
    ^C
    --- 8.8.8.8 ping statistics ---
    2 packets transmitted, 2 received, 0% packet loss, time 2ms
    rtt min/avg/max/mdev = 3.097/3.682/4.268/0.588 ms
    ```

## Troubleshooting
1. Make sure maestro dhcp_client is enabled on wlan0 interface. By default, we have [configured](../recipes-wigwag/maestro/maestro/rpi3/maestro-config-rpi3bplus.yaml) both interfaces - eth0 and wlan0 with maestro.
1. [wpa_supplicant Arch Linux Wiki](https://wiki.archlinux.org/index.php/Wpa_supplicant)
1. [ip man page](https://linux.die.net/man/8/ip)
