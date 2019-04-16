/*
 * Copyright 2019-present Open Networking Foundation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.p4.p4d2.tutorial.common;

import org.onosproject.core.ApplicationId;
import org.onosproject.net.DeviceId;
import org.onosproject.net.PortNumber;
import org.onosproject.net.flow.DefaultFlowRule;
import org.onosproject.net.flow.DefaultTrafficSelector;
import org.onosproject.net.flow.DefaultTrafficTreatment;
import org.onosproject.net.flow.FlowRule;
import org.onosproject.net.flow.criteria.PiCriterion;
import org.onosproject.net.group.DefaultGroupBucket;
import org.onosproject.net.group.DefaultGroupDescription;
import org.onosproject.net.group.DefaultGroupKey;
import org.onosproject.net.group.GroupBucket;
import org.onosproject.net.group.GroupBuckets;
import org.onosproject.net.group.GroupDescription;
import org.onosproject.net.group.GroupKey;
import org.onosproject.net.pi.model.PiTableId;
import org.onosproject.net.pi.runtime.PiAction;

import java.nio.ByteBuffer;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static org.p4.p4d2.tutorial.AppConstants.DEFAULT_FLOW_RULE_PRIORITY;

public final class Utils {

    public static GroupDescription forgeMulticastGroup(
            ApplicationId appId,
            DeviceId deviceId,
            int groupId,
            Set<PortNumber> ports) {

        checkNotNull(deviceId);
        checkNotNull(appId);
        checkArgument(!ports.isEmpty());

        final GroupKey groupKey = new DefaultGroupKey(
                ByteBuffer.allocate(4).putInt(groupId).array());

        final List<GroupBucket> bucketList = ports.stream()
                .map(p -> DefaultTrafficTreatment.builder()
                        .setOutput(p).build())
                .map(DefaultGroupBucket::createAllGroupBucket)
                .collect(Collectors.toList());

        return new DefaultGroupDescription(
                deviceId,
                GroupDescription.Type.ALL,
                new GroupBuckets(bucketList),
                groupKey, groupId, appId);
    }

    public static FlowRule forgeFlowRule(DeviceId switchId, ApplicationId appId,
                                   PiTableId tableId, PiCriterion piCriterion, PiAction piAction) {
        return DefaultFlowRule.builder()
                .forDevice(switchId)
                .forTable(tableId)
                .fromApp(appId)
                .withPriority(DEFAULT_FLOW_RULE_PRIORITY)
                .makePermanent()
                .withSelector(DefaultTrafficSelector.builder()
                                      .matchPi(piCriterion).build())
                .withTreatment(DefaultTrafficTreatment.builder()
                                       .piTableAction(piAction).build())
                .build();
    }
}