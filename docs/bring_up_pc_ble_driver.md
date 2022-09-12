# Setting up a  pc-ble-driver

This repository has a recipe in recipes-connectivity/pc-ble-driver/pc-ble-driver_4.1.4.bb
for fetching and building the `pc-ble-driver`, which is hosted and developed in
[https://github.com/NordicSemiconductor/pc-ble-driver](https://github.com/NordicSemiconductor/pc-ble-driver).

That repository also contains:

- The documentation to flash and connect the BLE module using the driver.
- Example applications.

By default, the `pc-ble-driver` is not built because not all hardware supports it and because the use case may not require it even if the hardware has support.

To configure `pc-ble-driver` for your board:

1. Enable the driver in `local.conf`, found in `./.repo/manifests/conf/local.conf`.

   Modify this section:

   ```
   # Uncomment following line to get pc-ble-driver into image.
   # IMAGE_INSTALL:append = " pc-ble-driver"
   ```

   to:

   ```
   # Uncomment following line to get pc-ble-driver into image.
   IMAGE_INSTALL:append = " pc-ble-driver"
   ```

1. Build:

   `bitbake console-image-lmp`

Implementing the actual application using the driver is out of scope of this documentation.
