package org.jenkinsci.plugins.discuss;

import com.google.api.client.util.Key;

public class Message {

	@Key
	private Long topicId;

	@Key
	private String message;

	@Key
	private Long replyTo;

	public Long getTopicId() {
		return topicId;
	}

	public void setTopicId(Long topicId) {
		this.topicId = topicId;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public Long getReplyTo() {
		return replyTo;
	}

	public void setReplyTo(Long replyTo) {
		this.replyTo = replyTo;
	}

}
