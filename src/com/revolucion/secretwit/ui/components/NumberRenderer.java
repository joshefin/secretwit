/*******************************************************************************
 * Copyright (c) 2011 Željko Zirikoviæ.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package com.revolucion.secretwit.ui.components;

import java.awt.AlphaComposite;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import java.awt.image.BufferedImage;

import javax.swing.BorderFactory;
import javax.swing.JLabel;

import com.revolucion.secretwit.utils.Colors;
import com.revolucion.secretwit.utils.Fonts;

/**
 * Renders a rounded rectangle (i.e. a badge) with a given number in the center
 * of the rectangle.
 */
public class NumberRenderer extends JLabel {

	private static final long serialVersionUID = -4981742518906121388L;

	public NumberRenderer() {
		this(0);
	}

	public NumberRenderer(int count) {
		setFont(Fonts.SIGNUP_NUMBER);
		setBackground(Colors.SIGNUP_NUMBER_BG);
		setForeground(Colors.SIGNUP_NUMBER_FB);
		setBorder(BorderFactory.createEmptyBorder(0, 6, 0, 6));
		setState(count);
	}

	public void setState(int count) {
		setText(String.valueOf(count));
	}

	@Override
	protected void paintComponent(Graphics g) {
		// create a buffered image to draw the component into. this lets us
		// draw "out" an area, making it transparent
		BufferedImage image = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_ARGB);

		// create the graphics and set its initial state.
		Graphics2D g2d = image.createGraphics();
		g2d.setFont(getFont());
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2d.setColor(getBackground());

		// draw the badge
		g2d.fillRoundRect(0, 0, getWidth(), getHeight(), getHeight(), getHeight());

		g2d.setColor(getForeground());
		// if the badge is selected, punch out the text so that the
		// underlying color shows through as the font color.
		// else use use a standard alpha composite to simply draw on top of
		// whatever is currently there
		g2d.setComposite(AlphaComposite.DstOut); // or AlphaComposite.SrcOver
		// calculate the bottom left point to draw the text at
		Font font = g2d.getFont();
		FontRenderContext renderContext = g2d.getFontRenderContext();
		GlyphVector glyphVector = font.createGlyphVector(renderContext, getText());
		Rectangle visualBounds = glyphVector.getVisualBounds().getBounds();
		int x = getWidth() / 2 - g2d.getFontMetrics().stringWidth(getText()) / 2;
		int y = getHeight() / 2 - visualBounds.height / 2 - visualBounds.y;

		// draw the badge text
		g2d.drawString(getText(), x, y);

		// draw the image into this component
		g.drawImage(image, 0, 0, null);

		// dispose of the buffered image graphics
		g2d.dispose();
	}

}
