/*******************************************************************************
 * Copyright (c) 2011 Željko Zirikoviæ.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package com.revolucion.secretwit.stego;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.WritableRaster;

public class ImageSteganography {

	private static final int MAX_INT_LEN = 4;

	// number of image bytes required to store one stego byte
	private static final int DATA_SIZE = 8;

	/**
	 * Hide text inside image.
	 * 
	 * @param text
	 * @param image
	 * @return true or false
	 */
	public static boolean hide(String text, BufferedImage image) {
		if (text == null || text.isEmpty())
			return false;

		byte[] stego = buildStego(text);

		// access the image's data as a byte array
		if (image == null)
			return false;

		byte imBytes[] = accessBytes(image);

		// im is modified with the stego
		if (!singleHide(imBytes, stego))
			return false;

		return true;
	}

	/**
	 * Build a stego (a byte array), made up of 2 fields: <size of binary
	 * message> <binary message>
	 * 
	 * @param inputText
	 * @return Stego as byte array
	 */
	private static byte[] buildStego(String inputText) {
		// convert data to byte arrays
		byte[] msgBytes = inputText.getBytes();
		byte[] lenBs = intToBytes(msgBytes.length);

		int totalLen = lenBs.length + msgBytes.length;
		byte[] stego = new byte[totalLen]; // for holding the resulting stego

		// combine the 2 fields into one byte array

		// length of binary message
		System.arraycopy(lenBs, 0, stego, 0, lenBs.length);
		// binary message
		System.arraycopy(msgBytes, 0, stego, lenBs.length, msgBytes.length);

		// System.out.println("Num. pixels to store fragment " + i + ": " +
		// totalLen*DATA_SIZE);
		return stego;
	}

	/**
	 * Split integer i into a MAX_INT_LEN-element byte array.
	 * 
	 * @param i
	 * @return
	 */
	private static byte[] intToBytes(int i) {
		// map the parts of the integer to a byte array
		byte[] integerBs = new byte[MAX_INT_LEN];
		integerBs[0] = (byte) ((i >>> 24) & 0xFF);
		integerBs[1] = (byte) ((i >>> 16) & 0xFF);
		integerBs[2] = (byte) ((i >>> 8) & 0xFF);
		integerBs[3] = (byte) (i & 0xFF);

		// for (int j=0; j < integerBs.length; j++)
		// System.out.println(" integerBs[ " + j + "]: " + integerBs[j]);

		return integerBs;
	}

	/**
	 * Access the data bytes in the image.
	 * 
	 * @param image
	 * @return Image data as byte array
	 */
	private static byte[] accessBytes(BufferedImage image) {
		WritableRaster raster = image.getRaster();
		DataBufferByte buffer = (DataBufferByte) raster.getDataBuffer();

		return buffer.getData();
	}

	/**
	 * Store stego in image bytes.
	 * 
	 * @param imBytes
	 * @param stego
	 * @return true or false
	 */
	private static boolean singleHide(byte[] imBytes, byte[] stego) {
		int imLen = imBytes.length;
		// System.out.println("Byte length of image: " + imLen);

		int totalLen = stego.length;
		// System.out.println("Total byte length of message: " + totalLen);

		// check that the stego will fit into the image
		// multiply stego length by number of image bytes required to store one
		// stego byte
		if ((totalLen * DATA_SIZE) > imLen) {
			// System.out.println("Image not big enough for message");
			return false;
		}

		hideStego(imBytes, stego, 0); // hide at start of image

		return true;
	}

	/**
	 * Store stego in image starting at byte position offset.
	 * 
	 * @param imBytes
	 * @param stego
	 * @param offset
	 */
	private static void hideStego(byte[] imBytes, byte[] stego, int offset) {
		// loop through stego
		for (int i = 0; i < stego.length; i++) {
			int byteVal = stego[i];
			// loop through the 8 bits of each stego byte
			for (int j = 7; j >= 0; j--) {
				int bitVal = (byteVal >>> j) & 1;

				// change last bit of image byte to be the stego bit
				imBytes[offset] = (byte) ((imBytes[offset] & 0xFE) | bitVal);
				offset++;
			}
		}
	}

	/**
	 * Retrieve the hidden message from the beginning of the image after first
	 * extractibg its length information.
	 * 
	 * @param image
	 * @return true or false
	 */
	public static String reveal(BufferedImage im) {
		if (im == null)
			return null;

		// get the image's data as a byte array
		byte[] imBytes = accessBytes(im);
		// System.out.println("Byte length of image: " + imBytes.length);

		// get msg length at the start of the image
		int msgLen = getMsgLength(imBytes, 0);
		if (msgLen == -1)
			return null;

		// System.out.println("Byte length of message: " + msgLen);

		// get message located after the length info in the image
		String msg = getMessage(imBytes, msgLen, MAX_INT_LEN * DATA_SIZE);

		//if (msg == null)
			//System.out.println("No message found");

		return msg;
	}

	/**
	 * Retrieve binary message length from the image.
	 * 
	 * @param imBytes
	 * @param offset
	 * @return Message length
	 */
	private static int getMsgLength(byte[] imBytes, int offset) {
		byte[] lenBytes = extractHiddenBytes(imBytes, MAX_INT_LEN, offset);
		// get the binary message length as a byte array
		if (lenBytes == null)
			return -1;

		// for (int j=0; j < lenBytes.length; j++)
		// System.out.println(" lenBytes[ " + j + "]: " + lenBytes[j]);

		// convert the byte array into an integer
		int msgLen = ((lenBytes[0] & 0xff) << 24) | ((lenBytes[1] & 0xff) << 16) | ((lenBytes[2] & 0xff) << 8) | (lenBytes[3] & 0xff);
		// System.out.println("Message length: " + msgLen);

		if ((msgLen <= 0) || (msgLen > imBytes.length)) {
			// System.out.println("Incorrect message length");
			return -1;
		}
		// else
		// System.out.println("Revealed message length: " + msgLen);

		return msgLen;
	}

	/**
	 * Extract a binary message of size msgLen from the image, and convert it to
	 * a string.
	 * 
	 * @param imBytes
	 * @param msgLen
	 * @param offset
	 * @return Hidden message
	 */
	private static String getMessage(byte[] imBytes, int msgLen, int offset) {
		byte[] msgBytes = extractHiddenBytes(imBytes, msgLen, offset);
		// the message is msgLen bytes long
		if (msgBytes == null)
			return null;

		String msg = new String(msgBytes);

		// check the message is all characters
		return (isPrintable(msg) ? msg : null);
	}

	/**
	 * Extract 'size' hidden data bytes, starting from 'offset' in the image
	 * bytes.
	 * 
	 * @param imBytes
	 * @param size
	 * @param offset
	 * @return
	 */
	private static byte[] extractHiddenBytes(byte[] imBytes, int size, int offset) {
		int finalPosn = offset + (size * DATA_SIZE);
		if (finalPosn > imBytes.length) {
			// System.out.println("End of image reached");
			return null;
		}

		byte[] hiddenBytes = new byte[size];

		// loop through each hidden byte
		for (int j = 0; j < size; j++) {
			// make one hidden byte from DATA_SIZE image bytes
			for (int i = 0; i < DATA_SIZE; i++) {
				hiddenBytes[j] = (byte) ((hiddenBytes[j] << 1) | (imBytes[offset] & 1));
				// shift existing 1 left; store right most bit of image byte
				offset++;
			}
		}

		return hiddenBytes;
	}

	private static boolean isPrintable(String str) {
		for (int i = 0; i < str.length(); i++)
			if (!isPrintable(str.charAt(i))) {
				// System.out.println("Unprintable character found");
				return false;
			}
		return true;
	}

	private static boolean isPrintable(int ch) {
		if (Character.isWhitespace(ch) && (ch < 127))
			return true;
		else if ((ch > 32) && (ch < 127))
			return true;

		return false;
	}

}
