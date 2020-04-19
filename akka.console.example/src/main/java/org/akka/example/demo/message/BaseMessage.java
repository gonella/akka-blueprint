package org.akka.example.demo.message;

public class BaseMessage {

	private Integer priority;
	private Integer id;
	private String type;

	public BaseMessage(Integer priority) {
		this.setPriority(priority);

	}

	public BaseMessage() {

	}

	public Integer getPriority() {
		return priority;
	}

	public void setPriority(Integer priority) {
		this.priority = priority;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
}
