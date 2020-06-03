# Updating Pelion Edge Yocto image

This section explains how to:

- [Generate a firmware update package](#generate-a-firmware-update-package) you can push to a device through a firmware update campaign.
- [Initiate a firmware update campaign](#initiate-a-firmware-update-campaign).

## Generate a delta firmware update package

Follow the instructions provided in the readme of [scripts-pelion-edge](https://github.com/armPelionEdge/scripts-pelion-edge/tree/master) to create a delta update package.

## Initiate a firmware update campaign
Device Management pushes your firmware update package to a defined set of devices, which unpack the firmware update package and apply the updates within it.
You can initiate a firmware update campaign targeting any registered device from Device Management Portal.

<span class="notes">**Note:** You can also initiate a firmware update campaign using the APIs, [as explained in the Pelion Device Management online documentation](https://www.pelion.com/docs/device-management/current/updating-firmware/update-api-tutorial.html).</span>

**To initiate a firmware update campaign:**
1. Upload the firmware update tar.gz package to Pelion Device Management:
    1. Log in to Device Management Portal.
    1. From the left navigation pane, select **Firmware Update** > **Images**.
    1. Click the **+ Upload Image** button.
    1. Follow the instructions on the screen to upload the tar.gz file.
        After you upload the file, Device Management Portal displays a URL from which devices can download the tar.gz file.
1. Create a firmware update manifest:
   1. Use the manifest-tool utility to [create a manifest file](https://github.com/ARMmbed/manifest-tool#creating-manifests) for your firmware update tar.gz package:
      ```bash
      manifest-tool create -u <firmware.url> -p <firmware-update.tar.gz> -o manifest
      ```

      Where:

      - `<firmware.url>` is the URL of the firmware update tar.gz package, as shown in Device Management Portal. Devices use this URL to download the firmware update image.
      - `<firmware-update.tar.gz>` is the firmware update package tar.gz file. The manifest-tool utility calculates a hash from the firmware update tar.gz.
      - Make sure `.manifest_tool.json` is in the current directory.
      - Make sure `.update-certificates/` folder is in the current directory.
1. Upload the firmware update manifest to Device Management:
   1. From the left navigation pane, select **Firmware Update** > **Manifests**.
   1. Click the **+ Upload Manifest** button.
   1. Follow the instructions on the screen to upload the manifest file.
1. Create a device filter to select a set of registered devices that should receive the firmware update package:
   1. From the left navigation pane, select **Device directory** > **Devices**.
   1. In the grey bar above the list of devices, click the arrow next to **Filters**.
   1. Choose an attribute and operator, and give a value, such as **Device ID**.

      - To combine multiple attributes in one filter, click **Add another**.
      - To use a raw string instead, click **Advanced view**.

   1. Click **Save**.
      This opens the **Filter name** popup window.

   1. Give your filter a name.
   1. Click **Save filter**.
1. Create the campaign:
    1. From the left navigation pane, select **Firmware Update** > **Update campaigns**.
    1. Click the **+ New Campaign** button.
    1. Populate the **Name** and **Description** (optional) fields.
    1. From the **Manifest** dropdown list, select the manifest file uploaded earlier.
    1. From the **Filter** dropdown list, select the filter that targets the devices you need to update.
    1. Click **Finish** to start the campaign.