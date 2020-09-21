# Changelog
# Pelion Edge 2.1.0 - September 2020

Primary feature in this release is the addition of container orchestration
- [compiler] Switched to [golang 1.14.4](https://github.com/armPelionEdge/meta-pelion-edge/pull/158) compiler from 1.11.1.  This includes poky meta upstream [go*.bbclass](classes/) files.
- [edge-proxy] Added [edge-proxy](https://github.com/armPelionEdge/edge-proxy) tunneling daemon to the [build](https://github.com/armPelionEdge/meta-pelion-edge/pull/160/files#diff-cf2bb0a8acf52dfc5336946e4e85d00a)and subsequent systemD unit and supporting [files](https://github.com/armPelionEdge/meta-pelion-edge/pull/160/files#diff-d168093479202d5365837188cd0ab52f)
- [devicejs] Moved devicejs default port from [8080 to 8081](https://github.com/armPelionEdge/meta-pelion-edge/pull/160/files#diff-91df9bdf1d7512379b7145418e22a5a2) in support of edge-proxy using 8080 for kubelet
- [os] modified [/etc/hosts](https://github.com/armPelionEdge/meta-pelion-edge/pull/160/files#diff-2b56f81a8769186caec20129e7038331) creation file to support kubelet communication with edge-proxy using the address gateways.local
- [os] adds poky's [meta-virtualization](https://git.yoctoproject.org/cgit/cgit.cgi/meta-virtualization/) layer [bblayers](https://github.com/armPelionEdge/meta-pelion-edge/pull/161/files#diff-8eb2e96d599896eef2db53b566b4d701)to bring in docker and containerd programs, as well as kernel optimizations specified in meta-virtualization to [local.conf](https://github.com/armPelionEdge/meta-pelion-edge/pull/161/files#diff-f427c8a66b8134441c4facaa0aeaa518)).
- [os] adds [boot flags](https://github.com/armPelionEdge/meta-pelion-edge/pull/161/files#diff-a00df6690f4fb299ba7bae881b745122) to enable cgroups control over  memory and cpu utilization.
- [kubelet] Added [edge-kubelet](https://github.com/armPelionEdge/meta-pelion-edge/pull/161/files), modified to enable container orchestration with Pelion cloud to the main [console-image](https://github.com/armPelionEdge/meta-pelion-edge/pull/161/files#diff-9ed858c519118697208a2af7585cf7ef)
- [docker] adds docker via rdepends in [edge-kubelet](https://github.com/armPelionEdge/meta-pelion-edge/pull/161/files#diff-a60a51806a69d10af75c2d0a4c752698).  Docker's default storage location is moved to [/userdata/Docker](xxxx) to take advantage of /userdata's single (non-overlay) ext4 partition, enabling docker to use the more performant overfs2.
- [cni] adds contianer networking interfaces [cni](https://github.com/armPelionEdge/meta-pelion-edge/pull/161/files#diff-8d44f3bb16ba9ff316e2e3622366d386) to enable container network communication

### Bug Fixes
* general syntax and whitespace changes in bitbake recipes
* Fixed the situation where the root password changes after an upgrade by changing the default WIPETHEUSER_PARTITION to 0

### Known issues
- When conducting back to back production factory flow with the Pelion Edge Provisioner, the mcc_config directory sometimes is not written correctly and upon reboot, Edge-Core will not connect properly.  Workaround: run the provisioner again.
- After production factory flow, if you run the info command before edge-core pairs with the cloud, the info command will show n/a for the deviceID while displaying connected.   Workaround: delete the file /wigwag/system/lib/bash/relaystatics.sh and re-run the info command.
- Portal is not correctly updated after a firmware campaign in some instances
- The maximum translated devices behind the edge gateway is 100.
- No moving devices are supported (Device would be moving from Edge to another Edge device).
- Assumption is that no development is needed in device multiplexing code in Connect.
- Device Echo is not supported (Automatic grouping is not supported).
- No end-to-end security to translated device. Trust is between Edge and Pelion Device Management.
- No bootstrapping for translated devices.
- Registration on observation is not supported.

### Important Notes
* While provisioning your gateway, please use `vendor-id=42fa7b48-1a65-43aa-890f-8c704daade54` to unlock the rich node features, such as gateway logs and gateway terminal in the Pelion web portal.


## Release 2.1.0 - August 2020
### Added
Primary feature in this release is the addition of container orchistration
* [compiler] Switched to [golang 1.14.4](https://github.com/armPelionEdge/meta-pelion-edge/pull/158) compiler from 1.11.1.  This includes poky meta upstream [go*.bbclass](classes/) files.
* [edge-proxy] Added [edge-proxy](https://github.com/armPelionEdge/edge-proxy) tunneling daemon to the [build](https://github.com/armPelionEdge/meta-pelion-edge/pull/160/files#diff-cf2bb0a8acf52dfc5336946e4e85d00a)and subsequent systemD unit and supporting [files](https://github.com/armPelionEdge/meta-pelion-edge/pull/160/files#diff-d168093479202d5365837188cd0ab52f)
* [devicejs] Moved devicejs default port from [8080 to 8081](https://github.com/armPelionEdge/meta-pelion-edge/pull/160/files#diff-91df9bdf1d7512379b7145418e22a5a2) in support of edge-proxy using 8080 for kubelet
* [os] modified [/etc/hosts](https://github.com/armPelionEdge/meta-pelion-edge/pull/160/files#diff-2b56f81a8769186caec20129e7038331) creation file to support kubelet communication with edge-proxy using the address gateways.local
* [os] adds poky's [meta-virtualization](https://git.yoctoproject.org/cgit/cgit.cgi/meta-virtualization/) layer [bblayers](https://github.com/armPelionEdge/meta-pelion-edge/pull/161/files#diff-8eb2e96d599896eef2db53b566b4d701)to bring in docker and containerd programs, as well as kernel optimizations specified in meta-virtualzation to [local.conf](https://github.com/armPelionEdge/meta-pelion-edge/pull/161/files#diff-f427c8a66b8134441c4facaa0aeaa518)).
* [os] adds [boot flags](https://github.com/armPelionEdge/meta-pelion-edge/pull/161/files#diff-a00df6690f4fb299ba7bae881b745122) to enable cgroups control over  memory and cpu utilization. 
* [kubelet] Added [edge-kubelet](https://github.com/armPelionEdge/meta-pelion-edge/pull/161/files), modified to enable container orchistration with Pelion cloud to the main [console-image](https://github.com/armPelionEdge/meta-pelion-edge/pull/161/files#diff-9ed858c519118697208a2af7585cf7ef)
* [docker] adds docker via rdepends in [edge-kubelet](https://github.com/armPelionEdge/meta-pelion-edge/pull/161/files#diff-a60a51806a69d10af75c2d0a4c752698).  Docker's default storage location is moved to [/userdata/Docker](xxxx) to take advantage of /userdata's single (non-overlay) ext4 partition, enabling docker to use the more perforamnt overfs2.
* [cni] adds contianer networking interfaces [cni](https://github.com/armPelionEdge/meta-pelion-edge/pull/161/files#diff-8d44f3bb16ba9ff316e2e3622366d386) to enable container network communication
### Bug Fixes
* general syntax and whitespace changes in bitbake reciepes
* Fixed the situation where the root password changes after an upgrade by changing the default WIPETHEUSER_PARTITION to 0
### Known issues
* While provisioning your gateway, please use `vendor-id=42fa7b48-1a65-43aa-890f-8c704daade54` to unlock the rich node features, such as gateway logs and gateway terminal in the Pelion web portal.
## Release 2.0.0 - 2020-05-18
### Added
* [mbed-edge] Upgraded mbed-edge, also known as edge-core, from 0.8.0 to 0.12.0.
* [maestro] Fixed DHCP client in maestro daemon to reorder options and parameters and to add right options to discover packet.
* [maestro] Running maestro process with Go env flag - `GODEBUG=madvdontneed=1`.
* [devicedb] Upgraded DeviceDB from 1.9.2 to 1.9.4.
* [mbed-devicejs-bridge] Mapped new device function - accelerometer, gravity sensor, magnetometer, pressure, TVOC, gyroscope, signal strength, tap detection, CO2, step counter, Euler angles and heading to LwM2M objects and resources.
* [relay-term] Added websocket ping-pong handler for relay-term.
* [ble-pt] Added features to BLE protocol translator:
	* APIs to scan the gateway for BLE devices and report the MAC address, name and RSSI of all discovered devices.
	* APIs to allow users to dynamically onboard a BLE device found in the response of the above API.
	* Disabled the static whitelist of BLE service UUIDs.
	* Added support for Nordic Thingy and Embedded Planet Agora board.
* [os] Switched init system from SysVinit to SystemD.
* [os] Introduced `gai.conf` to control the sorting order of the addresses resolved by libc library. By default, IPv4 is preferred over IPv6.
* [os] Removed the cronjob, which periodically restarted the gateway services.
### Bug fixes
* Fixed the situation in which the maestro daemon can run out of the system resources and cause the platform to reboot.
* Fixed the issue of DHCP client being unable to renew the IP address lease.
* Fixed the intermittent failure of remote terminal not connecting to Pelion cloud.
* Fixed the intermittent bug in the maestro daemon, causing it not to restart a monitoring process that exits unexpectedly.
### Known issues
* While provisioning your gateway, please use `vendor-id=42fa7b48-1a65-43aa-890f-8c704daade54` to unlock the rich node features, such as gateway logs and gateway terminal in the Pelion web portal.
## 1.0.0
### Summary abstract 
This is the 1.0 release of the Pelion Edge for Gateways. In general, it is a Yocto metalayer to build an operating system for a gateway that will connect to the Arm Pelion Cloud. Once provisioned with the Pelion Cloud, the gateway can register Bluetooth devices to the cloud, participate in secure update campaigns and be controlled from a mobile application. The first supported platform is the Raspberry PI3b+.    
### Features description 
- Gateway-based service enabling edge applications to interact with gateway-connected devices through a REST API. 
- Systems management API and daemon (Maestro): 
  - Dynamic system configuration. 
  - Logging pushed to the Pelion Cloud. 
- Protocol translator engine with example Bluetooth implementation.
- Supports upgrade campaigns from Pelion Cloud: 
  - Securely downloads edge gateway firmware updates. 
  - Keeps deployed gateways up to date. 
- Cloud services support gateway features for edge applications: 
  - Remote access. 
  - Remote terminal (Preview feature). 
  - Device data collection and query. 
  - Edge alerting. 
  - Real-time device control and configuration. 
- REST APIs available locally on the gateway to read, write and observe device’s states.  
- Virtual Device Driver application – ability to create different types of virtual devices on the gateway.  

### Installation and usage 
Follow the instructions for building the operating system for Raspberry PI at the README for this GitHub project. 
### Help and issues
Follow the README. For issues, file GitHub issues.
### Known issues 
#### System software 
- There is a known situation in which the maestro daemon can run out of system resources and cause the platform to reboot. (E19-264) 
- On some networks, the IP address can become disconnected, and DHCP fails to reobtain an address. To remedy this problem, restart the gateway. (E19-243) 
- Sometimes the preview feature 'remote terminal' fails to connect. In this instance, the workaround is to restart the gateway. 

#### Cloud service 
- Sending 1-2K back-to-back requests to the Pelion Cloud "accounts" service causes a 500 return. A few seconds after the requests clear, the service is again available. (E19-381) 
- While provisioning your gateway, please use `vendor-id=42fa7b48-1a65-43aa-890f-8c704daade54` to unlock some of the functionalities, such as gateway logs and gateway terminal in the Pelion web portal. (E19-419) 