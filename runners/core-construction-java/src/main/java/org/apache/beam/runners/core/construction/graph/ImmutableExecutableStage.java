/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.beam.runners.core.construction.graph;

import com.google.auto.value.AutoValue;
import com.google.common.collect.ImmutableSet;
import java.util.Collection;
import java.util.stream.Collectors;
import org.apache.beam.model.pipeline.v1.RunnerApi.Components;
import org.apache.beam.model.pipeline.v1.RunnerApi.Environment;
import org.apache.beam.runners.core.construction.graph.PipelineNode.PCollectionNode;
import org.apache.beam.runners.core.construction.graph.PipelineNode.PTransformNode;

/** An {@link ExecutableStage} which is constructed with all of its initial state. */
@AutoValue
public abstract class ImmutableExecutableStage implements ExecutableStage {
  public static ImmutableExecutableStage ofFullComponents(
      Components components,
      Environment environment,
      PCollectionNode input,
      Collection<SideInputReference> sideInputs,
      Collection<UserStateReference> userStates,
      Collection<PTransformNode> transforms,
      Collection<PCollectionNode> outputs) {
    Components prunedComponents =
        components
            .toBuilder()
            .clearTransforms()
            .putAllTransforms(
                transforms
                    .stream()
                    .collect(Collectors.toMap(PTransformNode::getId, PTransformNode::getTransform)))
            .build();
    return of(prunedComponents, environment, input, sideInputs, userStates, transforms, outputs);
  }

  public static ImmutableExecutableStage of(
      Components components,
      Environment environment,
      PCollectionNode input,
      Collection<SideInputReference> sideInputs,
      Collection<UserStateReference> userStates,
      Collection<PTransformNode> transforms,
      Collection<PCollectionNode> outputs) {
    return new AutoValue_ImmutableExecutableStage(
        components,
        environment,
        input,
        ImmutableSet.copyOf(sideInputs),
        ImmutableSet.copyOf(userStates),
        ImmutableSet.copyOf(transforms),
        ImmutableSet.copyOf(outputs));
  }

  @Override
  public abstract Components getComponents();

  // Redefine the methods to have a known order.
  @Override
  public abstract Environment getEnvironment();

  @Override
  public abstract PCollectionNode getInputPCollection();

  @Override
  public abstract Collection<SideInputReference> getSideInputs();

  @Override
  public abstract Collection<UserStateReference> getUserStates();

  @Override
  public abstract Collection<PTransformNode> getTransforms();

  @Override
  public abstract Collection<PCollectionNode> getOutputPCollections();
}
