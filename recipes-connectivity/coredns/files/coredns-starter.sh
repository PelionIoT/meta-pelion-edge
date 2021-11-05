#!/usr/bin/env bash
# ----------------------------------------------------------------------------
# Copyright (c) 2021, Pelion and affiliates.
#
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
# ----------------------------------------------------------------------------
OFFLINE_WAIT_DURATION=5
ONLINE_WAIT_DURATION=20

check_network_device() {
    if [[ ! -e /sys/class/net/kube-bridge ]]; then
        return 2
    fi

    state=$(cat /sys/class/net/kube-bridge/operstate)
    if [[ $state = "down" ]]; then
        return 1
    fi

    return 0
}

while true; do
    systemctl is-active --quiet coredns
    
    if [[ $? -eq 0 ]]; then
        # Service is already running
        sleep $ONLINE_WAIT_DURATION
        continue
    fi 

    check_network_device

    if [[ $? -eq 0 ]]; then
        echo "Starting coredns.service"
        systemctl start coredns.service
        sleep $ONLINE_WAIT_DURATION
        continue
    fi

    # Service is dead and device is NOT ready
    sleep $OFFLINE_WAIT_DURATION
done
