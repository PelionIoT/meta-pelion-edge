obj-m := panic.o

SRC := $(shell pwd)

all:
	$(MAKE) -C $(KERNEL_SRC) M=$(SRC)

modules_install:
	$(MAKE) -C $(KERNEL_SRC) M=$(SRC) modules_install

clean:
	rm -f *.o *~ core .depend .*.cmd *.ko *.mod.c
	rm -f Module.markers Module.symvers modules.order
	rm -rf .tmp_versions Modules.symvers



#example given at: http://www.unix.com/unix-for-dummies-questions-and-answers/131850-simulating-kernel-panic.html
# Makefile
# Intendations have to be tabs, not spaces
# UNAME := $(shell uname -r)
# KERNEL26 := 2.6
# KERNELVERSION := $(findstring $(KERNEL26),$(UNAME))

# ifeq ($(KERNELVERSION),2.6)

# obj-m := panic.o

# INCLUDE := -I/usr/include/asm/
# KDIR := /lib/modules/$(shell uname -r)/build
# PWD := $(shell pwd)

# all::
# 	$(MAKE) -C $(KDIR) $(INCLUDE) SUBDIRS=$(PWD) modules
 
# else

# TARGET := panic
# INCLUDE := -I/lib/modules/`uname -r`/build/include -I/usr/include/asm/
# CFLAGS := -O2 -Wall -DMODULE -D__KERNEL__ -DLINUX
# CC := gcc

# all:: ${TARGET}.o

# ${TARGET}.o: ${TARGET}.c
# 	$(CC) $(CFLAGS) ${INCLUDE} -c ${TARGET}.c
 
# endif