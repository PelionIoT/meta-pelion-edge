#!/bin/bash
# ----------------------------------------------------------------------------
# Copyright (c) 2020, Arm Limited and affiliates.
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

DEVICE_ID=`jq -r .deviceID /userdata/edge_gw_config/identity.json`
if [[ $? -ne 0 ]] || [[ $DEVICE_ID == null ]]; then
    echo "Unable to extract device ID from identity.json"
    exit 1
fi

/wigwag/system/bin/launch-edgenet.sh
if [ $? -ne 0 ]; then
    echo "Unable to create edgenet docker network"
    exit 2
fi

exec /wigwag/system/bin/kubelet \
--root-dir=/var/lib/kubelet \
--offline-cache-path=/wigwag/system/var/lib/kubelet/store \
--fail-swap-on=false \
--image-pull-progress-deadline=2m \
--hostname-override=${DEVICE_ID} \
--kubeconfig=/wigwag/system/var/lib/kubelet/kubeconfig \
--cni-bin-dir=/wigwag/system/opt/cni/bin \
--cni-conf-dir=/wigwag/system/etc/cni/net.d \
--network-plugin=cni \
--register-node=true
