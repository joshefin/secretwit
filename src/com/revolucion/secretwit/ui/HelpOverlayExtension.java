/*******************************************************************************
 * Copyright (c) 2011 Zeljko Zirikovic.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GPL which 
 * accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package com.revolucion.secretwit.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Point;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JWindow;

import com.revolucion.secretwit.ui.components.GradientLineBorder;
import com.revolucion.secretwit.utils.ImageUtils;
import com.sun.awt.AWTUtilities;

public class HelpOverlayExtension extends JWindow {

	private static final long serialVersionUID = -3875813473985109366L;

	private JFrame parentFrame;
	private WindowFocusListener parentFocusListener;
	private WindowAdapter parentWindowListener;

	public HelpOverlayExtension(final JFrame parentFrame) {
		this.parentFrame = parentFrame;

		setLayout(new BorderLayout());

		init();

		pack();

		Point appLocation = parentFrame.getLocationOnScreen();
		setLocation(appLocation.x - 270, appLocation.y - 70);

		parentFocusListener = new WindowFocusListener() {
			@Override
			public void windowLostFocus(WindowEvent e) {}

			@Override
			public void windowGainedFocus(WindowEvent e) {
				setVisible(true);
				toFront();
			}
		};
		parentFrame.addWindowFocusListener(parentFocusListener);

		parentWindowListener = new WindowAdapter() {
			@Override
			public void windowIconified(WindowEvent e) {
				setVisible(false);
			}
		};
		parentFrame.addWindowListener(parentWindowListener);

		if (AWTUtilities.isTranslucencyCapable(getGraphicsConfiguration())) {
			AWTUtilities.setWindowOpaque(this, false);
		}
		else {
			((JPanel) getContentPane()).setBackground(Color.white);
			((JPanel) getContentPane()).setBorder(new GradientLineBorder(Color.darkGray, Color.lightGray, 1));
		}
	}

	private void init() {
		JLabel overlayImage = new JLabel();
		overlayImage.setIcon(ImageUtils.getLocalIcon("help_overlay.png"));

		add(overlayImage, BorderLayout.CENTER);
	}

	@Override
	public void dispose() {
		parentFrame.removeWindowFocusListener(parentFocusListener);
		parentFrame.removeWindowListener(parentWindowListener);
		super.dispose();
	}

}
