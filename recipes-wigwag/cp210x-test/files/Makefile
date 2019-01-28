LDFLAGS ?= -lpthread -pthread 
# TWSOVERSION is the compiler version...
# see http://rute.2038bug.com/node26.html.gz

CXX ?= g++ -g -O0 -fPIC -std=c++11
CC ?= gcc -g -O0 -fPIC
AR ?= ar

LIBUVDIR= libuv-v1.10.1

CXXFLAGS= -fPIC -std=c++11

ARCH ?=x86 
#ARCH=armel	
SYSCALLS= syscalls-$(ARCH).c

ALLOBJS= $($<:%.cpp=%.o)

DEBUG_OPTIONS=-rdynamic -D_TW_TASK_DEBUG_THREADS_ -DLOGGER_HEAVY_DEBUG
#-D_TW_BUFBLK_DEBUG_STACK_
CROSS_INCLUDE ?= /usr/include
CROSS_LDFLAGS ?= -L/usr/lib
CFLAGS=  $(GLIBCFLAG) -D_TW_DEBUG -I./include  -D__DEBUG   -fPIC -I./deps/$(LIBUVDIR)/include -I./deps/build/include  -L./deps/build/lib -DGREASE_LIB

DEBUG_CFLAGS= -g -DERRCMN_DEBUG_BUILD $(DEBUG_OPTIONS)

ROOT_DIR=.
OUTPUT_DIR=.

EXTRA_TARGET=

CFLAGS+= -fPIC

GLIBCFLAG=-D_USING_GLIBC_
LD_TEST_FLAGS= -lgtest

## concerning the -whole-archive flags: http://stackoverflow.com/questions/14889941/link-a-static-library-to-a-shared-one-during-build
## originally we used that when creating the node module version of greaseLogger - but apparently needed for tcmalloc here also
LDFLAGS += -L./deps/build/lib
#  -lTW -luv -ldl

# -Wl,-whole-archive deps/build/lib/libtcmalloc_minimal.a -Wl,-no-whole-archive 
STATIC_LIB_FLAGS= -static deps/build/lib/libuv.a deps/build/lib/libTW.a

HRDS= include/TW/tw_bufblk.h  include/TW/tw_globals.h  include/TW/tw_object.h include/TW/tw_stack.h\
include/TW/tw_dlist.h   include/TW/tw_llist.h    include/TW/tw_socktask.h    include/TW/tw_syscalls.h\
include/TW/tw_macros.h include/TW/tw_globals.h include/TW/tw_alloc.h include/TW/tw_sparsehash.h include/TW/tw_densehash.h\
include/TW/tw_stringmap.h

#SRCS_CPP= error-common.cc logger.cc standalone_test_logsink.cc grease_lib.cc

SRCS_CPP= cp210x_gpio_example.cpp
#OBJS= $(SRCS_CPP:%.cc=$(OUTPUT_DIR)/%.o) $(SRCS_C:%.c=$(OUTPUT_DIR)/%.o)
#OBJS_NAMES= $(SRCS_CPP:%.cc=$%.o) $(SRCS_C:%.c=%.o)

##tw_sparsehash.h

## The -fPIC option tells gcc to create position 
## independant code which is necessary for shared libraries. Note also, 
## that the object file created for the static library will be 
## overwritten. That's not bad, however, because we have a static 
## library that already contains the needed object file.

#$(OUTPUT_DIR)/%.o: %.cc
#	$(CXX) $(CXXFLAGS) $(CFLAGS) -c $< -o $@

#$(OUTPUT_DIR)/%.o: %.c
#	$(CC) $(CFLAGS) -c $< -o $@

#CFLAGS+= -DGREASE_LIB -I./deps/$(LIBUVDIR)/include -I./deps/twlib/include  $(DEBUG_CFLAGS) -I$(CROSS_INCLUDE)
all: 
	$(CXX) $(CXXFLAGS) $(CFLAGS)  $(SRCS_CPP)  -o cp210xtest





#install: tw_lib $(EXTRA_TARGET)
#	./install-sh $(TWSOVERSION) $(INSTALLPREFIX)
#	ln -sf $(INSTALLPREFIX)/lib/$(TWSONAME) $(INSTALLPREFIX)/lib/$(TWSOVERSION) && \
#	ln -sf $(INSTALLPREFIX)/lib/$(TWSONAME) $(INSTALLPREFIX)/lib/$(TWSOLIBNAME)


clean: 
	-rm -rf $(OUTPUT_DIR)/*.o $(OUTPUT_DIR)/*.obj $(OUTPUT_DIR)/*.rpo $(OUTPUT_DIR)/*.idb $(OUTPUT_DIR)/*.lib $(OUTPUT_DIR)/*.exe $(OUTPUT_DIR)/*.a $(OUTPUT_DIR)/*~ $(OUTPUT_DIR)/core
	-rm -rf Debug
	-rm -f $(TWSOLIBNAME) $(TWSONAME) $(TWSOVERSION)
	-rm -f cp210xtest
# DO NOT DELETE