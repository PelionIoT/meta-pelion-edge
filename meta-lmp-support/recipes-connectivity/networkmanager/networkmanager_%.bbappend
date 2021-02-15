FILESEXTRAPATHS_prepend := "${THISDIR}/files:"

# Patch the IPv6 to use EUI64 as default
# If you want to enable the non-eui64 IP addresses, you can disable this patch

SRC_URI += "file://addr_gen_mode_eui64.patch"

