# Pelion Edge 2.5.2

- Fix compilation error related to `mbed-fcce`.
- Pull in `opencontainer/runc` recipe to `meta-pelion-edge` to fix master to main rename build breakage.

# Pelion Edge 2.5.1 - 18th Jan 2021

- Fixed code fetch error by pointing [mbedtls repository definition](https://github.com/PelionIoT/meta-mbed-edge/commit/56dce48ed2d832d603b86505d2467ef0d9f821e4) to the redefined location.
- Fixed code fetch error on `meta-mbed-edge`/`mbed-edge` and `mbed-edge-examples` by using `protocol=https` in `SRC_URI` as [GitHub is now brownouting plain-text git fetching/cloning](https://github.blog/2021-09-01-improving-git-protocol-security-github/).
- Updated `bitbake` tooling to version [3.3.4](https://github.com/openembedded/bitbake/commits/yocto-3.3.4), which has support for using https-protocol by default to avoid GitHub plain-text cloning issue.

# Pelion Edge 2.5.0 - 14th Dec 2021

## New features

- Updated [LmP version to v83](https://foundries.io/products/releases/83/).
    - Introduced a new machine for the AVNET ZU3EG board. Use the `zue3eg-iocc-ebbr` machine to enable UEFI Capsule Update, which lets you update the boot partition together with another component - for example, the kernel - as part of Pelion's Combined Update feature.
    - Dropped boot.cmd for imx8 boards in support of the default u-boot from LmP.
- [Edge-Core] Updated [Edge Core to 0.19.1](https://github.com/PelionIoT/mbed-edge/releases/tag/0.19.1).
    - Brought in Pelion Device Management Client 4.11.1.
    - Introduced Combined Update callback capabilities.
    - Updated Mbed TLS version to 2.27.
    - Set `MBED_CONF_MBED_CLIENT_MAX_RECONNECT_TIMEOUT` to 10 minutes by default. This provides faster reconnection in the event of a network breakdown.
- [Terminal]
    - Rewrote terminal with `golang`.
    - Removed the need for `node.js` in the build.
    - This decreased build RAM requirements, build size and build time.
- [Parsec]
    - Upgraded parsec-se-driver to 0.6.0.
    - Upgraded parsec-service to 0.8.1.
    - Upgraded parsec-tool to 0.4.0.
- [CoreDNS]
    - CoreDNS now uses 172.21.1.0/30 as the default IP address internally. If this IP conflicts with the local network, you can override the default address with a script.
    - Added a new starter service for CoreDNS to reduce the amount of log spam.
- [mbed-fcce] Upgraded factory-configurator-client-example to v4.11.1.
- [Maestro] Upgraded to release 3.0.0.
    - Removed devicedb dependency.
    - Removed greaslib and complimentary patches.
    - Simplified logging and configuration of Maestro.
- [devicedb] removed.
    - Replicated functionality internal in Maestro and Edge-Core.
- [other]
    - Enabled SoftHSM in targets by default (not used though). This prevents TPM-related error log entries generated when using Parsec.
    - Added Yocto "Hardknott" compatibility.
    - Replaced vim with nano.

## Bug fixes

- Fixed a bug that caused very large OSTree updates (100 MB) to fail.
- You can now create a delta patch for firmware update by comparing any two firmware versions. Previously, you always had to compare the new firmware version to the original base version.

    For example, to update to v1.2, you can now create a delta patch by comparing v1.1 to 1.2, whereas previously you had to create a delta patch comparing v1.0 to v1.2, if v1.0 was your original base version.

- Removed warning about `SOTA_PACKED_CREDENTIALS`.
- Fixed file/folder usage to make the build more compatible with OSTree directory usage.
- Removed software TPM and all related limitations and known issues.

## Known issues

- Alpine Linux/muslc has issues with DNS lookups when used from containers. For more details, see [Deploying Containers/DNS issues](https://github.com/PelionIoT/pelion-dm-edge-docs/blob/f62fe9335f4c37a29668cea563084d9413290740/docs/container-orchestration/deploying.md#dns-issues-with-muslc--alpine-linux).
- The new `golang`-based terminal has some stability issues that can lead to connection loss. Click **Reopen Terminal** to reconnect.
- The Pelion Device Management portal isn't correctly updated after a firmware campaign in some instances.
- [Maestro] The FeatureMgmt config resource is initialized with a maximum file content of 3.8KB. The remaining file content is truncated during initialization. This is most likely due to the limitation of the Gorilla WebSocket library but needs further investigation. However, you can still push a file of up to 64KB through cloud service APIs.
- [pt-example] `cpu-temperature` device reports random values because the default CPU temperature file isn't the same on Yocto and LmP.
- [Container integration with Parsec](../container/security-parsec-container.html) doesn't work on the Raspberry Pi 3 Model B+.
- When using the Notification service API, if you subscribe to a translated device's LwM2M resources, which are registered with operation write (PUT) or execute (POST), you won't receive notification of the device state change.
- [AVNet ZU3EG] If you enable the [PREEMPT](https://cateee.net/lkddb/web-lkddb/PREEMPT.html) kernel configuration item, the LmP release, including PetaLinux 2020.2, doesn't work in a stable manner. PREEMPT is disabled in the default configuration. If you have any issues with this configuration, please contact Xilinx support.
- [AVNet ZU3EG] You can program the Ethernet MAC address to the EEPROM on the board. Please see [the Xilinx support documentation](https://www.xilinx.com/support/answers/70176.html) on how to do this with the `i2c` commands.
- When using Wi-Fi, the device shutdown can take longer than expected because Edge Core takes 1m 35s to shut down.

## Limitations

- Firmware update from Edge 2.4 to Edge 2.5 is not possible with the AVNET ZU3EG platform because the UEFI Capsule Update requires a new partition for `grub`.
- There is a maximum size limit to the full registration message, which limits the number of devices Pelion Edge can host:
   - Maximum registration message size is 64KB.
   - Hosted devices with five typical Resources consume ~280B (the exact size depends, for example, on the length of resource paths). This limits the maximum number to 270 devices.
   - The more Resources you have, the fewer devices can be supported.
   - The Pelion Edge device Resources are also included in the same registration message.
   - **Test the limits with your configuration and set guidance accordingly.**
- Devices behind Pelion Edge don't support [auto-observation](https://www.pelion.com/docs/device-management/current/connecting/device-guidelines.html#auto-observation).
- Pelion Device Management Client-enabled devices must bootstrap to the Pelion Device Management cloud before connecting to Pelion Edge.
- No moving devices are supported (such as the device moving from Pelion Edge to another edge device.)
- LmP's base partition table is set above 10GB to support three upgrade images in OSTree. Therefore, we only support SD card installation (compared to supporting onboard EMMC or NAND) for the i.MX 8M Mini EVK and the UltraZed-EG IOCC.

# Pelion Edge 2.4.2 - 26th Aug 2021

- Fixed kubelet initialization to enable containers to properly inherit host DNS settings.
- Fixed race condition causing coreDNS to intermittently fail to start.

# Pelion Edge 2.4.1 - 5th Aug 2021

- Fixed the compilation error by locking down the crate versions of `parsec-tool` and `parsec-se-driver`.

# Pelion Edge 2.4.0 - 28th June 2021

### New features

- Updated [LmP version to 81](https://foundries.io/products/releases/81/).
- Added secure edge container applications with Parsec and the Trust Platform Module (TPM) v2.0:
   - You can access the TPM resource through Parsec APIs from within the container application.
   - The storage of secure assets, such as keys, is separated on a per-client basis: Assets created by one client can't be accessed by another.
   - You can use `securityContext` in the Pod specifications to restrict the privileges and access control of an application to system resources.
- [edge-core] Updated Edge Core to [0.18.0](https://github.com/PelionIoT/mbed-edge/releases/tag/0.18.0):
   - Starts Edge Core service `After=network-online.target`, after which the gateway acquires an IP address.
   - Restricts access to Mbed configuration `/userdata/mbed`, setting permissions to 700 with `owner=root`.
   - Adds `BYOC_MODE` build flow, so you can inject the certificates into the image at run time rather than compile time. This enables you to create generic developer builds.
- [edge-tool] Added [Edge tool v0.2.0](https://github.com/PelionIoT/mbed-edge/tree/master/edge-tool):
   - Installs only when built with `BYOC_MODE=ON`.
   - Converts the development certificate to a CBOR configuration object, which is then provided to Edge Core as a command-line argument.
- [New board support] The following boards are now supported in LmP:
    - RPi3B+.
    - RPi4.
    - SolidRun Hummingboard Ripple and SolidRun Hummingboard Pulse when booting from SD card over `imx8mmsolidrun` meta layer.
           - Added i.MX8MM SolidRun meta layer.
- [Container orchestration] Network policy controller:
   - Adds [kube-router](https://github.com/PelionIoT/kube-router) and [coredns](https://coredns.io/) and removes older edge-net to support sophisticated networking policies between containers and the host.
   - Allows kubelet and kube-router to function without external network. Supports offline cache mode.
- [Board-specific improvements]: 
    - UZ3EG - align wks.in file usage with i.MX8.
    - RPI - use miniUART  BT dtoverlay to enable Bluetooth.
             - Use standard TTY console config.
             - BLE now work correctly.
    - New recipe for Nordic Semiconductor´s `pc-ble-driver`, configuration in `local.conf` which defaults to **off** .
- [Other] Changed default image file name for MFG tool - `MFGTOOL_FLASH_IMAGE = "console-image-lmp"`.
- [Space conservation] Removed meta-arm-autonomy layer.
- [Upgrades]:
    - Added support for full image update.
    - Prevented duplicate deployment. Modified to check if the commit has been deployed before making the deployment. This prevents a previous deploy from being over-written, which, in turn, would break the rollback functionality.
- [Parsec] Upgraded parsec-se-driver to 0.5.0, parsec-service to 0.7.2 and parsec-tool to 0.3.0:
   - Sets the Parsec socket directory permission to 0750.
- [Image] [Simplified the partition layout](https://github.com/PelionIoT/meta-mbed-edge/pull/51).
- [build process] Generic `mx8mm` support - Instead of using the i.MX8MM EVK target, you can use the i.MX8MM to generalize the support because the current changes can run on all targets, generalizing to the SoC level target `mx8mm`.
- [OS general]: 
    - Replaced `networkmanager-nmtui` with `networkmanager-nmcli`.
    - Enabled Wi-Fi by default for all targets.
- [golang] Removed golang overrides (1.14.4) to use native version 1.15.8 provided by current Yocto branch.
- [edge-proxy] Modified `edge-proxy` configuration to add new forwarding address for containers domain. 
   - Added `containers.local` to the list of known hosts.
- Updated `info` utility to v2.0.9 and `identity-tool` to v2.0.8.
- [fluentbit] Reduced the default FluentBit logging level to warning.
- [mbed-fcce] Upgraded factory-configurator-client-example to v4.9.0.
- It is no longer required to specify `vendor-id=42fa7b48-1a65-43aa-890f-8c704daade54` to unlock the rich node features in Pelion Device Management Portal. Portal now reads the gateway capabilities from gateway's FeatureMgmt LwM2M object 33457 and then enables the UI associated to the features.

### Bug fixes

- [info] Fixed issue whereby `info` command required `root` access on all LmP supported boards.
- [info] Fixed issue whereby the `info` command on the UltraZed-EG IOCC attempted to read the default Linux thermal zones, which don't exist in Xilinx BSPs. Added support for [Xilinx AMS](https://xilinx-wiki.atlassian.net/wiki/spaces/A/pages/18842163/Zynq+UltraScale+MPSoC+AMS) feature for correct temperature reading.
- Fixed issue whereby the LmP updates didn't accept firmware updates with numbers 10 and 100. 

### Known issues

- The Pelion Device Management portal isn't correctly updated after a firmware campaign in some instances.
- [maestro] The FeatureMgmt config resource is initialized with a maximum 3.8KB of file content. The remaining file content is truncated during initialization. This is most likely due to the limitation of the gorilla/websocket library but needs further investigation. However, you can still push a file size of a maximum of 64KB through cloud service APIs.
- [pt-example] `cpu-temperature` device reports random values because the default CPU temperature file isn't the same on Yocto and LmP.
- The LmP build enables software TPM and Parsec stacks by default in all configurations, including developer certificate configurations. However, because it won't be used or set up in those configurations, the logs will show some TPM related errors. These logs can be ignored.
- [Container integration with Parsec](https://developer.pelion.com/docs/device-management-edge/v2.4/container/security-parsec-container.html) doesn't work on the the Raspberry Pi 3 Model B+.
- When using the Notification service API, if you subscribe to a translated device's LwM2M resources, which are registered with operation write (PUT) or execute (POST), you won't receive notification of the device state change.
- [AVNet ZU3EG] If you enable kernel configurations [CPU_IDLE](https://cateee.net/lkddb/web-lkddb/CPU_IDLE.html) and [PREEMPT](https://cateee.net/lkddb/web-lkddb/PREEMPT.html), the LmP release including PetaLinux 2020.2 doesn't work in a stable manner. The default configuration has those disabled. If you have any issues with those configurations, please contact Xilinx support.
- [AVNet ZU3EG] You can program the Ethernet MAC address to the EEPROM on the board. Please see [the Xilinx support documentation](https://www.xilinx.com/support/answers/70176.html) on how to do this with the `i2c` commands.

### Limitations

- Firmware update from Edge 2.2 to Edge 2.3, from Edge 2.3 to Edge 2.4 and from Edge 2.2 to Edge 2.4 isn't possible on any of the supported platforms. Partition table changes and in some cases FPGA support changes prevent the upgrading between these versions. To update between these versions, manual flashing is required. OTA update is still supported within the versions.
- There is a maximum size limit to the full registration message, which limits the number of devices Pelion Edge can host:
   - Maximum registration message size is 64KB.
   - Hosted devices with five typical Resources consume ~280B (the exact size depends, for example, on the length of resource paths). This limits the maximum number to 270 devices.
   - The more Resources you have, the fewer devices can be supported.
   - The Pelion Edge device Resources are also included in the same registration message.
   - **Test the limits with your configuration, and set guidance accordingly.**
- Devices behind Pelion Edge don't support [auto-observation](https://www.pelion.com/docs/device-management/current/connecting/device-guidelines.html#auto-observation).
- Pelion Device Management Client enabled devices must first boostrap to the Pelion Device Management cloud before connecting to Pelion Edge.
- No moving devices are supported (such as the device moving from Pelion Edge to another edge device.)
- LmP's base partition table is set above 10GB to support three upgrade images in OSTree. Therefore, we only support SD card installation (compared to supporting onboard EMMC or NAND) for the i.MX 8M Mini EVK and the UltraZed-EG IOCC.
- Software TPM is [not designed to be resilient](https://sourceforge.net/p/ibmswtpm2/discussion/general/thread/fc5f4e0daf/) against power failures. Instead of disconnecting the power supply to the gateway, always perform a graceful shutdown of the edge device when using software TPM. To resolve this, follow the troubleshooting section of [our documentation about using Pelion Edge with TPM](developer.pelion.com/device-management-edge/v2.4/security/security-tpm.html#troubleshooting).


# Pelion Edge 2.3.0 - 1st April 2021

### New features

This release adds features to the Linux microPlatform (LmP) OS, which supports NXP's i.MX8 development platform i.MX 8M Mini EVK and AVNet's Xilinx MPSoC Starter kit UltraZed-EG IOCC. This release:

- [TPM] Introduces [Secure Pelion Edge with the Trusted Platform Module (TPM) v2.0](https://developer.pelion.com/docs/device-management-edge/latest/secure-with-tpm.html):
   - [meta-parsec] Leverages [Platfrom Abstraction for Security (Parsec)](https://parallaxsecond.github.io/parsec-book/index.html) to interface with TPM and adds a [new meta layer](https://github.com/PelionIoT/meta-parsec) to build `parsec` service 0.6.0.
   - [swtpm] `meta-parsec` layer also brings in [IBM's software TPM](https://sourceforge.net/projects/ibmswtpm2/) `swtpm` package. If your hardware supports physical TPM, we recommend you comment out this package from the `console-image-lmp.bb` file.
   - [parsec-se-driver] Adds a recipe to build [Parsec Secure Element driver](https://github.com/parallaxsecond/parsec-se-driver) 0.4.0, which is a dependency of Edge Core and mbed-fcce package when compiled with `MBED_EDGE_CORE_CONFIG_PARSEC_TPM_SE_SUPPORT=ON`.
   - Adds `meta-rust`, `meta-clang` and `meta-security/meta-tpm`, which are prerequisites to build Parsec and related packages.
   - [parsec-tool] Adds a recipe to build [Parsec Tool](https://github.com/parallaxsecond/parsec-tool), a command-line utility to debug and cross-validate the working of Parsec service and TPM.
- [edge-core] Updates Edge Core to [0.16.1](https://github.com/PelionIoT/mbed-edge/blob/master/CHANGELOG.md#release-0160-2021-3-15).
   - Reduces the default log level to WARN.
   - Adds `mbed_cloud_client_user_config.h`, so you can set the values for your use case. This overwrites the default config options set by Edge Core. The default lifetime value is set to 1800s (30min).
   - Explicitly defines HTTP_PROXY and HTTPS_PROXY environment variables.
- [edge-examples] Updates examples to [0.16.0](https://github.com/PelionIoT/mbed-edge-examples/blob/master/CHANGELOG.md#release-0160-2021-03-15).
- [mbed-fcce] Upgrades factory-configurator-client-example to v4.7.1.
   - Renames the package name from `mbed-fcc` to `mbed-fcce`.
   - Explicitly defines HTTP_PROXY and HTTPS_PROXY environment variables.
- [verified-logging] By default, the gateway is configured with persistent journal logging for LMP UltraZed-EG IOCC and i.MX 8M Mini EVK. To disable persistent logging, set flag `VOLATILE_LOG_DIR = "no"` in `local.conf`, and update the `Storage` in recipes-core/systemd/systemd-conf/journald.conf. Note: If you disable persistent logging, the FSS feature won't work.
- Updates `identity-tool`, `kubelet` and `info-tool` package source file protocol from SSH to HTTPS.

### Bug fixes

- [pt-example] Pelion Edge 2.2 used protocol translator example 0.13.0, which wasn't compatible with Edge Core 0.15.0. We fixed this by upgrading the example to version 0.16.0.
- In Pelion Edge 2.2, using the i.MX 8M Mini EVK in production mode with firmware update enabled failed with a FOTA_ASSERT after the reboot. This has been fixed.

### Known issues

- The Pelion Device Management portal is not correctly updated after a firmware campaign in some instances.
- [maestro] The FeatureMgmt config resource is initialized with a maximum 3.8KB of file content. The remaining file content is truncated during initialization. This is most likely due to the limitation of the gorilla/websocket library but needs further investigation. However, you can still push a file size of a maximum of 64KB through cloud service APIs.
- [pt-example] `cpu-temperature` device reports random values because the default CPU temperature file is not the same on Yocto and LmP.
- [info] The `info` command must be run with `sudo` on LMP-based boards (UltraZed-EG IOCC and i.MX 8M Mini EVK).
- [info] The `info` command on the UltraZed-EG IOCC attempts to read the CPU temperature when the temperature file does not exist. This results in a cat error message.
- The LmP build will enable SW TPM and Parsec stacks by default in all configuration, including developer certificate configurations. However, as it will not be used or set up in those configurations the logs will show some TPM related errors - those logs can be ignored.

#### AVNET ZU3EG

- If you enable kernel configurations [CPU_IDLE](https://cateee.net/lkddb/web-lkddb/CPU_IDLE.html) and [PREEMPT](https://cateee.net/lkddb/web-lkddb/PREEMPT.html), the LmP release including PetaLinux 2020.2 does not work in a stable manner. Our default configuration has those disabled. If you have any issues with those configurations, please contact Xilinx support.
- You cannot do firmware update from Edge 2.2 to Edge 2.3 on the AVNET ZU3EG board due to LmP v79 release FPGA-support changes. The changes have interdepencies between the BOOT image and kernel image and as in the current update you can only update ther kernel image it fails to boot up correctly with the Edge 2.2 based BOOT image (as it does not supply the required updated device tree/FPGA files etc.). So, update to Edge 2.3 image must be done with manual flashing on ZU3EG targets.

### Limitations

- There is a maximum size limit to the full registration message, which limits the number of devices Edge can host:
   - Maximum registration message size is 64KB.
   - Hosted devices with five typical Resources consume ~280B (the exact size depends, for example, on the length of resource paths). This limits the maximum number to 270 devices.
   - The more Resources you have, the fewer devices can be supported.
   - The Pelion Edge device Resources are also included in the same registration message.
   - **Test the limits with your configuration, and set guidance accordingly.**
- Devices behind Pelion Edge don't support [auto-observation](https://www.pelion.com/docs/device-management/current/connecting/device-guidelines.html#auto-observation).
- Pelion Device Management Client enabled devices must first boostrap to the Pelion Device Management cloud before connecting to Pelion Edge.
- No moving devices are supported (such as the device moving from Pelion Edge to another edge device.)
- LmP's base partition table is set above 10GB to support three upgrade images in OSTree. Therefore, we only support SD card installation (compared to supporting onboard EMMC or NAND) for the i.MX 8M Mini EVK and the UltraZed-EG IOCC.

### Important note

While provisioning your gateway, please use `vendor-id=42fa7b48-1a65-43aa-890f-8c704daade54` to unlock the rich node features, such as gateway logs and gateway terminal in the Pelion web portal.

# Pelion Edge 2.2.0 - February 2021

## New features

In this release of Pelion Edge 2.2, we introduce support for two additional platforms and one additional Yocto distribution operating system:

- Now supported by Pelion Edge is the Linux microPlatform (LmP) OS, running on NXP's [i.MX8 development platform imx8mmevk](https://www.nxp.com/design/development-boards/i-mx-evaluation-and-development-boards/evaluation-kit-for-the-i-mx-8m-mini-applications-processor:8MMINILPD4-EVK), an excellent choice for typical gateway solutions
- AVNet's [UltraZed-EG Starter kit](https://www.avnet.com/shop/us/products/avnet-engineering-services/aes-zu3eg-1-sk-g-3074457345635014225/) based on Xilinx Zynq UltraScale+ MPSoC. This FPGA capable is the perfect choice when you need to support some legacy HW with modern, cost-efficient HW without doing your own custom SoC.
- Pelion Edge continues support of Yocto's Poky OS on the Raspberry PI3.

[Quick start guides](https://developer.pelion.com/docs/device-management-edge/2.2/quick-start/index.html) are available for all three platforms and should be followed individually because the build systems have differences. meta-pelion-edge and meta-mbed-edge are the basis for all Pelion Edge programs. Although the operating systems are different in support models, Pelion Edge has been tested to perform and operate the same within both OS environments. Moreover, both operating systems have the same feature set, other than the following difference:

   - [FOTA Update] Pelion Edge on Poky OS continues to use overlayFS for upgrades as it did in previous releases. Pelion Edge on LmP OS uses a new mechanism called [OSTree](https://ostreedev.github.io/ostree/).

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
- [FluentBit integration](https://developer.pelion.com/docs/device-management-edge/2.2/managing/logs.html) - allows you to upload logs to the cloud, filter log dynamically.
   - Added recipe to install Fluent Bit 1.3.5 on the gateway for providing an open source log processor and forwarder solution.
   - By default, Fluent Bit is configured with the following input endpoints - CPU, MEM, Systemd services - edge-core, edge-proxy, pelion-relay-term, Maestro, kubelet, Docker and wait-for-pelion-identity. FluentBit is also configured with an output endpoint HTTP to publish logs at API endpoint: `http://gateways.local:8080/v3/device-logs` (routing through the edge-proxy service).
- [Verified logging](https://developer.pelion.com/docs/device-management-edge/2.2/managing/verified-logging.md) - allows you to sign the logs in the device to prevent log manipulation.
   - [journald] Enabled Forward Secure Sealing (FSS) feature of systemd journal.
   - To configure Pelion Edge gateway with a sealing key and to keep track of the verification key in production setup, use Pelion Edge Provisioner (PEP) tool [v2.3.0](https://github.com/PelionIoT/pelion-edge-provisioner/releases/tag/v2.3.0).
   - By default, the gateway is configured with persistent journal logging for Yocto Poky Raspberry PI3. To disable persistent logging, set flag `VOLATILE_LOG_DIR = "no"` in `local.conf`, and update the `Storage` in recipes-core/systemd/systemd-conf/journald.conf. Note: If you disable persistent logging, the FSS feature won't work.
   - By default, the gateway is configured **without** persistent journal logging for LMP `uz3eg-iocc` and `imx8mmevk`. To disable persistent logging, set flag `VOLATILE_LOG_DIR = "no"` in `local.conf`, and update the `Storage` in recipes-core/systemd/systemd-conf/journald.conf. Note: If you disable persistent logging, the FSS feature won't work.
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
      - LmP's equivalent image is named `lmp-console-image`.
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

### Bug fixes

- After production factory flow, if you ran the `info` command before Edge Core paired with the cloud, the `info` command showed `N/A` for the deviceID while displaying connected. This has been fixed.
- Streamlined the startup sequence of Pelion Edge programs to remove the cyclic dependency, which caused many starts and stops of processes in certain conditions.
- When conducting back-to-back production factory flow with the Pelion Edge Provisioner, the mcc_config directory sometimes was not written correctly, and upon reboot, Edge Core didn't connect properly. This has been fixed.

### Known issues

- The Pelion Device Management portal is not correctly updated after a firmware campaign in some instances.
- [maestro] The FeatureMgmt config resource is initialized with a maximum 3.8KB of file content. The remaining file content is truncated during initialization. This is most likely due to the limitation of the gorilla/websocket library but needs further investigation. However, Pelion Device Management users can still push a file size of a maximum of 64KB through cloud service APIs.
- [pt-example] `cpu-temperature` device reports random values because the default CPU temperature file is not the same on Yocto and LmP.
- [info] The `info` command must be ran with `sudo` on LMP based boards (uz3eg-iocc & imx8mmevk)
- [info] The `info` command on the `uz3eg-iocc` attempts to read the cpu temperature when the temperature file does not exist. This results in a cat error message.

#### Xilinx ZU3EG

If you enable kernel configurations [CPU_IDLE](https://cateee.net/lkddb/web-lkddb/CPU_IDLE.html) and [PREEMPT](https://cateee.net/lkddb/web-lkddb/PREEMPT.html), the LmP release including PetaLinux 2020.2 does not work in a stable manner. Our default configuration has those disabled. If you have any issues with those configurations, please contact Xilinx support.

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
- LmP's base partition table is set above 10GB to support three upgrade images in OSTree. Therefore, we only support SD card installation (compared to supporting onboard EMMC or NAND) for the `imx8mmevk` and the `uz3eg-iocc`.

### Important note

While provisioning your gateway, please use `vendor-id=42fa7b48-1a65-43aa-890f-8c704daade54` to unlock the rich node features, such as gateway logs and gateway terminal in the Pelion web portal.

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
