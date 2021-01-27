SUMMARY = "Adds the version number file for overfs updater"

LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/Apache-2.0;md5=89aea4e17d99a7cacdbeed46a0096b10"

SRC_URI = "file://BUILDMMU.txt"

PR = "r0"

S = "${WORKDIR}"

FILES_${PN} = " \
/wigwag \
/wigwag/wwrelay-utils \
/wigwag/wwrelay-utils/conf \
/wigwag/etc \
"

do_compile() {
	BUILDMMU=$(cat ${S}/BUILDMMU.txt)
	VER_FILE=${S}/version.json
	if [ -e $VER_FILE ] ; then
		rm $VER_FILE
	fi
	echo  "{" > $VER_FILE
	echo  "   "  \"version\" ":" \"0.0.1\", >> $VER_FILE
	echo  "   "  \"packages\" ":" [{ >> $VER_FILE
	echo  "      "  \"name\" ":" \"WigWag-Firmware\", >> $VER_FILE
	echo  "      "  \"version\" ":" \"${BUILDMMU}\", >> $VER_FILE
	echo  "      "  \"description\" ":" \"Base Factory deviceOS\", >> $VER_FILE
	echo  "      "  \"node_module_hash\" ":" \"\", >> $VER_FILE
	echo  "      "  \"ww_module_hash\" ":" \"\" >> $VER_FILE
	echo  "   "  }]  >> $VER_FILE
	echo  "}" >> $VER_FILE

}

do_install() {
	install -d ${D}/wigwag/wwrelay-utils/conf
	install -d ${D}/wigwag/etc
    install -m 0755 ${S}/version.json ${D}/wigwag/wwrelay-utils/conf/versions.json
	install -m 0755 ${S}/version.json ${D}/wigwag/etc/versions.json
}


