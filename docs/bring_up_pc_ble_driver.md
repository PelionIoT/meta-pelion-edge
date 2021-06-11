# Setting up a pc-ble-driver

This repository has a recipe in [recipes-connectivity/pc-ble-driver/pc-ble-driver_4.1.4.bb]
for fetching and building the `pc-ble-driver`, which is hosted and developed in
[https://github.com/NordicSemiconductor/pc-ble-driver].

That repository also contains all the documentation needed for flashing and connecting the BLE module,
using the driver and also many useful example applications.

By default the `pc-ble-driver` is not built, as not all hardware support it and it may not be needed by the use case even if the HW had support.

Follow these setups to configure `pc-ble-driver` for your board

1. Enable driver on your `local.conf`, found in ```./.repo/manifests/conf/local.conf```

Ie. modify this section in it
```
# Uncomment following line to get pc-ble-driver into image.
# IMAGE_INSTALL_append = " pc-ble-driver"
```
to
```
# Uncomment following line to get pc-ble-driver into image.
IMAGE_INSTALL_append = " pc-ble-driver"
```

1. Build, eg. ```bitbake console-image-lmp```

Implementing the actual application using the driver is out of scope of this documentation.
