# Changelog

## Pelion Edge 2.2 - February 2021

<<<<<<< HEAD
In this release of Pelion Edge 2.2, we introduce support for two additional platforms and one additional Yocto distribution operating system: Now supported by Pelion Edge is the Linux microPlatform (LMP) OS, running on NXP's i.MX8 development platform `imx8mmevk` and the Xilinx Zynq® UltraScale development platform `uz3eg-iocc`. Pelion Edge continues support of Yocto's Poky OS on the Raspberry PI3. [Quick start guides](https://developer.pelion.com/docs/device-management-edge/2.2/quick-start/index.html) are available for all three platforms and should be followed individually because the build systems have differences. meta-pelion-edge and meta-mbed-edge are the basis for all Pelion Edge programs. Although the operating systems are different in support models, Pelion Edge has been tested to perform and operate the same within both OS environments. Moreover, both operating systems have the same feature set, other than the following difference:

   - [FOTA Update] Pelion Edge on Poky OS continues to use overlayFS for upgrades as it did in previous releases. Pelion Edge on LMP OS uses a new mechanism called [OSTree](https://ostreedev.github.io/ostree/).

The primary features in this release:
- [edge-core] Updated Edge Core to [0.15.0](https://github.com/PelionIoT/mbed-edge/blob/master/CHANGELOG.md#release-0150-2021-1-12):
   - Moved the mbed-edge-core recipe to its own meta layer: [meta-mbed-edge](https://github.com/PelionIoT/meta-mbed-edge).
   - The new FOTA update framework library is supported on platforms `imx8mmevk` and `uz3eg-iocc` but not on `raspberrypi3`. To compile with this library, add the `MBED_EDGE_CORE_CONFIG_FIRMWARE_UPDATE="ON"`, `MBED_EDGE_CORE_CONFIG_FOTA_ENABLE="ON"` and `MBED_EDGE_CORE_CONFIG_CURL_DYNAMIC_LINK="ON"` Bitbake parameters to local.conf.  (Note: The location for local.conf is specific to each platform, so we recommend following the quick start guide in the documentation in detail.) 
   - The old firmware update library, Update Client (UC) hub, is only supported on the `raspberrypi3` platform. To enable that, add the `MBED_EDGE_CORE_CONFIG_FIRMWARE_UPDATE="ON"` parameter to local.conf.
- [maestro] Updated Maestro to [v2.9.0](https://github.com/PelionIoT/maestro/releases/tag/v2.9.0):
   - [Gateway capabilities](https://developer.pelion.com/docs/device-management-edge/2.2/managing/maestro.html#gateway-capabilities) - Allows gateways to advertise the features supported by the platform. Maestro uses Edge Core's GRM JSON-RPC APIs to add to the gateway device's LwM2M resources. The registered resources are added under Pelion's reserved FeatureMgmt LwM2M object, 33457, with three resources: 0 - featureID, 1 - enabled and 2 - config.
   - Remote config management through LwM2M - FeatureMgmt config resource allows you to remotely view the current configuration of the feature and also push a config update using the LwM2M cloud service APIs. Maestro, on receiving an update, writes the content to the file path specified in the respective parameter config_filepath.
   - Removed parsing and generation of self-signed certificates. Also removed the platforms rp200 and wwrelay, which are no longer supported.
   - Routed Maestro traffic through the edge-proxy service instead of directly connecting to the Pelion Device Management cloud.
   - By default, disabled the Maestro's gateway logging feature in the configuration file for platforms `imx8mmevk`, `uz3eg-iocc` and `raspberrypi3`. The gateway logging feature is supported through Fluent Bit.
- [logrotate] Added bbappend file to install a configuration that rotates the logs of files under `/var/log`.
- [logrotate] Removed crontab method to call logrotate every 5min. Instead, we configured systemd logrotate timer to 5min.
- [identity-tool] Established an independent recipe under recipes-connectivity and removed the dependency on wwrelay-utils:
   - Converted the identity node program to bash, which lives in a new project called pe-utils.
   - Installed v2.0.4 of identity-tool, which removes the generation of self-signed certificate.
- [edge-proxy] Configured services gateway logs (Maestro), gateway stats (Maestro), relay-term, Fluent Bit HTTP endpoint to route traffic through the edge-proxy service and not consume self-signed certificate.
- [meta-nodejs] Removed the dependency on node v8.x. Upgraded the node packages to work with default nodejs version of Dunfell.
- [relay-term] Upgraded relay-term to work with node v12.x. Established an independent recipe from wwrelay-utils and removed the dependency on global-node-modules.
- [mbed-fcce] Upgraded factory-configurator-client-example to v4.7.0 and pinned its dependencies.
- [fluentbit] Added recipe to install Fluent Bit 1.3.5 on the gateway for providing an open source log processor and forwarder solution:
   - By default, Fluent Bit is configured with the following input endpoints - CPU, MEM, Systemd services - edge-core, edge-proxy, pelion-relay-term, Maestro, kubelet, Docker and wait-for-pelion-identity. FluentBit is also configured with an output endpoint HTTP to publish logs at API endpoint: `http://gateways.local:8080/v3/device-logs` (routing through the edge-proxy service).
- [journald] Enabled Forward Secure Sealing (FSS) feature of systemd journal. This helps detect gateway logs file tampering:
   - To configure Pelion Edge gateway with a sealing key and to keep track of the verification key in production setup, use Pelion Edge Provisioner (PEP) tool [v2.3.0](https://github.com/PelionIoT/pelion-edge-provisioner/releases/tag/v2.3.0).
   - By default, the gateway is configured with persistent journal logging. To disable that, set flag `VOLATILE_LOG_DIR = "no"` in `local.conf`, and update the `Storage` in recipes-core/systemd/systemd-conf/journald.conf. Note: If you disable persistent logging, the FSS feature won't work.
- [systemd] Configured to manage the network. By default, we disabled Maestro's network management feature.
- [systemd] Updated the unit startup sequence and changed `Requires` directive to `Wants`. This makes the system more robust when dealing with failing services.

   ```
   network-online --> edge-core --> wait-for-pelion-identity --> edge-proxy
                                                             --> edge-kubelet
                                                             --> fluentbit (td-agent-bit)
                                                             --> pelion-relay-term
                                                             --> maestro --> devicedb
   ```
  
- [deprecation] Removed deprecated services:
   - devicejs-ng.
   - Compatible devicejs-ng protocol translators.
- [image improvements] Simplified and improved the "raspberrypi" supported image "console-image":
   - console-image previously, version 1.0 through 2.1, contained Pelion Edge and development tools, including but not limited to: compliers, editors, analysis tools, stress tools and SQA tools. Pelion Edge version 2.2's console-image contains a minimized set of accompanying software for running and testing all of Pelion Edge's software and features. Note: This isn't a minimal image that strips common Linux tools but is instead what you might expect to find on a heavy embedded device. With this new strategy, you can customize the image more easily. You can strip the image more to make a more lightweight embedded OS or add more packages to make it more like the previously provided image.
      - LMP's equivalent image is named `lmp-console-image`.
   - meta-pelion-edge itself as a Yocto layer is now easier to incorporate with other layers, allowing other Yocto projects to incorporate Pelion Edge.
   - Updated the splash screen banner from "DeviceOS by WigWag" to "Pelion".
   - [recipe removals] Removed:
      - wwrelay-utils recipe. Previously, this bitbake recipe performed many functions that have been replaced. For more details, please reference the recipe additions section below.
      - strace-plus recipe because the image no longer ships with developer tools enabled.
      - tsb recipe because the old wigwag relay is no longer supported.
      - pps-tools recipe because the image no longer ships with developer tools enabled.
      - node-znp recipe because devicejs is deprecated.
      - node-netkit recipe because devicejs is deprecated.
      - fftw recipe because the image no longer ships with developer tools enabled.
      - emacs recipe because the image no longer ships with developer tools enabled.
      - dnsmasq recipe because Pelion Edge programs no longer need these features.
      - deviceOSWD recipe because the old wigwag relay is no longer supported.
      - maestro-watchdog recipe because the old wigwag relay is no longer supported.
- Removed the dependency on hardcoded `vendor-id=42fa7b48-1a65-43aa-890f-8c704daade54` required while provisioning your gateway. To view the gateway logs and gateway terminal in the Pelion portal, enable the Gateway capabilities through Maestro configuration. (By default, logs and terminal are configured for supported platforms.)
=======
The primary features in this release:

* [edge-core] Updated Edge Core to [0.15.0](https://github.com/PelionIoT/mbed-edge/blob/master/CHANGELOG.md#release-0150-2021-1-12).
  * Moved the mbed-edge-core recipe to its own meta layer - [meta-mbed-edge](https://github.com/PelionIoT/meta-mbed-edge).
  * The new FOTA update framework library is supported on platforms `imx8mmevk` and `uz3eg-iocc` but not on `raspberrypi3`. To compile with this library, add the `MBED_EDGE_CORE_CONFIG_FIRMWARE_UPDATE="ON"`, `MBED_EDGE_CORE_CONFIG_FOTA_ENABLE="ON"` and `MBED_EDGE_CORE_CONFIG_CURL_DYNAMIC_LINK="ON"` Bitbake parameters to local.conf.
  * The old firmware update library, Update Client (UC) hub, is only supported on the `raspberrypi3` platform. To enable that, add the `MBED_EDGE_CORE_CONFIG_FIRMWARE_UPDATE="ON"` parameter to local.conf.
* [maestro] Updated Maestro to [v2.9.0](https://github.com/PelionIoT/maestro/releases/tag/v2.9.0).
  * Gateway capabilities - Allows gateways to advertise the features supported by the platform. Maestro uses Edge Core's GRM JSON-RPC APIs to add to the gateway device's LwM2M resources. The registered resources are added under Pelion's reserved FeatureMgmt LwM2M object - 33457 with three resources - 0 - featureID, 1 - enabled, 2 - config.
  * Remote config management through LwM2M - FeatureMgmt config resource allows you to remotely view the current configuration of the feature and also push a config update using the LwM2M cloud service APIs. Maestro, on receiving an update, writes the content to the file path specified in the respective parameter config_filepath.
  * Removed parsing and generation of self-signed certificates. Also removed the platforms rp200 and wwrelay, which are no longer supported.
  * Maestro traffic now routed through edge-proxy instead of directly connecting to the Pelion cloud.
  * By default, disabled the Maestro's gateway logging feature in the configuration file for platforms `imx8mmevk`, `uz3eg-iocc` and `raspberrypi3`. Gateway logging feature is supported through Fluent Bit.
* [logrotate] Added bbappend file to install a configuration that rotates the logs of files under `/var/log`.
* [logrotate] Removed crontab method to call logrotate every 5mins. Instead, we configured systemd logrotate timer to 5mins.
* [identity-tool] Established an independent recipe under recipes-connectivity and removed the dependency on wwrelay-utils.
  * Converted the identity node program to bash, which lives in a new project called pe-utils.
  * Installed v2.0.4 of identity-tool, which majorly removes the generation of self-signed certificate.
* [edge-proxy] Configured services gateway logs (Maestro), gateway stats (Maestro), relay-term, Fluent Bit HTTP endpoint to route traffic through edge-proxy and not consume self-signed certificate.
* [meta-nodejs] Removed the dependency on node v8.x. Upgraded the node packages to work with default nodejs version of Dunfell.
* [relay-term] Upgraded relay-term to work with node v12.x. Established an independent recipe from wwrelay-utils and removed the dependency on global-node-modules.
* [mbed-fcce] Upgraded factory-configurator-client-example to v4.7.0 and pinned its dependencies.
* [fluentbit] Added recipe to install Fluentbit 1.3.5 on the gateway for providing an open source log processor and forwarder solution.
  * By default, Fluent Bit is configured with following input endpoints - CPU, MEM, Systemd services - edge-core, edge-proxy, pelion-relay-term, maestro, kubelet, docker and wait-for-pelion-identity. And an output endpoint HTTP to publish logs at API endpoint - `http://gateways.local:8080/v3/device-logs` (routing through edge-proxy).
* [journald] Enabled Forward Secure Sealing (FSS) feature of systemd journal. This helps detect gateway logs file tampering.
  * To configure Pelion Edge gateway with a sealing key and to keep track of the verification key in production setup, use Pelion Edge Provisioner (PEP) tool [v2.3.0](https://github.com/PelionIoT/pelion-edge-provisioner/releases/tag/v2.3.0).
  * By default, the gateway is configured with persistent journal logging. To disable that, set flag `VOLATILE_LOG_DIR = "no"` in `local.conf` and also update the `Storage` in recipes-core/systemd/systemd-conf/journald.conf. Note: If you disable persistent logging, the FSS feature won't work.
* [systemd] Configured to manage the network. By default, we disabled Maestro's network management feature.
* [systemd] Updated the unit startup sequence and changed `Requires` directive to `Wants`. This makes the system more robust when dealing with failing services.

  ```
  network-online --> edge-core --> wait-for-pelion-identity --> edge-proxy
                                                            --> edge-kubelet
                                                            --> fluentbit (td-agent-bit)
                                                            --> pelion-relay-term
                                                            --> maestro --> devicedb
  ```
  
* [deprecation] Removed deprecated services.
   * devicejs-ng.
   * compatible devicejs-ng protocol translators.
* [image improvements] The "raspberrypi" supported image "console-image" has been simplified and improved.
   * console-image previously, version 1.0 through 2.1, contained Pelion Edge + development tools including but not limited to: compliers, editors, analysis tools, stress tools, and SQA tools.  Pelion Edge version 2.2's console-image contains a minimized set of accompany software for running and testing all of Pelion Edge's software and features.  Important to note, it is not a bare minimal image that strips common Linux tools, but more so what you might expect to find on a heavy embedded device.  With this new strategy, users of the Pelion Edge image can customize the image more easily to their liking.  It is very possible to strip the image more making a more lightweight embedded OS or add more packages to make it more like the image provide previously.
   * meta-pelion-edge itself as a yocto layer is now easier to incorporate with other layers, allowing other yocto projects to incorporate Pelion Edge.
   * Updated the splash screen banner from "DeviceOS by WigWag" to "Pelion".
   * [recipe removals]
      * wwrelay-utils recipe is removed. Previously, this bitbake recipe performed many functions which have been replaced. Reference the recipe additions section below.
      * strace-plus recipe is removed because the image no longer ships with developer tools enabled.
      * tsb recipe is removed because the old wigwag relay is no longer supported.
      * pps-tools recipe is removed because the image no longer ships with developer tools enabled.
      * node-znp recipe is removed because devicejs is deprecated.
      * node-netkit recipe is removed because devicejs is deprecated.
      * fftw recipe is removed because the image no longer ships with developer tools enabled.
      * emacs recipe is removed because the image no longer ships with developer tools enabled.
      * dnsmasq recipe is removed because Pelion Edge programs no longer need these features.
      * deviceOSWD recipe is removed because the old wigwag relay is no longer supported.
      * maestro-watchdog recipe is removed because the old wigwag relay is no longer supported.
>>>>>>> 5c21893... Edit CHANGELOG.md

### Bug fixes

- After production factory flow, if you ran the `info` command before Edge Core paired with the cloud, the `info` command showed `N/A` for the deviceID while displaying connected. This has been fixed.
- Streamlined the startup sequence of Pelion Edge programs to remove the cyclic dependency, which caused many starts and stops of processes in certain conditions.
- When conducting back-to-back production factory flow with the Pelion Edge Provisioner, the mcc_config directory sometimes was not written correctly, and upon reboot, Edge Core didn't connect properly. This has been fixed.

- After production factory flow, if you ran the `info` command before Edge Core paired with the cloud, the `info` command showed `N/A` for the deviceID while displaying connected. This has been fixed.
- Streamlined the startup sequence of Pelion Edge programs to remove the cyclic dependency, which caused many starts and stops of processes in certain conditions.
- When conducting back-to-back production factory flow with the Pelion Edge Provisioner, the mcc_config directory sometimes was not written correctly, and upon reboot, Edge Core didn't connect properly. This has been fixed.

### Known issues

<<<<<<< HEAD
- The Pelion Device Management portal is not correctly updated after a firmware campaign in some instances.
- [maestro] The FeatureMgmt config resource is initialized with a maximum 3.8KB of file content. The remaining file content is truncated during initialization. This is most likely due to the limitation of the gorilla/websocket library but needs further investigation. However, Pelion Device Management users can still push a file size of a maximum of 64KB through cloud service APIs.
- [pt-example] `cpu-temperature` device reports random values because the default CPU temperature file is not the same on Yocto and LmP.

### Limitations

- There is a maximum size limit to the full registration message, which limits the number of devices Edge can host:
   - Maximum registration message size is 64KB.
   - Hosted devices with five typical Resources consume ~280B (the exact size depends, for example, on the length of resource paths). This limits the maximum number to 270 devices.
   - The more Resources you have, the fewer devices can be supported.
   - The Pelion Edge device Resources are also included in the same registration message.
   - **Test the limits with your configuration, and set guidance accordingly.**
- Devices behind Pelion Edge do not support [auto-observation](https://www.pelion.com/docs/device-management/current/connecting/device-guidelines.html#auto-observation).
- Pelion Device Management Client enabled devices must first boostrap to the Pelion Device Management cloud before connecting to Pelion Edge.
- No moving devices are supported (such as the device moving from Pelion Edge to another edge device.)
- LMP's base partition table is set above 10GB to support three upgrade images in OSTree. Therefore, we only support SD card installation (compared to supporting onboard EMMC or NAND) for the `imx8mmevk` and the `uz3eg-iocc`.
=======
- Portal is not correctly updated after a firmware campaign in some instances.
- [maestro] The FeatureMgmt config resource is initialized with a maximum 3.8KB of file content. The remaining file content is truncated during initialization. This is most likely due to the limitation of the gorilla/websocket library but needs further investigation. However, Pelion Device Management users can still push a file size of a maximum of 64KB through cloud service APIs.

#### Xilinx ZU3EG

If you enable kernel configurations [CPU_IDLE](https://cateee.net/lkddb/web-lkddb/CPU_IDLE.html) and [PREEMPT](https://cateee.net/lkddb/web-lkddb/PREEMPT.html), the LmP release including PetaLinux 2020.2 does not work in stable manner. Our default configuration has those disabled. If you have any issues with those configurations, please contact Xilinx support.

### Limitations

- There is a maximum size limit to the full registration message, which limits the number of devices Edge can host.
   - Maximum registration message size is 64 KB.
   - Hosted devices with five typical Resources consume ~280 bytes (the exact size depends, for example, on the length of resource paths). This limits the maximum number to 270 devices.
   - The more Resources you have, the fewer devices can be supported.
   - The Edge device Resources are also included in the same registration message.
   - **Test the limits with your configuration and set guidance accordingly.**
- Devices behind Pelion Edge do not support [auto-observation](https://www.pelion.com/docs/device-management/current/connecting/device-guidelines.html#auto-observation).
- Pelion Device Management Client enabled devices must first boostrap to the Pelion Device Management Cloud before connecting to Pelion Edge.
- No moving devices are supported. (Device would be moving from Edge to another Edge device.)
>>>>>>> 5c21893... Edit CHANGELOG.md

## Pelion Edge 2.1.2 - January 2021

Updated 'pip' download url to download specific verson.


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
