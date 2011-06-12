/*******************************************************************************
 * Copyright (c) 2011 Željko Zirikoviæ.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package com.revolucion.secretwit.ui.components;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.RenderingHints;

import javax.swing.border.EmptyBorder;

public class RoundBorder extends EmptyBorder {

	private static final long serialVersionUID = 1L;

	private Color lineColor;
	private float borderWidth;
	private int corner = 10;
	private boolean borderGradient;

	private int dist = 1;

	public RoundBorder(Color lineColor, float borderSize) {
		this(0, 0, 0, 0, lineColor, borderSize);
	}

	public RoundBorder(int top, int left, int bottom, int right, Color lineColor, float borderWidth) {
		this(top, left, bottom, right, lineColor, borderWidth, false);
	}

	public RoundBorder(int top, int left, int bottom, int right, Color lineColor, float borderWidth, boolean borderGradient) {
		super(top, left, bottom, right);
		this.lineColor = lineColor;
		this.borderWidth = borderWidth;
		this.borderGradient = borderGradient;
	}

	@Override
	public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
		Graphics2D g2 = (Graphics2D) g.create();

		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2.setPaint(c.isEnabled() ? borderGradient ? createGradientPaint(c, height) : lineColor : Color.lightGray);
		g2.setStroke(new BasicStroke(borderWidth, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
		g2.drawRoundRect(x + dist, y + dist, width - dist - 1, height - dist - 1, corner, corner);

		g2.dispose();
	}
	
	private Paint createGradientPaint(Component comp, int height) {
		return new GradientPaint(0, 0, lineColor, 0, height, lineColor.brighter());
	}

	public Color getLineColor() {
		return lineColor;
	}

	public void setLineColor(Color lineColor) {
		this.lineColor = lineColor;
	}

	public float getBorderWidth() {
		return borderWidth;
	}

	public void setBorderWidth(float borderWidth) {
		this.borderWidth = borderWidth;
	}

	public int getCorner() {
		return corner;
	}

	public void setCorner(int corner) {
		this.corner = corner;
	}
}
