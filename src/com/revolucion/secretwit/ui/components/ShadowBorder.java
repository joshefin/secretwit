/*******************************************************************************
 * Copyright (c) 2011 Zeljko Zirikovic.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GPL which 
 * accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package com.revolucion.secretwit.ui.components;

import java.awt.Color;
import java.awt.Component;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Paint;
import java.awt.RenderingHints;

import javax.swing.border.AbstractBorder;

/**
 * @author Christophe Le Besnerais
 */
public class ShadowBorder extends AbstractBorder {

	private static final long serialVersionUID = -817334487094892912L;

	public static Color TRANSPARENT_COLOR = new Color(0, 0, 0, 0);

	private Insets insets = new Insets(0, 0, 0, 0);
	private int shadowsMargin = 0;
	private Paint colorRight, colorLeft, colorTop, colorBottom;
	private Color shadowColor = new Color(0.0f, 0.0f, 0.0f, 0.5f);
	private boolean linesVisible = true;

	public ShadowBorder() {
		this(new Insets(0, 0, 0, 0));
	}

	public ShadowBorder(Insets insets) {
		this(insets, TRANSPARENT_COLOR, TRANSPARENT_COLOR, TRANSPARENT_COLOR, TRANSPARENT_COLOR);
	}

	public ShadowBorder(Insets insets, Paint color) {
		this(insets, color, color, color, color);
	}

	public ShadowBorder(Insets insets, Paint colorRight, Paint colorLeft, Paint colorTop, Paint colorBottom) {
		this(insets, colorRight, colorLeft, colorTop, colorBottom, null);
	}

	public ShadowBorder(Insets insets, Paint colorRight, Paint colorLeft, Paint colorTop, Paint colorBottom, Paint insideColor) {
		this.insets = insets;
		this.colorRight = colorRight;
		this.colorLeft = colorLeft;
		this.colorTop = colorTop;
		this.colorBottom = colorBottom;
	}

	public boolean isLinesVisible() {
		return linesVisible;
	}

	public void setLinesVisible(boolean linesVisible) {
		this.linesVisible = linesVisible;
	}

	public int getShadowsMargin() {
		return shadowsMargin;
	}

	public void setShadowsMargin(int shadowsMargin) {
		this.shadowsMargin = shadowsMargin;
	}

	public Paint getColorBottom() {
		return colorBottom;
	}

	public void setColorBottom(Paint colorBottom) {
		this.colorBottom = colorBottom;
	}

	public Paint getColorLeft() {
		return colorLeft;
	}

	public void setColorLeft(Paint colorLeft) {
		this.colorLeft = colorLeft;
	}

	public Paint getColorRight() {
		return colorRight;
	}

	public void setColorRight(Paint colorRight) {
		this.colorRight = colorRight;
	}

	public Paint getColorTop() {
		return colorTop;
	}

	public void setColorTop(Paint colorTop) {
		this.colorTop = colorTop;
	}

	public Color getShadowColor() {
		return shadowColor;
	}

	public void setShadowColor(Color shadowColor) {
		this.shadowColor = shadowColor;
	}

	@Override
	public Insets getBorderInsets(Component c) {
		return insets;
	}

	@Override
	public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
		Graphics2D g2 = (Graphics2D) g;
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		// paint background :
		if (insets.right > 0) {
			g2.setPaint(colorRight);
			g2.fillRect(width - insets.right, 0, insets.right, height);
		}
		if (insets.left > 0) {
			g2.setPaint(colorLeft);
			g2.fillRect(0, 0, insets.left, height);
		}
		if (insets.top > 0) {
			g2.setPaint(colorTop);
			g2.fillRect(0, -1, width, insets.top + 2);
		}
		if (insets.bottom > 0) {
			g2.setPaint(colorBottom);
			g2.fillRect(0, height - insets.bottom, width, insets.bottom);
		}

		// paint shadows :
		if (insets.right > 0) {
			g2.setPaint(new GradientPaint(width - insets.right, 0, shadowColor, width, 0, TRANSPARENT_COLOR));
			g2.fillArc(width - 2 * insets.right, insets.top + shadowsMargin, insets.right * 2, height - insets.bottom - insets.top - 2 * shadowsMargin, -90, 180);
			if (isLinesVisible()) {
				g2.setColor(shadowColor);
				g2.drawLine(width - insets.right, insets.top, width - insets.right, height - insets.bottom);
			}
		}
		if (insets.left > 0) {
			g2.setPaint(new GradientPaint(insets.left, 0, shadowColor, 0, 0, TRANSPARENT_COLOR));
			g2.fillArc(0, insets.top + shadowsMargin, insets.left * 2, height - insets.bottom - insets.top - 2 * shadowsMargin, 90, 180);
			if (isLinesVisible()) {
				g2.setColor(shadowColor);
				g2.drawLine(insets.left - 1, insets.top, insets.left - 1, height - insets.bottom);
			}
		}
		if (insets.top > 0) {
			g2.setPaint(new GradientPaint(0, insets.top, shadowColor, 0, 0, TRANSPARENT_COLOR));
			g2.fillArc(insets.left + shadowsMargin, 1, width - insets.left - insets.right - 2 * shadowsMargin, insets.top * 2, 0, 180);
			if (isLinesVisible()) {
				g2.setColor(shadowColor);
				g2.drawLine(insets.left, insets.top, width - insets.right, insets.top);
			}
		}
		if (insets.bottom > 0) {
			g2.setPaint(new GradientPaint(0, height - insets.bottom, shadowColor, 0, height, TRANSPARENT_COLOR));
			g2.fillArc(insets.left + shadowsMargin, height - 2 * insets.bottom, width - insets.left - insets.right - 2 * shadowsMargin, insets.bottom * 2, 0, -180);
			if (isLinesVisible()) {
				g2.setColor(shadowColor);
				g2.drawLine(insets.left, height - insets.bottom, width - insets.right, height - insets.bottom);
			}
		}
	}

}
