# Changelog

## Release 2.0.0 - 2020-05-18
### Added
* [mbed-edge] Upgraded mbed-edge aka edge-core from 0.8.0 to 0.12.0
* [maestro] Fixed DHCP client in maestro daemon to reorder options/parameters and to add right options to discover packet
* [maestro] Running maestro process with Go env flag - GODEBUG=madvdontneed=1
* [devicedb] Upgraded devicedb from 1.9.2 to 1.9.4
* [mbed-devicejs-bridge] Mapped new device function - accelerometer, gravity sensor, magnetometer, pressure, TVOC, gyroscope, signal strength, tap detection, CO2, step counter, Euler angles and heading to LwM2M objects and resources.
* [relay-term] Added websocket ping-pong handler for relay-term.
* [ble-pt] Added features to BLE protocol translator
	* APIs to scan the gateway for BLE devices and report the mac address, name and rssi of all discovered devices.
	* APIs to allow user to dynamically onboard a BLE device found in the response of the above API.
	* Disabled the static whitelist of BLE service UUIDs.
	* Added support for Nordic Thingy and Embedded Planet Agora board.
* [os] Switched init system from SysVinit to SystemD
* [os] Introduced gai.conf to control the sorting order of the addresses resolved by libc library. By default IPv4 is preferred over IPv6.
* [os] Removed the cronjob which periodically restarted the gateway services.


### Bug Fixes
* Fixed the situation where the maestro daemon can run out of the system resources and cause the platform to reboot.
* Fixed the issue of DHCP client not able to renew the IP address lease.
* Fixed the intermittent failure of remote terminal not connecting to Pelion cloud.
* Fixed the intermittent bug in maestro daemon where it doesn't restart a monitoring process which exits unexpectedly.

### Known Issues
* While provisioning your gateway, please use vendor-id=42fa7b48-1a65-43aa-890f-8c704daade54 to unlock the rich node features like Gateway logs and Gateway terminal in the Pelion web portal.

## 1.0.0
### Summary Abstract 
This is the 1.0 release of the Pelion Edge for Gateways.  In general, it is a Yocto meta layer to build an operating system for a gateway that will connect to the arm Pelion Cloud.   Once provisioned with the Pelion Cloud, the gateway can register Bluetooth devices to the cloud, can participate in secure update campaigns, and be controlled from a mobile application.   The first supported platform is the Raspberry PI3b+.    
### Features Description 
- Gateway based service enabling edge applications to interact with gateway connected devices through a REST API. 
- Systems management API and daemon (Maestro) 
  - Dynamic system configuration 
  - Logging pushed to the Pelion Cloud 
- Protocol translator engine with example Bluetooth implementation
- Supports upgrade campaigns from Pelion Cloud 
  - Securely downloads edge gateway firmware updates 
  - Keeps deployed gateways up to date 
- Cloud services support gateway features for edge applications, such as: 
  - Remote access 
  - Remote terminal (Preview feature) 
  - Device data collection and query 
  - Edge alerting 
  - Real-time device control and configuration 
- REST APIs available locally on the gateway to read, write and observe device’s states.  
- Virtual Device Driver application – ability to create different types of virtual devices on the gateway.  

### Installation / Useage 
Follow the instructions for building the operating system for Raspberry PI at the readme for this github project. 
### Help / Issues
Follow the readme.  For issues file Github issues
### Known Issues 
#### System Software 
- There is a known situation where the maestro daemon can run out of system resources and cause the platform to reboot.  (E19-264) 
- On some networks, the IP address can become disconnected and DHCP fails to re-obtain an address.  To remedy this problem, restart the gateway. (E19-243) 
- Sometimes the preview feature 'remote terminal' fails to connect.  In this instance, the workaround is to restart the gateway. 

#### Cloud Service 
- Sending 1-2K back to back requests to the Pelion Cloud "accounts" service causes a 500 return.  After the requests clear, in a few seconds, the service is again available.  (E19-381) 
- While provisioning your gateway, please use vendor-id=42fa7b48-1a65-43aa-890f-8c704daade54 to unlock some of the functionalities like Gateway logs and Gateway terminal in the Pelion web portal (E19-419) 
