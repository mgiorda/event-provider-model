package org.unicen.operation.support;

import java.util.List;
import java.util.Objects;

import org.unicen.operation.OperationContext;
import org.unicen.operation.SimpleOperation;

public class SequentialSimpleOperation implements SimpleOperation {

	private final List<SimpleOperation> steps;

	public SequentialSimpleOperation(List<SimpleOperation> steps) {
		
		Objects.requireNonNull(steps, "Steps cannot be null");

		this.steps = steps;
	}
	
	@Override
	public void execute(OperationContext context) {
		
		for(SimpleOperation operation : steps) {
			operation.execute(context);
		}
	}
}
