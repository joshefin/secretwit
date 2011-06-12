/*******************************************************************************
 * Copyright (c) 2011 Željko Zirikoviæ.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package com.revolucion.secretwit.stego;

import java.io.UnsupportedEncodingException;

import org.apache.commons.codec.binary.Base64;

public class URLSteganography {

	private static final String DEFAULT_CHARSET = "ISO-8859-1";

	public static final String TWITPIC_URL = "http://twitpic.com/";
	public static final String BITLY_URL = "http://bit.ly/";

	private URLSteganography() {}

	public static String makePictureLink(String text) {
		return TWITPIC_URL + encode(text);
	}

	public static String makeUrlLink(String text) {
		return BITLY_URL + encode(text);
	}

	public static String encode(String text) {
		try {
			return new String(Base64.encodeBase64(text.getBytes(DEFAULT_CHARSET), false, true, 10));
		}
		catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		return null;
	}

	public static String decode(String text) {
		try {
			return new String(Base64.decodeBase64(text), DEFAULT_CHARSET);
		}
		catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		return null;
	}
}
