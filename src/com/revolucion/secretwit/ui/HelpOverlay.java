/*******************************************************************************
 * Copyright (c) 2011 Željko Zirikoviæ.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package com.revolucion.secretwit.ui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JComponent;

public class HelpOverlay extends JComponent {

	private static final long serialVersionUID = -3875813473985109366L;

	private RenderingHints renderingHints;

	private MouseAdapter mouseListener;
	private KeyAdapter keyListener;

	public HelpOverlay() {
		// setBackground(new Color(128, 128, 128, 100));
		setBackground(new Color(255, 255, 255, 100));

		mouseListener = new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				removeMouseListener(mouseListener);
				removeKeyListener(keyListener);
				firePropertyChange("visibility", true, false);
				setVisible(false);
			}
		};

		keyListener = new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
					removeMouseListener(mouseListener);
					removeKeyListener(keyListener);
					firePropertyChange("visibility", true, false);
					setVisible(false);
				}
			}
		};

		addMouseListener(mouseListener);
		addKeyListener(keyListener);

		renderingHints = new RenderingHints(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
		renderingHints.put(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		renderingHints.put(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
	}

	@Override
	protected void paintComponent(Graphics g) {
		Graphics2D g2 = (Graphics2D) g.create();
		g2.setRenderingHints(renderingHints);
		g2.setColor(getBackground());
		g2.fillRect(0, 0, getWidth(), getHeight());
		g2.dispose();
	}

}
