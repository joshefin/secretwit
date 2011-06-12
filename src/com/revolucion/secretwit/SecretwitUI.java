/*******************************************************************************
 * Copyright (c) 2011 Zeljko Zirikovic.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GPL which 
 * accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package com.revolucion.secretwit;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.UIManager;

import com.revolucion.secretwit.twitter.TwitterClient;
import com.revolucion.secretwit.ui.HeaderPane;
import com.revolucion.secretwit.ui.HelpOverlay;
import com.revolucion.secretwit.ui.HelpOverlayExtension;
import com.revolucion.secretwit.ui.LoadingOverlay;
import com.revolucion.secretwit.ui.MessagePane;
import com.revolucion.secretwit.ui.components.GradientLineBorder;
import com.revolucion.secretwit.ui.timeline.TimelinePane;
import com.revolucion.secretwit.ui.views.ViewManager;
import com.revolucion.secretwit.utils.Colors;
import com.revolucion.secretwit.utils.SystemUtils;
import com.revolucion.secretwit.utils.SystemUtils.JavaVersion;

public class SecretwitUI extends JFrame {

	private static final long serialVersionUID = -5282747479266361018L;

	public static final String APP_NAME = "SECRETWIT BETA";

	public SecretwitUI() {
		setTitle(APP_NAME);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setUndecorated(true);
		setResizable(true);

		setLayout(new BorderLayout());
		setMinimumSize(new Dimension(500, 400));
		getContentPane().setBackground(Colors.WINDOW_BG);
		((JPanel) getContentPane()).setBorder(new GradientLineBorder(Color.darkGray, Color.lightGray, 1));

		init();

		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				TwitterClient.getInstance().shutdown();
			}
		});

		pack();
		setLocationRelativeTo(null);

		setVisible(true);

		// Initialize connection to Twitter, get info and tweets
		new Initializer().execute();
		
		// Check versions of OS and Java and notify the user
		checkJavaVersion();
	}

	private void init() {
		add(HeaderPane.getInstance(), BorderLayout.NORTH);
		add(ViewManager.getInstance().getViewPane(), BorderLayout.CENTER);
	}
	
	private void checkJavaVersion() {
		new JavaVersionChecker().execute();
	}

	private void showLoadingOverlay(boolean visible) {
		if (visible)
			setGlassPane(new LoadingOverlay());
		getGlassPane().setVisible(visible);
	}
	
	public void showHelpOverlay() {
		if (getExtendedState() == JFrame.NORMAL) {
			final HelpOverlayExtension helpOverlayExtension = new HelpOverlayExtension(this);
			final HelpOverlay helpOverlay = new HelpOverlay();
			helpOverlay.addPropertyChangeListener("visibility", new PropertyChangeListener() {
				@Override
				public void propertyChange(PropertyChangeEvent evt) {
					helpOverlayExtension.setVisible(false);
					helpOverlayExtension.dispose();
				}
			});
			
			setGlassPane(helpOverlay);
			getGlassPane().setVisible(true);
			helpOverlay.requestFocusInWindow();
			helpOverlayExtension.setVisible(true);
		}
	}

	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				try {
					UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
				}
				catch (Exception e) {}

				new SecretwitUI();
			}
		});
	}

	private class Initializer extends SwingWorker<Void, Void> {
		public Initializer() {
			showLoadingOverlay(true);
		}

		@Override
		protected Void doInBackground() throws Exception {
			HeaderPane.getInstance().initData();
			TimelinePane.getInstance().loadTweets();
			MessagePane.getInstance().initData();

			return null;
		}

		@Override
		protected void done() {
			showLoadingOverlay(false);
		}

	}
	
	private class JavaVersionChecker extends SwingWorker<JavaVersion, Void> {
		private final JavaVersion MIN_JAVA_VERSION = new JavaVersion(1.6, 10);
		
		public JavaVersionChecker() {}
		
		@Override
		protected JavaVersion doInBackground() throws Exception {
			return SystemUtils.getJavaVersion();
		}
		
		@Override
		protected void done() {
			try {
				boolean isWindows = SystemUtils.isWindows();
				
				boolean isSupportedJava = true;
				JavaVersion version = get();
				if (version != null && version.compareTo(MIN_JAVA_VERSION) < 0)
					isSupportedJava = false;
				
				if (!isWindows || !isSupportedJava) {
					String message = "<html><p>Secretwit is optimized for <b>Windows</b> OS but may work on other OSes like Linux or Mac OS X.</p><p>In order to achieve best user experience, <b>Java 1.6 update 10</b> or newer is needed.</p>";
					JOptionPane.showMessageDialog(SecretwitUI.this, message, "System Requirements", JOptionPane.WARNING_MESSAGE);
				}
			}
			catch (Throwable t) {}
		}
	}

}
