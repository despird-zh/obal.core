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
package com.obal.audit.aop;

import java.util.List;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.CodeSignature;

import com.obal.aop.AspectUtils;
import com.obal.aop.CacheTestAccessor;
import com.obal.aop.DemoBean;

@Aspect
public abstract class AuditAspect {

	@Pointcut
	public abstract void beforeOperation();

	@Before("beforeOperation()")
	public void beforeOperation(JoinPoint jp) {
		
		Object[] paramValues = jp.getArgs();
		String[] paramNames = ((CodeSignature) jp.getStaticPart()
				.getSignature()).getParameterNames();

		StringBuilder logLine = new StringBuilder(jp.getStaticPart()
				.getSignature().getName()).append("(");

		if (paramNames.length != 0)
			AspectUtils.logParamValues(logLine, paramNames, paramValues);
		logLine.append(") - started");
		AspectUtils.getLogger(jp).info(logLine.toString());
	}

	@Pointcut
	public abstract void afterOperation();
	
	@AfterReturning(pointcut="afterOperation()",returning="rtv")
	public void afterOperation(Object rtv,JoinPoint jp) {

		Object target = jp.getTarget();
		CacheTestAccessor cta = (CacheTestAccessor)target;
		DemoBean dp= cta.getdp();
		System.out.println("DemoP is:"+dp.demoStr);
		
		if (rtv != null && (!(rtv instanceof List) || ((List) rtv).size() != 0)) {
			StringBuilder rv = new StringBuilder("Return Value : ");
			rv.append(AspectUtils.toString(rtv));
			AspectUtils.getLogger(jp).info(rv.toString());
		}
		
		Object[] paramValues = jp.getArgs();
		String[] paramNames = ((CodeSignature) jp.getStaticPart()
				.getSignature()).getParameterNames();
		StringBuilder logLine = new StringBuilder(jp.getStaticPart()
				.getSignature().getName()).append("(");
		if (paramNames.length != 0)
			AspectUtils.logParamValues(logLine, paramNames, paramValues);
		logLine.append(") - finished");
		AspectUtils.getLogger(jp).info(logLine.toString());
	}
}
 
 
