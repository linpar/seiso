/* 
 * Copyright 2013-2015 the original author or authors.
 * 
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
 */
package com.expedia.seiso.aop;

/**
 * @author Willie Wheeler
 */
public class AdvisorOrder {
	
	// The numbers reflect priority: lower number means higher priority.
	// High priority runs first on the way in and last on the way out.
	// Note that the priorities apply on a per-advisee basis. For a given advisee, the transaction would close before
	// we send the AMQP notification. But after both those things happen, control can pop into a context where there's
	// a live transaction to close. This happens e.g. with ItemSaver (tx close, AMQP notify) and ItemServiceImpl (tx
	// close following the ItemSaver notification).
	public static final int NOTIFICATION_ADVISOR_ORDER = 0;
	public static final int TRANSACTION_ADVISOR_ORDER = 100;
	public static final int SET_AGGREGATE_ROTATION_STATUS_ADVISOR_ORDER = 400;
}
