package com.android.domji84.mcgridview.events;

import com.google.common.eventbus.EventBus;

/**
 * Created by domji84 on 19/11/14.
 */
public class EventBusProvider {

	private static final EventBus EVENT_BUS = new EventBus();

	/**
	 * Use this to post events to enable de-coupling of various components
	 * @return
	 */
	public static EventBus getEventBusInstance() {
		return EVENT_BUS;
	}

	private EventBusProvider() {
		// don't allow instantiation
	}

}
