package org.unicen.operation.event;

import org.unicen.eventdriver.Listener;
import org.unicen.eventdriver.test.OnStartEvent;
import org.unicen.operation.event.OnFailEvent;
import org.unicen.operation.event.OnFinishEvent;

public interface StartFinishListener extends Listener {

	void onStart(OnStartEvent onStartEvent);
	
	void onFinish(OnFinishEvent onFinishEvent);
	
	void onFail(OnFailEvent onFailEvent);
}
