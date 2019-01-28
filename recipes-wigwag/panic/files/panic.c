// panic.c
#include <linux/module.h>

static int __init panic_init_module(void) {
    panic(" panic has been called");
}

module_init(panic_init_module);
MODULE_LICENSE("GPL");
