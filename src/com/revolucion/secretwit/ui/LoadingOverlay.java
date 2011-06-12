/*******************************************************************************
 * Copyright (c) 2011 Željko Zirikoviæ.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package com.revolucion.secretwit.ui;

import java.awt.Color;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingConstants;

import net.miginfocom.swing.MigLayout;

import com.revolucion.secretwit.ui.components.MinimalisticProgressBarUI;
import com.revolucion.secretwit.utils.Colors;
import com.revolucion.secretwit.utils.Fonts;

public class LoadingOverlay extends JPanel {

	private static final long serialVersionUID = 3819223024551873984L;

	private JProgressBar progressBar;
	private JLabel labelProgress;

	public LoadingOverlay() {
		init();
	}

	private void init() {
		setOpaque(false);
		setLayout(new MigLayout("fillx, insets 300 40 0 40", "[c]", ""));

		progressBar = new JProgressBar(0, 100);
		progressBar.setIndeterminate(true);
		progressBar.setForeground(Colors.LOADING_PROGRESS_BAR);
		progressBar.setUI(new MinimalisticProgressBarUI());

		labelProgress = new JLabel("Signing in to Twitter ...");
		labelProgress.setFont(Fonts.LOADING_PROGRESS);
		labelProgress.setForeground(Color.black);
		labelProgress.setHorizontalTextPosition(SwingConstants.CENTER);
		labelProgress.setHorizontalAlignment(SwingConstants.CENTER);

		add(progressBar, "grow, wrap");
		add(labelProgress);
	}

	public void setText(String text) {
		labelProgress.setText(text);
	}
}
