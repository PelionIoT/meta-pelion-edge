#!/bin/bash
# ----------------------------------------------------------------------------
# Copyright (c) 2021, Pelion Ltd.
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

POD_CIDR=EDGE_PODCIDR

exec env NODE_NAME=${DEVICE_ID} KUBE_ROUTER_CNI_CONF_FILE=EDGE_RUN/10-kuberouter.conflist EDGE_BIN/kube-router \
--v=1 \
--kubeconfig=EDGE_KUBELET_STATE/kubeconfig \
--run-firewall=true \
--run-service-proxy=false \
--run-router=true \
--master=127.0.0.1:10355 \
--enable-overlay=false \
--enable-ibgp=false \
--hostname-override=${DEVICE_ID} \
--pod-cidr=${POD_CIDR}
