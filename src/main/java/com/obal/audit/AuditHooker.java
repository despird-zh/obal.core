/*
 * Licensed to the G.Obal under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  G.Obal licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 * 
 */
package com.obal.audit;

import com.obal.disruptor.EventHooker;
import com.obal.disruptor.EventPayload;
import com.obal.disruptor.EventType;
import com.obal.exception.RingEventException;

public class AuditHooker extends EventHooker<AuditInfo>{

	public AuditHooker() {
		super(EventType.AUDIT);
	}

	@Override
	public void processPayload(EventPayload payload) throws RingEventException {
		AuditInfo ai = (AuditInfo)payload;
		
		System.out.println("---:"+ai.getKey());
	}


}