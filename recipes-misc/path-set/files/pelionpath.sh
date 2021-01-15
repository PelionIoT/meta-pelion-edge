# Copyright (c) 2018-2021, Pelion and affiliates.
# SPDX-License-Identifier: Apache-2.0
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#     http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.

export HAVEGLOBAL="yes"
export PS1='\h:\w\$ '
umask 022

# You may uncomment the following lines if you want `ls' to be colorized:
color_prompt=yes
PS1='${debian_chroot:+($debian_chroot)}\[\033[01;32m\]\u@\h\[\033[00m\]:\[\033[01;34m\]\w\
\[\033[00m\]\$ '
# enable color support of ls and also add handy aliases
if [ -x /usr/bin/dircolors ]; then
	test -r ~/.dircolors && eval "$(dircolors -b ~/.dircolors)" || eval "$(dircolors -b)"
	alias ls='ls --color=auto'
	alias grep='grep --color=auto'
	alias fgrep='fgrep --color=auto'
	alias egrep='egrep --color=auto'
fi

export NODE_PATH="/wigwag/devicejs-core-modules/node_modules/" 
export LD_PRELOAD="/usr/lib/libcrypto.so.1.0.2"
export PATH="/wigwag/system/bin:/wigwag/system/lib:/wigwag/system/lib/bash:$PATH"
export LD_LIBRARY_PATH="/wigwag/system/lib:/wigwag/system/lib/bash:$LD_LIBRARY_PATH"
export BASHLIBS="/wigwag/system/lib/bash/:$BASHLIBS"

