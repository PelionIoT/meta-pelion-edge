# Pelion Edge builds require the FIT signing keys to be available
# before compiling rather than allowing the build system to generate
# a new key so that firmware updates work properly.
do_compile_prepend() {
    if [ ! -e "${MBL_KEYSTORE_DIR}/${MBL_FIT_ROT_KEY_FILENAME}" ]; then
        bbfatal "Missing FIT image signing key: ${MBL_KEYSTORE_DIR}/${MBL_FIT_ROT_KEY_FILENAME}"
    fi

    if [ ! -e "${MBL_KEYSTORE_DIR}/${MBL_FIT_ROT_KEY_CERT_FILENAME}" ]; then
        bbfatal "Missing FIT image certificate: ${MBL_KEYSTORE_DIR}/${MBL_FIT_ROT_KEY_CERT_FILENAME}"
    fi
}
