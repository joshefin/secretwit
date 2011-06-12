/*******************************************************************************
 * Copyright (c) 2011 Željko Zirikoviæ.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package com.revolucion.secretwit.ui.components;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.plaf.basic.BasicScrollBarUI;

import com.revolucion.secretwit.ui.components.SimpleArrowButton.Size;

/**
 * Minimalistic scroll bar.
 * 
 * @author codeR
 * 
 */
public class MinimalisticScrollBarUI extends BasicScrollBarUI {

	private final int THUMB_WIDTH = 5;

	private Color trackColor;
	private Color thumbColor;
	private Color arrowColor;

	public MinimalisticScrollBarUI(Color trackColor, Color thumbColor, Color arrowColor) {
		this.trackColor = trackColor;
		this.thumbColor = thumbColor;
		this.arrowColor = arrowColor;
	}

	@Override
	protected void installDefaults() {
		super.installDefaults();

		scrollbar.setOpaque(false);
	}

	@Override
	protected void paintThumb(Graphics g, JComponent c, Rectangle thumbBounds) {
		if (thumbBounds.isEmpty())
			return;

		Graphics2D g2 = (Graphics2D) g.create();

		g2.setColor(isThumbRollover() ? thumbColor.darker() : thumbColor);
		g2.fillRect(thumbBounds.x + (thumbBounds.width - THUMB_WIDTH) / 2, thumbBounds.y, THUMB_WIDTH, thumbBounds.height);
		g2.dispose();
	}

	@Override
	protected void paintTrack(Graphics g, JComponent c, Rectangle trackBounds) {
		Graphics2D g2 = (Graphics2D) g.create();

		g2.setColor(trackColor);
		g2.drawLine(trackBounds.x + trackBounds.width / 2, trackBounds.y, trackBounds.x + trackBounds.width / 2, trackBounds.y + trackBounds.height);

		g2.dispose();
	}

	@Override
	protected JButton createDecreaseButton(int orientation) {
		return new SimpleArrowButton(orientation, arrowColor, Size.SMALL);
	}

	@Override
	protected JButton createIncreaseButton(int orientation) {
		return new SimpleArrowButton(orientation, arrowColor, Size.SMALL);
	}

}
