package org.unicen.operation.event;

import org.unicen.eventdriver.EventListener;

public interface StartFinishListener extends EventListener {

	void onStart(OnStartEvent onStartEvent);
	
	void onFinish(OnFinishEvent onFinishEvent);
	
	void onFail(OnFailEvent onFailEvent);
}
