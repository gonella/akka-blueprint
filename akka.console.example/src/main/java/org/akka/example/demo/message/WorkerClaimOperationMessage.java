package org.akka.example.demo.message;

public class WorkerClaimOperationMessage extends BaseMessage {
	private Integer number = -1;

	public WorkerClaimOperationMessage() {
		super(0);
	}

	public WorkerClaimOperationMessage(Integer priority) {
		super(priority);
	}

	public WorkerClaimOperationMessage(Integer number, Integer priority) {
		super(priority);

		this.number = number;

	}

	public Integer getNumber() {
		return number;
	}
}
