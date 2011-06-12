/*******************************************************************************
 * Copyright (c) 2011 Željko Zirikoviæ.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package com.revolucion.secretwit.ui.components;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

import com.revolucion.secretwit.utils.ColorUtils;
import com.revolucion.secretwit.utils.ImageUtils;

public class SimpleArrowButton extends JButton implements SwingConstants {

	private static final long serialVersionUID = -5442401466266961051L;
	
	protected int direction;
	private Color color;

	public enum Size {
		DEFAULT, SMALL, BIG
	}

	public SimpleArrowButton(int direction, Color color) {
		this(direction, color, Size.DEFAULT);
	}

	public SimpleArrowButton(int direction, Color color, Size size) {
		this.color = color;
		setRequestFocusEnabled(false);
		setMargin(new Insets(0, 0, 0, 0));
		setFocusable(false);
		// setFocusPainted(false);
		setContentAreaFilled(false);
		setDirection(direction);
		setBorder(new EmptyBorder(0, 0, 0, 0));

		int fontSize = getFont().getSize();
		if (size.equals(Size.SMALL))
			fontSize -= 2;
		else if (size.equals(Size.BIG))
			fontSize = 18;

		setIcon(new ImageIcon(getArrow(fontSize, direction, this.color)));
		setRolloverIcon(new ImageIcon(getArrow(fontSize, direction, ColorUtils.isDark(this.color) ? this.color.brighter() : this.color.darker())));
	}

	@Override
	public boolean isFocusable() {
		return false;
	}

	public int getDirection() {
		return direction;
	}

	public void setDirection(int dir) {
		direction = dir;
	}

	@Override
	public Dimension getPreferredSize() {
		int dim = (int) getArrowIconWidth(getFont().getSize()) + 1;
		return new Dimension(dim, dim);
	}

	@Override
	public Dimension getMinimumSize() {
		return getPreferredSize();
	}

	@Override
	public Dimension getMaximumSize() {
		return new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE);
	}

	@Override
	public boolean isFocusTraversable() {
		return false;
	}

	private BufferedImage getArrow(int fontSize, int direction, Color c) {
		float width = getArrowIconWidth(fontSize); // -1
		float height = getArrowIconHeight(fontSize); // -1
		if (direction == SwingConstants.CENTER)
			height *= 2;
		float strokeWidth = fontSize / 6.0f;
		return ImageUtils.createArrow(width, height, strokeWidth, direction, c);
	}

	private float getArrowIconHeight(int fontSize) {
		if (fontSize < 12)
			return 2.5f + fontSize * 0.5f;
		return 3.0f + fontSize * 0.6f;
	}

	private float getArrowIconWidth(int fontSize) {
		int result = 2 * fontSize / 3;
		if (result % 2 == 0)
			result++;
		return result + 4;
	}
}
