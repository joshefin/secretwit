/*******************************************************************************
 * Copyright (c) 2011 Željko Zirikoviæ.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package com.revolucion.secretwit.stego;

import java.io.UnsupportedEncodingException;
import java.util.BitSet;

public class WhitespaceSteganography {

	private static final String DEFAULT_CHARSET = "ISO-8859-1";
	private static final char ENCODING_ONE = ' ';
	private static final char ENCODING_ZERO = '\t';
	
	public static final String MESSAGE_ENDING = "&nbsp;";

	private WhitespaceSteganography() {}

	public static String encode(String text) {
		StringBuilder encodedText = new StringBuilder();

		try {
			byte[] textAsBytes = text.getBytes(DEFAULT_CHARSET);
			
			// Primer: 
			// b = 00110000
			// mask je 00000001, 00000010, ..., 10000000
			// primenjuje se logicko and kako bi se izvukao bit

			for (byte b : textAsBytes) {
				// System.out.println("byte: " + Integer.toString(b & 0xFF, 2));
				for (int mask = 0x01; mask != 0x100; mask <<= 1) {
					// System.out.println("mask: " + Integer.toBinaryString(mask));
					boolean value = (b & mask) != 0;
					encodedText.append(value ? ENCODING_ONE : ENCODING_ZERO);
				}
			}
		}
		catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		return encodedText.toString();
	}

	public static String decode(String text) {
		byte[] textAsBytes = new byte[text.length() / 8];

		BitSet bitSet = new BitSet();
		for (int i = text.length() - 1; i >= 0; i--) {
			char c = text.charAt(i);

			int bitPos = i % 8;
			bitSet.set(bitPos, c == ENCODING_ONE);

			if (!bitSet.isEmpty() && bitPos == 0) {
				textAsBytes[i / 8] = toByte(bitSet);
				bitSet.clear();
			}
		}

		try {
			return new String(textAsBytes, DEFAULT_CHARSET);
		}
		catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		return null;
	}

	private static byte toByte(BitSet bits) {
		byte[] bytes = new byte[1];
		for (int i = 0; i < bits.length(); i++) {
			if (bits.get(i))
				bytes[0] |= 1 << (i % 8);
		}
		return bytes[0];
	}
}
