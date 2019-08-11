/*
 * #%L
 * wcm.io
 * %%
 * Copyright (C) 2019 wcm.io
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
package io.wcm.samples.core.testcontext;

import static com.day.cq.commons.jcr.JcrConstants.JCR_CREATED;
import static com.day.cq.commons.jcr.JcrConstants.JCR_LASTMODIFIED;
import static com.day.cq.commons.jcr.JcrConstants.JCR_MIXINTYPES;
import static com.day.cq.commons.jcr.JcrConstants.JCR_PRIMARYTYPE;
import static com.day.cq.wcm.api.NameConstants.NN_RESPONSIVE_CONFIG;
import static com.day.cq.wcm.api.NameConstants.PN_CREATED_BY;
import static com.day.cq.wcm.api.NameConstants.PN_LAST_MOD_BY;
import static com.day.cq.wcm.api.NameConstants.PN_PAGE_LAST_MOD_BY;
import static com.day.cq.wcm.api.NameConstants.PN_PAGE_LAST_REPLICATED;
import static com.day.cq.wcm.api.NameConstants.PN_PAGE_LAST_REPLICATED_BY;
import static com.day.cq.wcm.api.NameConstants.PN_PAGE_LAST_REPLICATION_ACTION;
import static com.day.cq.wcm.msm.api.MSMNameConstants.NT_LIVE_SYNC_CONFIG;
import static com.day.cq.wcm.msm.api.MSMNameConstants.PN_LAST_ROLLEDOUT;
import static com.day.cq.wcm.msm.api.MSMNameConstants.PN_LAST_ROLLEDOUT_BY;

import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.osgi.service.component.annotations.Component;

import com.adobe.cq.export.json.SlingModelFilter;
import com.google.common.collect.ImmutableSet;

@Component(service = SlingModelFilter.class)
public class MockSlingModelFilter implements SlingModelFilter {

  private static final Set<String> IGNORED_NODE_NAMES = ImmutableSet.of(
      NN_RESPONSIVE_CONFIG,
      NT_LIVE_SYNC_CONFIG,
      "cq:annotations");

  private static final Set<String> IGNORED_PROPERTIES = ImmutableSet.of(
      JCR_PRIMARYTYPE,
      JCR_MIXINTYPES,
      JCR_CREATED,
      JCR_LASTMODIFIED,
      ResourceResolver.PROPERTY_RESOURCE_TYPE,
      "sling:resourceSuperType",
      PN_CREATED_BY,
      PN_LAST_MOD_BY,
      PN_PAGE_LAST_MOD_BY,
      PN_PAGE_LAST_REPLICATED,
      PN_PAGE_LAST_REPLICATED_BY,
      PN_PAGE_LAST_REPLICATION_ACTION,
      PN_LAST_ROLLEDOUT,
      PN_LAST_ROLLEDOUT_BY,
      "cq:LiveCopy");

  @Override
  public Map<String, Object> filterProperties(Map<String, Object> properties) {
    return properties.entrySet()
        .stream()
        .filter(entry -> !IGNORED_PROPERTIES.contains(entry.getKey()))
        .collect(Collectors.toMap(Entry::getKey, Entry::getValue));
  }

  @Override
  public Iterable<Resource> filterChildResources(Iterable<Resource> childResources) {
    return StreamSupport
        .stream(childResources.spliterator(), false)
        .filter(entry -> !IGNORED_NODE_NAMES.contains(entry.getName()))
        .collect(Collectors.toList());
  }

}
