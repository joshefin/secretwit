/*******************************************************************************
 * Copyright (c) 2011 Zeljko Zirikovic.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GPL which 
 * accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package com.revolucion.secretwit.test;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.revolucion.secretwit.stego.URLSteganography;
import com.revolucion.secretwit.stego.MessageContainer;
import com.revolucion.secretwit.stego.WhitespaceSteganography;


public class StegoTest {

	public StegoTest() {
		String realMessage = "Ljudi pogledajte ovu sliku [pic]! Jos ima na [url].";
		String hiddenMessage = "Venac,19:35";
		String finalMessage = new String(realMessage);
		
		MessageContainer hiddenMessageContainer = new MessageContainer(hiddenMessage);
		
		// ENCODE

		// If message has [pic], use twitpic link to hide data
		if (finalMessage.contains("[pic]") && !hiddenMessageContainer.isEmpty()) {
			finalMessage = finalMessage.replace("[pic]", URLSteganography.makePictureLink(hiddenMessageContainer.takeFirst(4)));
		}
		
		// If message has [url], use bitly link to hide data
		if (finalMessage.contains("[url]") && !hiddenMessageContainer.isEmpty()) {
			finalMessage = finalMessage.replace("[url]", URLSteganography.makeUrlLink(hiddenMessageContainer.takeFirst(4)));
		}
		
		// If there are chars left in hidden message, use whitespaces to hide data
		if (!hiddenMessageContainer.isEmpty()) {
			String whitespaces = WhitespaceSteganography.encode(hiddenMessageContainer.getMessage());
			finalMessage += whitespaces;
			finalMessage += WhitespaceSteganography.MESSAGE_ENDING;
		}
		
		System.out.println("Encoded: [" + finalMessage + "]");
		System.out.println(finalMessage.length());
		
		// DECODE
		
		StringBuilder decodedMessage = new StringBuilder();
		
		// Decode twipic url
		if (finalMessage.contains(URLSteganography.TWITPIC_URL)) {
			Pattern pattern = Pattern.compile(URLSteganography.TWITPIC_URL + "([A-Za-z0-9-]+)[ !?.,;:]");
			Matcher matcher = pattern.matcher(finalMessage);
			if (matcher.find()) {
				String value = matcher.group(1);
				decodedMessage.append(URLSteganography.decode(value));
			}
		}
		
		// Decode bitly url
		if (finalMessage.contains(URLSteganography.BITLY_URL)) {
			Pattern pattern = Pattern.compile(URLSteganography.BITLY_URL + "([A-Za-z0-9-]+)[ !?.,;:]");
			Matcher matcher = pattern.matcher(finalMessage);
			if (matcher.find()) {
				String value = matcher.group(1);
				decodedMessage.append(URLSteganography.decode(value));
			}
		}
		
		// Decode whitespaces
		if (finalMessage.contains(WhitespaceSteganography.MESSAGE_ENDING)) {
			Pattern pattern = Pattern.compile("[.,!?;: ]([ \t]+)" + WhitespaceSteganography.MESSAGE_ENDING);
			Matcher matcher = pattern.matcher(finalMessage);
			if (matcher.find()) {
				String value = matcher.group(1);
				decodedMessage.append(WhitespaceSteganography.decode(value));
			}
		}
		
		System.out.println("Decoded: [" + decodedMessage + "]");
	}

	public static void main(String[] args) {
		new StegoTest();
	}
}
