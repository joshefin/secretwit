/*******************************************************************************
 * Copyright (c) 2011 Zeljko Zirikovic.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GPL which 
 * accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package com.revolucion.secretwit.stego;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.imageio.ImageIO;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StegoUtils {
	
	private static final Logger logger = LoggerFactory.getLogger(StegoUtils.class);
	
	private static final String PIC_TAG = "[pic]";
	private static final String URL_TAG = "[url]";
	private static final String IMAGE_WATERMARK_TEXT = "SECRETWIT";

	private StegoUtils() {}

	public static String encodeTweet(String message, String secret) {
		// System.out.println("\nencodeTweet(\"" + message + "\", \"" + secret + "\")");
		MessageContainer hiddenMessageContainer = new MessageContainer(secret);

		// If message has [pic], use twitpic link to hide data
		if (message.contains(PIC_TAG) && !hiddenMessageContainer.isEmpty()) {
			message = message.replace(PIC_TAG, URLSteganography.makePictureLink(hiddenMessageContainer.takeFirst(4)));
			
			// System.out.println("encodeTweet - [pic]: " + message);
		}

		// If message has [url], use bitly link to hide data
		if (message.contains(URL_TAG) && !hiddenMessageContainer.isEmpty()) {
			message = message.replace(URL_TAG, URLSteganography.makeUrlLink(hiddenMessageContainer.takeFirst(4)));
			
			// System.out.println("encodeTweet - [url]: " + message);
		}

		// If there are chars left in hidden message, use whitespaces to hide
		// data
		if (!hiddenMessageContainer.isEmpty()) {
			String whitespaces = WhitespaceSteganography.encode(hiddenMessageContainer.getMessage());
			message += whitespaces;
			message += WhitespaceSteganography.MESSAGE_ENDING;
			
			// System.out.println("encodeTweet - whitespaces: " + message);
		}

		return message;
	}

	public static String decodeTweet(String text) {
		StringBuilder decodedMessage = new StringBuilder();
		
		// System.out.println("\ndecodeTweet(\"" + text + "\")");

		// Decode twipic url
		if (text.contains(URLSteganography.TWITPIC_URL)) {
			// Pattern pattern = Pattern.compile(HideInLink.TWITPIC_URL + "([A-Za-z0-9-]{6})[ !?.,;:]");
			Pattern pattern = Pattern.compile(URLSteganography.TWITPIC_URL + "([A-Za-z0-9-]{6})");
			Matcher matcher = pattern.matcher(text);
			if (matcher.find()) {
				String value = matcher.group(1);
				String decoded = URLSteganography.decode(value);
				if (decoded != null && !decoded.isEmpty()) {
					if (Character.isLetterOrDigit(decoded.charAt(0))) {
						decodedMessage.append(URLSteganography.decode(value));
						
						// System.out.println("decodeTweet - decoded twitpic: " + decodedMessage);
					}
				}
			}
		}

		// Decode bitly url
		if (text.contains(URLSteganography.BITLY_URL)) {
			// Pattern pattern = Pattern.compile(HideInLink.BITLY_URL + "([A-Za-z0-9-]{6})[ !?.,;:]");
			Pattern pattern = Pattern.compile(URLSteganography.BITLY_URL + "([A-Za-z0-9-]{6})");
			Matcher matcher = pattern.matcher(text);
			if (matcher.find()) {				
				String value = matcher.group(1);
				String decoded = URLSteganography.decode(value);
				if (decoded != null && !decoded.isEmpty()) {
					if (Character.isLetterOrDigit(decoded.charAt(0))) {
						decodedMessage.append(decoded);
						
						// System.out.println("decodeTweet - decoded bitly: " + decodedMessage);
					}
				}
			}
		}

		// Decode whitespaces
		
		StringBuilder secret = new StringBuilder();
		
		int lastIndex = text.lastIndexOf("&nbsp;");
		if (lastIndex > 0)
			lastIndex--;
		else
			lastIndex = text.length() - 1;
		
		for (int i = lastIndex; i >= 0; i--) {
			char c = text.charAt(i);
			
			if (c != ' ' && c != '\t' && !Character.isSpaceChar(c))
				break;
			
			secret.append(c);
		}
		
		if (secret.length() > 0) {
			secret.reverse();
			decodedMessage.append(WhitespaceSteganography.decode(secret.toString()));
			
			// System.out.println("decodeTweet - decoded whitespaces: " + decodedMessage);
		}
		
		/*
		if (text.contains(WhitespaceSteganography.MESSAGE_ENDING)) {
			Pattern pattern = Pattern.compile("[.,!?;: ]([ \t]+)" + WhitespaceSteganography.MESSAGE_ENDING);
			Matcher matcher = pattern.matcher(text);
			if (matcher.find()) {
				String value = matcher.group(1);
				decodedMessage.append(WhitespaceSteganography.decode(value));
			}
		}
		*/
		
		return decodedMessage.toString();
	}
	
	public static boolean doesProfileImageHaveWatermark(BufferedImage image) {
		if (image == null)
			return false;
		
		String watermark = ImageSteganography.reveal(image);
		
		return (watermark != null && watermark.equals(IMAGE_WATERMARK_TEXT));
	}
	
	public static BufferedImage watermarkProfileImage(String profileImageUrl) {
		return watermarkProfileImage(profileImageUrl, IMAGE_WATERMARK_TEXT);
	}
	
	public static BufferedImage watermarkProfileImage(String profileImageUrl, String text) {
		if (profileImageUrl == null || profileImageUrl.isEmpty())
			return null;
		
		if (text == null || text.isEmpty())
			return null;
		
		URL imageUrl = null;
		try {
			imageUrl = new URL(profileImageUrl);
		}
		catch (MalformedURLException e) {
			imageUrl = null;
		}
		
		if (imageUrl == null)
			return null;
		
		BufferedImage image = null;
		try {
			image = ImageIO.read(imageUrl);
		}
		catch (IOException e) {
			logger.error("Can't get profile image. {}", e.getMessage());
		}
		
		if (image == null)
			return null;
		
		String secret = ImageSteganography.reveal(image);
		if (secret != null && !secret.isEmpty()) {
			logger.info("Image already has watermark.");
			return null;
		}
		
		if (ImageSteganography.hide(text, image)) {
			logger.info("Watermark applied.");
			return image;
		}
		
		logger.warn("Failed to insert watermark.");
		
		return null;
	}
	
	public static void main(String[] args) {
		// String message = "http://bit.ly/YyAyMg here is my new pictures @ [url] ! the best - [pic] lolz :)";
		String message = "http://twitpic.com/3s5jiu - Wassup crew? Need u guys to go get S&M on ITunes NOW! Let's go!!! #BOOM";
		System.out.println("message >> " + message);
		String secret = "venac 22:30";
		System.out.println("secret >> " + secret);
		String encoded = encodeTweet(message, secret);
		System.out.println("encoded: [" + encoded + "]");
		String decoded = decodeTweet(encoded);
		System.out.println("decoded: [" + decoded + "]");
	}
	
}
