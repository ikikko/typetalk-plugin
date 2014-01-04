package org.jenkinsci.plugins.typetalk;

import com.google.api.client.util.Key;

public class Message {

	@Key
	private String message;

	@Key
	private Long replyTo;

	@Key
	private String[] fileKeys;

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

	public String[] getFileKeys() {
		return fileKeys;
	}

	public void setFileKeys(String[] fileKeys) {
		this.fileKeys = fileKeys;
	}

}
