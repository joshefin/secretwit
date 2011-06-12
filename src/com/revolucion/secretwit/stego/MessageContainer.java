/*******************************************************************************
 * Copyright (c) 2011 Zeljko Zirikovic.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GPL which 
 * accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package com.revolucion.secretwit.stego;

public class MessageContainer {

	private StringBuilder message;

	public MessageContainer(String message) {
		this.message = new StringBuilder(message);
	}

	public String getMessage() {
		return (message != null) ? message.toString() : "";
	}

	public String takeFirst(int charsNumber) {
		if (message == null || message.length() == 0)
			return null;

		if (charsNumber > message.length())
			return getMessage();

		String substring = message.substring(0, charsNumber);
		message.delete(0, charsNumber);

		return substring;
	}

	public boolean isEmpty() {
		return (message == null || message.length() == 0);
	}
}
