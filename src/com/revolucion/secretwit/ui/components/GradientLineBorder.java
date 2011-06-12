/*******************************************************************************
 * Copyright (c) 2011 Željko Zirikoviæ.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package com.revolucion.secretwit.ui.components;

import java.awt.Color;
import java.awt.Component;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.RenderingHints;

import javax.swing.border.AbstractBorder;

public class GradientLineBorder extends AbstractBorder {

	private static final long serialVersionUID = 4073436609231134123L;

	protected int thickness;
	protected Color lineColorTop;
	protected Color lineColorBottom;

	public GradientLineBorder() {
		this(new Color(255, 255, 255, 59), new Color(255, 255, 255, 25), 2);
	}

	public GradientLineBorder(Color colorTop, Color colorBottom) {
		this(colorTop, colorBottom, 1);
	}

	public GradientLineBorder(Color colorTop, Color colorBottom, int thickness) {
		this.lineColorTop = colorTop;
		this.lineColorBottom = colorBottom;
		this.thickness = thickness;
	}

	@Override
	public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
		Graphics2D g2d = (Graphics2D) g.create();
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		GradientPaint paint = new GradientPaint(0, 0, lineColorTop, 0, height, lineColorBottom);
		g2d.setPaint(paint);

		for (int i = 0; i < thickness; i++)
			g2d.drawRect(x + i, y + i, width - i - i - 1, height - i - i - 1);

		g2d.dispose();
	}

	@Override
	public Insets getBorderInsets(Component c) {
		// return new Insets(thickness, thickness, thickness, thickness);
		return new Insets(0, 0, 0, 0);
	}

	@Override
	public Insets getBorderInsets(Component c, Insets insets) {
		// insets.left = insets.top = insets.right = insets.bottom = thickness;
		insets.left = insets.top = insets.right = insets.bottom = 0;
		return insets;
	}

	@Override
	public boolean isBorderOpaque() {
		return true;
	}

	public Color getLineColorTop() {
		return lineColorTop;
	}

	public Color getLineColorBottom() {
		return lineColorBottom;
	}

	public int getThickness() {
		return thickness;
	}
}
