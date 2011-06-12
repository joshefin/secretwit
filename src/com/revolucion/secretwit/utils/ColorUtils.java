/*******************************************************************************
 * Copyright (c) 2011 Željko Zirikoviæ.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package com.revolucion.secretwit.utils;

import java.awt.Color;
import java.awt.Component;
import java.awt.GradientPaint;
import java.awt.Graphics2D;

public class ColorUtils {

	private static Color LIST_TOP_LINE_COLOR = new Color(255, 235, 180); // 145, 160, 192
	private static Color LIST_TOP_GRADIENT_COLOR = new Color(255, 243, 186); // 161, 176, 207
	private static Color LIST_BOTTOM_GRADIENT_COLOR = new Color(255, 223, 166); // 113, 133, 171

	public static Color TRANSPARENT_COLOR = new Color(0, 0, 0, 0);

	private static void paintGradientSelection(Color topLineColor, Color bottomLineColor, Color topGradientColor, Color bottomGradientColor, Graphics2D graphics2D, int width, int height) {
		GradientPaint paint = new GradientPaint(0, 1, topGradientColor, 0, height - 2, bottomGradientColor);

		graphics2D.setPaint(paint);
		graphics2D.fillRect(0, 0, width, height);

		graphics2D.setColor(topLineColor);
		graphics2D.drawLine(0, 0, width, 0);

		graphics2D.setColor(bottomLineColor);
		graphics2D.drawLine(0, height - 1, width, height - 1);
	}

	public static void paintListGradientSelection(Graphics2D graphics2D, Component component, int width, int height) {
		paintGradientSelection(LIST_TOP_LINE_COLOR, LIST_BOTTOM_GRADIENT_COLOR, LIST_TOP_GRADIENT_COLOR, LIST_BOTTOM_GRADIENT_COLOR, graphics2D, width, height);
	}

	/**
	 * Return the "distance" between two colors. The rgb entries are taken to be coordinates in a 3D space [0.0-1.0],
	 * and this method returnes the distance between the coordinates for the first and second color.
	 * 
	 * @param r1
	 *            , g1, b1 First color.
	 * @param r2
	 *            , g2, b2 Second color.
	 * @return Distance bwetween colors.
	 */
	public static double colorDistance(double r1, double g1, double b1, double r2, double g2, double b2) {
		double a = r2 - r1;
		double b = g2 - g1;
		double c = b2 - b1;

		return Math.sqrt(a * a + b * b + c * c);
	}

	/**
	 * Return the "distance" between two colors.
	 * 
	 * @param color1
	 *            First color [r,g,b].
	 * @param color2
	 *            Second color [r,g,b].
	 * @return Distance bwetween colors.
	 */
	public static double colorDistance(double[] color1, double[] color2) {
		return ColorUtils.colorDistance(color1[0], color1[1], color1[2], color2[0], color2[1], color2[2]);
	}

	/**
	 * Return the "distance" between two colors.
	 * 
	 * @param color1
	 *            First color.
	 * @param color2
	 *            Second color.
	 * @return Distance between colors.
	 */
	public static double colorDistance(Color color1, Color color2) {
		float rgb1[] = new float[3];
		float rgb2[] = new float[3];

		color1.getColorComponents(rgb1);
		color2.getColorComponents(rgb2);

		return ColorUtils.colorDistance(rgb1[0], rgb1[1], rgb1[2], rgb2[0], rgb2[1], rgb2[2]);
	}

	/**
	 * Check if a color is more dark than light. Useful if an entity of this color is to be labeled: Use white label on
	 * a "dark" color and black label on a "light" color.
	 * 
	 * @param r
	 *            ,g,b Color to check.
	 * @return True if this is a "dark" color, false otherwise.
	 */
	public static boolean isDark(double r, double g, double b) {
		// Measure distance to white and black respectively
		double dWhite = ColorUtils.colorDistance(r, g, b, 1.0, 1.0, 1.0);
		double dBlack = ColorUtils.colorDistance(r, g, b, 0.0, 0.0, 0.0);

		return dBlack < dWhite;
	}

	/**
	 * Check if a color is more dark than light. Useful if an entity of this color is to be labeled: Use white label on
	 * a "dark" color and black label on a "light" color.
	 * 
	 * @param color
	 *            Color to check.
	 * @return True if this is a "dark" color, false otherwise.
	 */
	public static boolean isDark(Color color) {
		float r = color.getRed() / 255.0f;
		float g = color.getGreen() / 255.0f;
		float b = color.getBlue() / 255.0f;

		return isDark(r, g, b);
	}
}
