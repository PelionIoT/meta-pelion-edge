# Changelog

## Pelion Edge 2.2 - January 2021

The primary features in this release:
* [edge-core] Updated Edge Core to [0.15.0](https://github.com/PelionIoT/mbed-edge/blob/master/CHANGELOG.md#release-0150-2021-1-12)
  * The new FOTA update framework library will be supported on platforms - `imx8mmevk` and `uz3eg-iocc`, (Feb 2021 release) not on `raspberrypi3`. To compile with this library, add following Bitbake parameters to local.conf - `MBED_EDGE_CORE_CONFIG_FIRMWARE_UPDATE="ON"`, `MBED_EDGE_CORE_CONFIG_FOTA_ENABLE="ON"` and `MBED_EDGE_CORE_CONFIG_CURL_DYNAMIC_LINK="ON"`.
  * The old firmware update library - Update Client (UC) hub is only supported on `raspberrypi3` platform. To enable that add following parameter to local.conf - `MBED_EDGE_CORE_CONFIG_FIRMWARE_UPDATE="ON"`.
* [maestro] Updated maestro to [v2.9.0](https://github.com/PelionIoT/maestro/releases/tag/v2.9.0)
  * Gateway capabilities - Allows gateways to advertise the features supported by the platform. Maestro utilizes Edge Core's GRM JSON-RPC APIs to add to the gateway device's LwM2M resources. The registered resources are added under Pelion's reserved FeatureMgmt LwM2M object - 33457 with 3 resources - 0 - featureID, 1 - enabled, 2 - config.
  * Remote config management via LwM2M - FeatureMgmt config resource allows users to remotely view the current configuration of the feature and also push a config update using the LwM2M cloud service APIs. Maestro, on receiving an update, writes the content to the file path specified in the respective parameter config_filepath.
  * Removed parsing and generation of self-signed certificates. Also removed the platforms rp200 and wwrelay which are no longer supported.
  * Maestro traffic now routed through edge-proxy instead of directly connecting to the Pelion Cloud
* [meta-nodejs] Removed the dependency on node v8.x. Upgraded the node packages to work with default nodejs version of Dunfell.
* [relay-term] Upgraded relay-term to work with node v12.x. Established an independent recipe from wwrelay-utils and removed the dependency on global-node-modules.
* [fluentbit] Added recipe to install Fluentbit 1.3.5 on the gateway for providing an open source Log Processor and Forwarder solution.
  * By default, fluentbit is configured with following input endpoints - CPU, MEM, Systemd services - edge-core, edge-proxy, pelion-relay-term, maestro, kubelet, docker and wait-for-pelion-identity and Tail for /wigwag/log/devicejs.log.
  * The output endpoint is posting the logs at API `http://gateways.local:8080/v3/devicejs-logs` (routing through edge-proxy).
* [journald] Enabled Forward Secure Sealing (FSS) feature of systemd journal. This will help detect gateway logs file tampering.
  * To configure Pelion Edge gateway with sealing key and to keep track of verification key in production setup, use Pelion Edge Provisioner (pep) tool [v2.3.0](https://github.com/PelionIoT/pelion-edge-provisioner/releases/tag/v2.3.0).
  * Provides the means to make logging persistent. Documentation on how to do this is updated.
* [systemd] Configured to manage the network. By default, disabled Maestro's network management feature.
* [depreciation] Removed deprecated services.
  * - devicejs-ng
  * - compatible devicejs-ng protocol translators.
* [image improvements] The "raspberrypi" supported image "console-image" has been simplified and thereby improved. 
  * console-image previously, version 1.0 through 2.1, contained Pelion Edge + development tools including but not limited to: compliers, editors, analysis tools, stress tools, and SQA tools.  Pelion Edge version 2.2's console-image contains a minimized set of accompany software for running and testing all of Pelion Edge's software and features.  Important to note, it is not a bare minimal image that strips common Linux tools, but more so what you might expect to find on a heavy embedded device.  With this new strategy, users of the Pelion Edge image can customize the image more easily to their liking.  It is very possible to strip the image more making a more lightweight embedded OS or add more packages to make it more like the image provide previously.  
  * meta-pelion-edge itself as a yocto layer is now easier to incorporate with other layers, allowing other yocto projects to incorporate Pelion Edge.
  * [recipe removals]
    * wwrelay-utils recipe is removed.  Previously this bitbake recipe did many functions which are now replaced.  Reference the recipe additions section below.
    * strace-plus recipe is removed as the image is no longer ships with developer tools enabled.
    * tsb recipe is removed as the old wigwag relay is no longer supported.
    * pps-tools recipe is removed as the image is no longer ships with developer tools enabled.
    * node-znp recipe is removed as devicejs is deprecated.
    * node-netkit recipe is removed as devicejs is deprecated.
    * fftw recipe is removed as the image is no longer ships with developer tools enabled.
    * emacs recipe is removed as the image is no longer ships with developer tools enabled.
    * dnsmasq recipe is removed as Pelion Edge programs no longer need these features.
    * deviceOSWD recipe is removed as the old wigwag relay is no longer supported.


### Bug fixes
- After production factory flow, if you run the info command before Edge core pairs with the cloud, the info command shows N/A for the deviceID while displaying connected. This has been fixed.
 - Streamlined the startup sequence of Pelion Edge programs in order to remove the cyclic dependency, which caused many starts and stops of processes in certain conditions.

### Known issues

- When conducting back-to-back production factory flow with the Pelion Edge Provisioner, the mcc_config directory sometimes is not written correctly and upon reboot, Edge-Core does not connect properly. Workaround: Run the provisioner again.

- Portal is not correctly updated after a firmware campaign in some instances.

### Limitations

- The maximum translated devices behind the edge gateway is 100.
- Devices behind Pelion Edge do not support [auto-observation.](https://www.pelion.com/docs/device-management/current/connecting/device-guidelines.html#auto-observation)
- Pelion Device Management Client enabled devices must first boostrap to the Pelion Device Management Cloud before connecting to Pelion Edge.
- No moving devices are supported. (Device would be moving from Edge to another Edge device.)

### Important note

While provisioning your gateway, please use `vendor-id=42fa7b48-1a65-43aa-890f-8c704daade54` to unlock the rich node features, such as gateway logs and gateway terminal in the Pelion web portal.


## Pelion Edge 2.1.1 - December 2020

Pinned dependency that broke 'mbed_fcc' build dependency.

## Pelion Edge 2.1.0-1 - October 2020

Updated package source file protocol from SSH to HTTPS.

## Pelion Edge 2.1.0 - September 2020

The primary feature in this release is the addition of container orchestration:

- [compiler] Switches to [golang 1.14.4](https://github.com/armPelionEdge/meta-pelion-edge/pull/158) compiler from 1.11.1. This includes poky meta upstream [go.bbclass](classes/) files.
- [edge-proxy] Adds [edge-proxy](https://github.com/armPelionEdge/edge-proxy) tunneling daemon to the [build](https://github.com/armPelionEdge/meta-pelion-edge/pull/160/files#diff-cf2bb0a8acf52dfc5336946e4e85d00a)and subsequent systemD unit and supporting [files](https://github.com/armPelionEdge/meta-pelion-edge/pull/160/files#diff-d168093479202d5365837188cd0ab52f).
- [devicejs] Moves devicejs default port from [8080 to 8081](https://github.com/armPelionEdge/meta-pelion-edge/pull/160/files#diff-91df9bdf1d7512379b7145418e22a5a2) in support of edge-proxy using 8080 for kubelet.
- [os] Modifies [/etc/hosts](https://github.com/armPelionEdge/meta-pelion-edge/pull/160/files#diff-2b56f81a8769186caec20129e7038331) creation file to support kubelet communication with edge-proxy using the address gateways.local.
- [os] Adds poky's [meta-virtualization](https://git.yoctoproject.org/cgit/cgit.cgi/meta-virtualization/) layer [bblayers](https://github.com/armPelionEdge/meta-pelion-edge/pull/161/files#diff-8eb2e96d599896eef2db53b566b4d701)to bring in Docker and containered programs, as well as kernel optimizations specified in meta-virtualization to [local.conf](https://github.com/armPelionEdge/meta-pelion-edge/pull/161/files#diff-f427c8a66b8134441c4facaa0aeaa518)).
- [os] Adds [boot flags](https://github.com/armPelionEdge/meta-pelion-edge/pull/161/files#diff-a00df6690f4fb299ba7bae881b745122) to enable cgroups control over memory and CPU use.
- [kubelet] Adds [edge-kubelet](https://github.com/armPelionEdge/meta-pelion-edge/pull/161/files), modified to enable container orchestration with Pelion cloud to the main [console-image](https://github.com/armPelionEdge/meta-pelion-edge/pull/161/files#diff-9ed858c519118697208a2af7585cf7ef).
- [docker] Adds Docker via rdepends in [edge-kubelet](https://github.com/armPelionEdge/meta-pelion-edge/pull/161/files#diff-a60a51806a69d10af75c2d0a4c752698). Docker's default storage location is moved to [/userdata/Docker](xxxx) to take advantage of /userdata's single (non-overlay) ext4 partition, enabling Docker to use the more performant overfs2.
- [cni] Adds container networking interfaces [cni](https://github.com/armPelionEdge/meta-pelion-edge/pull/161/files#diff-8d44f3bb16ba9ff316e2e3622366d386) to enable container network communication.

### Bug fixes

- General syntax and whitespace changes in bitbake recipes.
- Fixed the situation where the root password changes after an upgrade by changing the default WIPETHEUSER_PARTITION to 0.

### Known issues

- When conducting back-to-back production factory flow with the Pelion Edge Provisioner, the mcc_config directory sometimes is not written correctly and upon reboot, Edge-Core does not connect properly. Workaround: Run the provisioner again.
- After production factory flow, if you run the info command before Edge core pairs with the cloud, the info command shows N/A for the deviceID while displaying connected. Workaround: Delete the file /wigwag/system/lib/bash/relaystatics.sh, and rerun the info command.
- Portal is not correctly updated after a firmware campaign in some instances.

### Limitations

- The maximum translated devices behind the edge gateway is 100.
- Devices behind Pelion Edge do not support [auto-observation.](https://www.pelion.com/docs/device-management/current/connecting/device-guidelines.html#auto-observation)
- Pelion Device Management Client enabled devices must first boostrap to the Pelion Device Management Cloud before connecting to Pelion Edge.
- No moving devices are supported. (Device would be moving from Edge to another Edge device.)

### Important note

While provisioning your gateway, please use `vendor-id=42fa7b48-1a65-43aa-890f-8c704daade54` to unlock the rich node features, such as gateway logs and gateway terminal in the Pelion web portal.

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
