/*******************************************************************************
 * Copyright (c) 2011 Željko Zirikoviæ.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package com.revolucion.secretwit.utils;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.Transparency;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.SwingConstants;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.revolucion.secretwit.SecretwitUI;

public class ImageUtils {

	private static final Logger logger = LoggerFactory.getLogger(ImageUtils.class);

	private ImageUtils() {}

	public static ImageIcon getLocalIcon(String name) {
		URL url = SecretwitUI.class.getResource("images/" + name);
		if (url == null) {
			logger.warn("Icon '{}' not found.", name);
			return null;
		}

		return new ImageIcon(url);
	}
	
	public static BufferedImage getLocalImage(String name) {
		URL url = SecretwitUI.class.getResource("images/" + name);
		if (url == null) {
			logger.warn("Image '{}' not found.", name);
			return null;
		}

		return getImage(url);
	}

	public static BufferedImage getImage(URL url) {
		try {
			return ImageIO.read(url);
		}
		catch (IOException e) {
			logger.warn("Can't read image {}. {}", url, e.getMessage());
		}
		catch (Exception e) {}

		return null;
	}

	public static BufferedImage getBlankImage(int width, int height) {
		GraphicsEnvironment e = GraphicsEnvironment.getLocalGraphicsEnvironment();
		GraphicsDevice d = e.getDefaultScreenDevice();
		GraphicsConfiguration c = d.getDefaultConfiguration();
		BufferedImage compatibleImage = c.createCompatibleImage(width, height, Transparency.TRANSLUCENT);

		return compatibleImage;
	}

	public static BufferedImage getRotatedImage(BufferedImage image, int quadrantClockwise) {
		quadrantClockwise = quadrantClockwise % 4;
		int width = image.getWidth();
		int height = image.getHeight();
		if ((quadrantClockwise == 1) || (quadrantClockwise == 3)) {
			width = image.getHeight();
			height = image.getWidth();
		}
		BufferedImage imageRotated = getBlankImage(width, height);
		AffineTransform at = null;
		switch (quadrantClockwise) {
		case 1:
			at = AffineTransform.getTranslateInstance(width, 0);
			at.rotate(Math.PI / 2);
			break;
		case 2:
			at = AffineTransform.getTranslateInstance(width, height);
			at.rotate(Math.PI);
			break;
		case 3:
			at = AffineTransform.getTranslateInstance(0, height);
			at.rotate(-Math.PI / 2);
		}
		Graphics2D rotatedGraphics = imageRotated.createGraphics();
		if (at != null)
			rotatedGraphics.setTransform(at);
		rotatedGraphics.drawImage(image, 0, 0, null);
		rotatedGraphics.dispose();

		return imageRotated;
	}

	public static BufferedImage createArrow(float width, float height, float strokeWidth, int direction, Color color) {
		BufferedImage arrowImage = ImageUtils.getBlankImage((int) width, (int) height);

		Graphics2D graphics = (Graphics2D) arrowImage.getGraphics();

		graphics.translate(1, 1);
		width -= 2;
		height -= 2;
		graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		graphics.setColor(color);
		int cap = (width < 15) ? BasicStroke.CAP_BUTT : BasicStroke.CAP_ROUND;
		Stroke stroke = new BasicStroke(strokeWidth, cap, BasicStroke.JOIN_MITER);

		graphics.setStroke(stroke);

		int cushion = (int) strokeWidth / 2;
		if (direction == SwingConstants.CENTER) {
			BufferedImage top = createArrow(width, height / 2, strokeWidth, SwingConstants.NORTH, color);
			BufferedImage bottom = createArrow(width, height / 2, strokeWidth, SwingConstants.SOUTH, color);
			graphics.drawImage(top, 0, 1, null);
			graphics.drawImage(bottom, 0, (int) height / 2 - 1, null);
			return arrowImage;
		}
		else {
			GeneralPath gp = new GeneralPath();
			gp.moveTo(cushion, strokeWidth);
			gp.lineTo((float) 0.5 * (width - 1), height - 1 - cushion);
			gp.lineTo(width - 1 - cushion, strokeWidth);
			graphics.draw(gp);

			int quadrantCounterClockwise = 0;
			switch (direction) {
			case SwingConstants.NORTH:
				quadrantCounterClockwise = 2;
				break;
			case SwingConstants.WEST:
				quadrantCounterClockwise = 1;
				break;
			case SwingConstants.SOUTH:
				quadrantCounterClockwise = 0;
				break;
			case SwingConstants.EAST:
				quadrantCounterClockwise = 3;
				break;
			}
			BufferedImage rotatedImage = ImageUtils.getRotatedImage(arrowImage, quadrantCounterClockwise);

			return rotatedImage;
		}
	}
}
