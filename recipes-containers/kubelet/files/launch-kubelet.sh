#!/bin/bash
# ----------------------------------------------------------------------------
# Copyright (c) 2021, Pelion and affiliates.
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


exec EDGE_BIN/kubelet \
--v=4 \
--root-dir=EDGE_KUBELET_STATE \
--offline-cache-path=EDGE_KUBELET_STATE/store \
--fail-swap-on=false \
--image-pull-progress-deadline=2m \
--hostname-override=${DEVICE_ID} \
--kubeconfig=EDGE_KUBELET_STATE/kubeconfig \
--cni-bin-dir=EDGE_OPT/cni/bin \
--cni-conf-dir=EDGE_CNI \
--network-plugin=cni \
--register-node=true \
--node-status-update-frequency=150s \
--runtime-cgroups=/systemd/system.slice \
--kubelet-cgroups=/systemd/system.slice \
--resolv-conf=EDGE_RUN/coredns/resolv.conf \
--hosts-path=EDGE_RUN/coredns/hosts
