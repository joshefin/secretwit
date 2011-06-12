/*******************************************************************************
 * Copyright (c) 2011 Željko Zirikoviæ.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package com.revolucion.secretwit.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowEvent;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingConstants;
import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;

import net.miginfocom.swing.MigLayout;

import com.revolucion.secretwit.SecretwitUI;
import com.revolucion.secretwit.twitter.TwitterClient;
import com.revolucion.secretwit.ui.components.ComponentUtils;
import com.revolucion.secretwit.ui.components.MinimalisticProgressBarUI;
import com.revolucion.secretwit.ui.components.WindowDragger;
import com.revolucion.secretwit.ui.timeline.TimelinePane;
import com.revolucion.secretwit.ui.views.ViewManager;
import com.revolucion.secretwit.utils.Colors;
import com.revolucion.secretwit.utils.Fonts;
import com.revolucion.secretwit.utils.ImageUtils;
import com.revolucion.secretwit.utils.WindowUtils;

public class HeaderPane extends JPanel {

	private static final long serialVersionUID = 4379979826237164946L;

	private boolean draggingEnabled = false;
	
	private JProgressBar progressBar;

	private JPanel panelTitlebar;
	private JLabel labelTitle;
	private JButton buttonSettings;
	private JButton buttonMinimize;
	private JButton buttonMaximize;
	private JButton buttonClose;

	private JPanel panelNavigation;
	private JLabel labelPlace;
	private JLabel labelUser;

	private HeaderPane() {
		setLayout(new BorderLayout(0, 0));
		setBackground(Colors.HEADER_BG);

		init();

		addAncestorListener(new AncestorListener() {
			@Override
			public void ancestorAdded(AncestorEvent event) {
				if (!draggingEnabled) {
					new WindowDragger(JOptionPane.getFrameForComponent(HeaderPane.this), HeaderPane.this);
					draggingEnabled = true;
				}
			}

			@Override
			public void ancestorMoved(AncestorEvent event) {}

			@Override
			public void ancestorRemoved(AncestorEvent event) {}
		});
		
		// initData();
	}
	
	private static class Singleton {
		private static final HeaderPane INSTANCE = new HeaderPane();
	}

	public static HeaderPane getInstance() {
		return Singleton.INSTANCE;
	}

	private void init() {
		progressBar = new JProgressBar(0, 100);
		progressBar.setIndeterminate(true);
		progressBar.setForeground(Color.white);
		progressBar.setUI(new MinimalisticProgressBarUI());
		progressBar.setVisible(false);
		
		JPanel progressBarWrapper = new JPanel();
		progressBarWrapper.setOpaque(false);
		progressBarWrapper.setLayout(new BorderLayout());
		progressBarWrapper.setPreferredSize(new Dimension(getPreferredSize().width, 2));
		progressBarWrapper.add(progressBar, BorderLayout.CENTER);
		
		add(progressBarWrapper, BorderLayout.NORTH);
		
		panelTitlebar = new JPanel();
		panelTitlebar.setOpaque(false);
		panelTitlebar.setLayout(new MigLayout("insets 2 n n n", "[]push[][][][]", ""));

		labelTitle = new JLabel(SecretwitUI.APP_NAME);
		labelTitle.setForeground(Colors.HEADER_TITLE_FG);
		labelTitle.setFont(Fonts.HEADER_TITLE);

		buttonSettings = ComponentUtils.createSmallButton();
		buttonSettings.setIcon(ImageUtils.getLocalIcon("icon_settings.png"));
		buttonSettings.setRolloverIcon(ImageUtils.getLocalIcon("icon_settings_rollover.png"));
		buttonSettings.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				SecretwitUI mainFrame = (SecretwitUI) WindowUtils.getFrameForComponent(HeaderPane.this);
				mainFrame.showHelpOverlay();
			}
		});

		buttonMinimize = ComponentUtils.createSmallButton();
		buttonMinimize.setIcon(ImageUtils.getLocalIcon("icon_min.png"));
		buttonMinimize.setRolloverIcon(ImageUtils.getLocalIcon("icon_min_rollover.png"));
		buttonMinimize.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JFrame parentFrame = (JFrame) JOptionPane.getFrameForComponent(HeaderPane.this);
				parentFrame.setExtendedState(JFrame.ICONIFIED);
			}
		});

		buttonMaximize = ComponentUtils.createSmallButton();
		buttonMaximize.setIcon(ImageUtils.getLocalIcon("icon_max.png"));
		buttonMaximize.setRolloverIcon(ImageUtils.getLocalIcon("icon_max_rollover.png"));
		buttonMaximize.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JFrame parentFrame = (JFrame) JOptionPane.getFrameForComponent(HeaderPane.this);
				if (parentFrame.getExtendedState() == JFrame.MAXIMIZED_BOTH)
					parentFrame.setExtendedState(JFrame.NORMAL);
				else
					parentFrame.setExtendedState(JFrame.MAXIMIZED_BOTH);
			}
		});

		buttonClose = ComponentUtils.createSmallButton();
		buttonClose.setIcon(ImageUtils.getLocalIcon("icon_close.png"));
		buttonClose.setRolloverIcon(ImageUtils.getLocalIcon("icon_close_rollover.png"));
		buttonClose.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JFrame parentFrame = (JFrame) JOptionPane.getFrameForComponent(HeaderPane.this);
				parentFrame.dispatchEvent(new WindowEvent(parentFrame, WindowEvent.WINDOW_CLOSING));
			}
		});

		panelTitlebar.add(labelTitle);
		panelTitlebar.add(buttonSettings);
		panelTitlebar.add(buttonMinimize, "gap unrel");
		panelTitlebar.add(buttonMaximize);
		panelTitlebar.add(buttonClose);

		add(panelTitlebar, BorderLayout.CENTER);

		panelNavigation = new JPanel();
		panelNavigation.setOpaque(false);
		panelNavigation.setLayout(new MigLayout("insets 2 6 2 6", "[]push[]", ""));

		labelPlace = new JLabel();
		labelPlace.setFont(Fonts.HEADER_PLACE);
		labelPlace.setForeground(Colors.HEADER_NAVIGATION_FG);
		labelPlace.setVerticalAlignment(SwingConstants.CENTER);
		labelPlace.setText("@offline");

		labelUser = new JLabel();
		labelUser.setHorizontalTextPosition(SwingConstants.LEADING);
		labelUser.setHorizontalAlignment(SwingConstants.RIGHT);
		labelUser.setVerticalAlignment(SwingConstants.CENTER);
		labelUser.setForeground(Colors.HEADER_USER_FG);
		labelUser.setFont(Fonts.HEADER_USER);
		labelUser.setIcon(ImageUtils.getLocalIcon("twitter_disabled.png"));
		labelUser.setText(" ");
		
		labelUser.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (!TwitterClient.getInstance().isSignedIn()) {
					setPlace("sign in");
					ViewManager.getInstance().showSignupView();
				}
				else {
					TwitterClient.getInstance().signOut();
					setPlace("@public");
					ViewManager.getInstance().showTimelineView();
					TimelinePane.getInstance().reload();
					MessagePane.getInstance().setEnabled(false);
					setUserStatus(null, false);
				}
			}
		});

		panelNavigation.add(labelPlace);
		panelNavigation.add(labelUser);

		add(panelNavigation, BorderLayout.SOUTH);
	}
	
	public void initData() {
		setPlace(TwitterClient.getInstance().isSignedIn() ? "@home" : "@public");
		setUserStatus(TwitterClient.getInstance().getUser(), TwitterClient.getInstance().isSignedIn());
	}

	public void setPlace(String name) {
		labelPlace.setText(name);
	}
	
	public void setUserStatus(String username, boolean signedIn) {
		if (signedIn) {
			if (username != null)
				labelUser.setText(username);

			labelUser.setIcon(ImageUtils.getLocalIcon("twitter.png"));
			labelUser.setToolTipText("Click to logout");
		}
		else {
			labelUser.setText("Login");
			labelUser.setIcon(ImageUtils.getLocalIcon("twitter_disabled.png"));
			labelUser.setToolTipText("Click to login");
		}
	}
	
	public void setProgressBarVisible(boolean visible) {
		progressBar.setVisible(visible);
	}

}
