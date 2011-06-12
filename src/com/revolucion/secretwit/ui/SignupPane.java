/*******************************************************************************
 * Copyright (c) 2011 Željko Zirikoviæ.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package com.revolucion.secretwit.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import net.miginfocom.swing.MigLayout;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import twitter4j.TwitterException;
import twitter4j.User;

import com.revolucion.secretwit.twitter.TwitterClient;
import com.revolucion.secretwit.ui.components.MultilineLabel;
import com.revolucion.secretwit.ui.components.NumberRenderer;
import com.revolucion.secretwit.ui.timeline.TimelinePane;
import com.revolucion.secretwit.ui.views.ViewManager;
import com.revolucion.secretwit.utils.SystemUtils;

public class SignupPane extends JPanel {

	private static final long serialVersionUID = 8664006150759412226L;
	
	private final Logger logger = LoggerFactory.getLogger(SignupPane.class);

	private MultilineLabel labelInfo;
	private NumberRenderer numberAuthorize;
	private JLabel labelAuthorize;
	private JButton buttonAuthorize;
	private NumberRenderer numberPin;
	private JLabel labelPin;
	private JTextField fieldPin;
	private JButton buttonSignIn;
	private JButton buttonCancel;

	public SignupPane() {
		setLayout(new MigLayout("align center", "[]12[]", "[]22[]6[]12[]6[]20[]"));
		setBorder(BorderFactory.createEmptyBorder(2, 10, 2, 10));
		setOpaque(false);

		init();
	}

	private void init() {
		labelInfo = new MultilineLabel("SecreTwit uses OAuth authentication for safe access to your Twitter account.");

		numberAuthorize = new NumberRenderer(1);
		labelAuthorize = new JLabel("Please first authorize this application on Twitter.com");
		buttonAuthorize = new JButton("Sign in to Twitter in browser");
		buttonAuthorize.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String authorizationUrl = TwitterClient.getInstance().getAuthorizationUrl();
				logger.info("Authorization url: {}", authorizationUrl);
				
				SystemUtils.openWebSite(authorizationUrl);
				
				/*
				int option = JOptionPane.showOptionDialog(SignupPane.this, "Please use following link to authorize this application.", "Authorization", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, new Object[] { "Open in browser", "Copy link" }, "Open in browser");
				if (option == 0) {
					// Open url in default browser
					SystemUtils.openWebSite(authorizationUrl);
				}
				else if (option == 1) {
					// Copy url to clipboard
					StringSelection contents = new StringSelection(authorizationUrl);
					Toolkit.getDefaultToolkit().getSystemClipboard().setContents(contents, contents);
				}
				*/
				
				fieldPin.setEnabled(true);
			}
		});

		numberPin = new NumberRenderer(2);
		labelPin = new JLabel("Then enter the PIN provided to you by Twitter.com");
		fieldPin = new JTextField(10);
		fieldPin.setEnabled(false);
		fieldPin.getDocument().addDocumentListener(new DocumentListener() {
			@Override
			public void removeUpdate(DocumentEvent e) {
				buttonSignIn.setEnabled(e.getLength() != 0);
			}
			
			@Override
			public void insertUpdate(DocumentEvent e) {
				buttonSignIn.setEnabled(e.getLength() != 0);
			}
			
			@Override
			public void changedUpdate(DocumentEvent e) {
				buttonSignIn.setEnabled(e.getLength() != 0);
			}
		});
		buttonSignIn = new JButton("Sign in");
		buttonSignIn.setEnabled(false);
		buttonSignIn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String pin = fieldPin.getText();
				if (pin != null && !pin.isEmpty()) {
					try {
						User authorizedUser = TwitterClient.getInstance().authorize(pin);
						if (authorizedUser != null) {
							HeaderPane.getInstance().setPlace("@home");
							HeaderPane.getInstance().setUserStatus(authorizedUser.getScreenName(), true);
							ViewManager.getInstance().showTimelineView();
							TimelinePane.getInstance().reload();
							MessagePane.getInstance().setEnabled(true);
						}
					}
					catch (TwitterException te) {
						logger.error("Authorization error. {} - {}", te.getExceptionCode(), te.getMessage());
						JOptionPane.showMessageDialog(SignupPane.this, "<html><b>Authorization failed.</b><br>" + te.getMessage(), "Authorization Error", JOptionPane.ERROR_MESSAGE);
					}
				}
			}
		});

		buttonCancel = new JButton("Cancel");
		buttonCancel.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				HeaderPane.getInstance().setPlace("@public");
				HeaderPane.getInstance().setUserStatus(null, false);
				ViewManager.getInstance().showTimelineView();
			}
		});

		add(labelInfo, "span, growx, wrap");
		add(numberAuthorize, "spany 2");
		add(labelAuthorize, "wrap");
		add(buttonAuthorize, "growx, wrap");
		add(numberPin, "spany 2");
		add(labelPin, "wrap");
		add(fieldPin, "split 2");
		add(buttonSignIn, "growx, wrap");
		add(buttonCancel, "span, tag cancel");
	}

}
