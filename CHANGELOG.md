# 1.0.0
## Summary Abstract 
This is the 1.0 release of the Pelion Edge for Gateways.  In general, it is a Yocto meta layer to build an operating system for a gateway that will connect to the arm Pelion Cloud.   Once provisioned with the Pelion Cloud, the gateway can register Bluetooth devices to the cloud, can participate in secure update campaigns, and be controlled from a mobile application.   The first supported platform is the Raspberry PI3b+.    
## Features Description 
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

## Installation / Useage 
Follow the instructions for building the operating system for Raspberry PI at the readme for this github project. 
## Help / Issues
Follow the readme.  For issues file Github issues
## Known Issues 
### System Software 
- There is a known situation where the maestro daemon can run out of system resources and cause the platform to reboot.  (E19-264) 
- On some networks, the IP address can become disconnected and DHCP fails to re-obtain an address.  To remedy this problem, restart the gateway. (E19-243) 
- Sometimes the preview feature 'remote terminal' fails to connect.  In this instance, the workaround is to restart the gateway. 

### Cloud Service 
- Sending 1-2K back to back requests to the Pelion Cloud "accounts" service causes a 500 return.  After the requests clear, in a few seconds, the service is again available.  (E19-381) 
- While provisioning your gateway, please use vendor-id=42fa7b48-1a65-43aa-890f-8c704daade54 to unlock some of the functionalities like Gateway logs and Gateway terminal in the Pelion web portal (E19-419) 
