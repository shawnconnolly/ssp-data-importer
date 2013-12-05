/**
 * Licensed to Jasig under one or more contributor license
 * agreements. See the NOTICE file distributed with this work
 * for additional information regarding copyright ownership.
 * Jasig licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a
 * copy of the License at:
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.jasig.ssp.util.importer.job.processor;


import org.jasig.ssp.util.importer.job.domain.RawItem;

import org.springframework.batch.item.ItemProcessor;


/**
 * Dummy {@link ItemProcessor} for prototype purposes that just echos the {@link RawItem} it's been given.
 *
 */
public class ProcessedItemEchoProcessor implements ItemProcessor<RawItem,RawItem> {
    @Override
    public RawItem process(RawItem item) throws Exception {
    	
        return item;
    }
}
