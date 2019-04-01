#this directive informs the bitbake recipe to look "here" first when sourcing files
#because of this, when the recipe runs, it will find dot.bashrc in "thisdir's" share directory

FILESEXTRAPATHS_prepend := "${THISDIR}/${PN}:"

#if we wanted to add in a file, we could do ths too, but in our case we ju
#SRC_URI += "file://fstab"
