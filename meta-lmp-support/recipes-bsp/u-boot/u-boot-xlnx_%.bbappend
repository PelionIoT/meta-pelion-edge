COMPATIBLE_MACHINE = "uz"

# do_deploy is skipped if valid in the shared state cache and copied to DEPLOY_DIR_IMAGE,
# which is missed in install of openembedded-core/u-boot.inc
addtask deploy before do_install
