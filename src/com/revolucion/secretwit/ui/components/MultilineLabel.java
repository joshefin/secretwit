/*******************************************************************************
 * Copyright (c) 2011 Zeljko Zirikovic.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GPL which 
 * accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package com.revolucion.secretwit.ui.components;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Rectangle;

import javax.swing.JTextArea;
import javax.swing.LookAndFeel;
import javax.swing.UIManager;
import javax.swing.plaf.UIResource;
import javax.swing.text.DefaultCaret;

public class MultilineLabel extends JTextArea {

	private static final long serialVersionUID = 6281366200729922649L;

	public MultilineLabel() {
		initComponents();
	}

	public MultilineLabel(String s) {
		super(s);
		initComponents();
	}

	private void initComponents() {
		adjustUI();
	}

	/**
	 * Reloads the pluggable UI. The key used to fetch the new interface is
	 * <code>getUIClassID()</code>. The type of the UI is <code>TextUI</code>.
	 * <code>invalidate</code> is called after setting the UI.
	 */
	@Override
	public void updateUI() {
		super.updateUI();
		adjustUI();
	}

	/**
	 * Adjusts UI to make sure it looks like a label instead of a text area.
	 */
	protected void adjustUI() {
		setLineWrap(true);
		setWrapStyleWord(true);
		setEditable(false);
		setRequestFocusEnabled(false);
		setFocusable(false);
		setOpaque(false);

		setCaret(new DefaultCaret() {
			private static final long serialVersionUID = 1242467463492127346L;

			@Override
			protected void adjustVisibility(Rectangle nloc) {}
		});

		LookAndFeel.installBorder(this, "Label.border");
		Color fg = getForeground();
		if (fg == null || fg instanceof UIResource) {
			setForeground(UIManager.getColor("Label.foreground"));
		}
		Font f = getFont();
		if (f == null || f instanceof UIResource) {
			setFont(UIManager.getFont("Label.font"));
		}
		setBackground(null);
	}

	/**
	 * Overrides <code>getMinimumSize</code> to return
	 * <code>getPreferredSize()</code> instead. We did this because of a bug at
	 * http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=4824261.
	 * 
	 * @return new Dimension(1, 1).
	 */
	@Override
	public Dimension getMinimumSize() {
		return getPreferredSize();
	}
}
