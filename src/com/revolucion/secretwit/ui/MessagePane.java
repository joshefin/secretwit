/*******************************************************************************
 * Copyright (c) 2011 Željko Zirikoviæ.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package com.revolucion.secretwit.ui;

import java.awt.AlphaComposite;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JSeparator;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingWorker;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import javax.swing.text.PlainDocument;

import net.miginfocom.swing.MigLayout;
import twitter4j.Status;

import com.revolucion.secretwit.stego.StegoUtils;
import com.revolucion.secretwit.twitter.TwitterClient;
import com.revolucion.secretwit.ui.components.ComponentUtils;
import com.revolucion.secretwit.ui.components.MinimalisticProgressBarUI;
import com.revolucion.secretwit.ui.components.RoundBorder;
import com.revolucion.secretwit.ui.timeline.TimelinePane;
import com.revolucion.secretwit.utils.Colors;
import com.revolucion.secretwit.utils.Fonts;
import com.revolucion.secretwit.utils.ImageUtils;

public class MessagePane extends JPanel {

	private static final long serialVersionUID = 5083630688222588388L;

	private final int TEXT_MAX_LENGTH = 140;
	private int CHARS_LEFT = TEXT_MAX_LENGTH;

	private JProgressBar progressBar;
	private JTextArea areaText;
	private JTextField fieldSecretText;
	private JButton buttonSend;
	private JSeparator separator;

	private MessagePane() {
		setLayout(new MigLayout("wrap", "[grow]rel[]", "[]2[]unrel[][]"));
		setBorder(new CompoundBorder(BorderFactory.createEmptyBorder(10, 80, 10, 80), new RoundBorder(Colors.FOOTER_SECRET_TEXT_FG, 2)));
		setOpaque(false);

		init();
		
		setEnabled(false);
		
		// initData();
	}
	
	private static class Singleton {
		private static final MessagePane INSTANCE = new MessagePane();
	}

	public static MessagePane getInstance() {
		return Singleton.INSTANCE;
	}
	
	@Override
	public void setEnabled(boolean enabled) {
		super.setEnabled(enabled);
		
		areaText.setEnabled(enabled);
		fieldSecretText.setEnabled(enabled);
	}

	private void init() {
		progressBar = new JProgressBar(0, 100);
		progressBar.setIndeterminate(true);
		progressBar.setForeground(Colors.LOADING_PROGRESS_BAR);
		progressBar.setUI(new MinimalisticProgressBarUI());
		progressBar.setVisible(false);
		
		DocumentSizeFilter documentSizeFilter = new DocumentSizeFilter();
		TextLengthDocumentListener lengthDocumentListener = new TextLengthDocumentListener();

		areaText = new JTextArea();
		areaText.setBorder(BorderFactory.createEmptyBorder());
		areaText.setFont(Fonts.MESSAGE);
		areaText.setForeground(Colors.FOOTER_TEXT_FG);
		areaText.setLineWrap(true);
		areaText.setOpaque(false);
		areaText.setRows(4);
		areaText.setWrapStyleWord(true);
		((PlainDocument) areaText.getDocument()).setDocumentFilter(documentSizeFilter);
		areaText.getDocument().addDocumentListener(lengthDocumentListener);

		fieldSecretText = new BorderlessTextField();
		//fieldSecretText.setBorder(BorderFactory.createEmptyBorder());
		fieldSecretText.setFont(Fonts.MESSAGE);
		fieldSecretText.setForeground(Colors.FOOTER_SECRET_TEXT_FG);
		fieldSecretText.setOpaque(false);
		((PlainDocument) fieldSecretText.getDocument()).setDocumentFilter(documentSizeFilter);
		fieldSecretText.getDocument().addDocumentListener(lengthDocumentListener);

		buttonSend = ComponentUtils.createSmallButton();
		buttonSend.setIcon(ImageUtils.getLocalIcon("icon_send.png"));
		buttonSend.setRolloverIcon(ImageUtils.getLocalIcon("icon_send_rollover.png"));
		buttonSend.setEnabled(false);
		buttonSend.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String status = encodeMessage();
				if (status != null && !status.isEmpty()) {
					progressBar.setVisible(true);
					new StatusUpdater(status, (fieldSecretText.getText() != null)).execute();
				}
			}
		});

		separator = new JSeparator();
		separator.setForeground(Colors.FOOTER_SECRET_TEXT_FG);

		add(progressBar, "growx, wrap, height 4!");
		add(areaText, "growx");
		add(buttonSend, "spany, grow");
		add(separator, "growx");
		add(fieldSecretText, "growx");
	}
	
	public void initData() {
		setEnabled(TwitterClient.getInstance().isSignedIn());
	}
	
	private String encodeMessage() {
		String status = areaText.getText();
		if (status != null && !status.isEmpty()) {
			String messageToHide = fieldSecretText.getText();
			if (messageToHide != null && !messageToHide.isEmpty())
				status = StegoUtils.encodeTweet(status, messageToHide);
		}
		
		return status;
	}

	@Override
	protected void paintComponent(Graphics g) {
		drawCharsLeft(g);

		super.paintComponent(g);
	}

	private void drawCharsLeft(Graphics g) {
		Graphics2D g2 = (Graphics2D) g.create();
		g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.08f));

		g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		g2.setFont(Fonts.MESSAGE_CHAR_COUNT);

		String text = Integer.toString(CHARS_LEFT);
		int textWidth = g2.getFontMetrics().stringWidth(text);
		Insets insets = getInsets();
		g2.drawString(text, (getWidth() - textWidth) / 2, insets.top + 115);

		g2.dispose();
	}

	private class TextLengthDocumentListener implements DocumentListener {
		@Override
		public void insertUpdate(DocumentEvent e) {
			if (!buttonSend.isEnabled() && e.getDocument().getLength() > 0)
				buttonSend.setEnabled(true);
			
			String status = encodeMessage();
			if (status != null && !status.isEmpty())
				CHARS_LEFT = Math.max(0, TEXT_MAX_LENGTH - status.length());
			else
				CHARS_LEFT = TEXT_MAX_LENGTH;
			
			// CHARS_LEFT -= e.getLength();
			repaint();
		}

		@Override
		public void removeUpdate(DocumentEvent e) {
			if (buttonSend.isEnabled() && e.getDocument().getLength() == 0)
				buttonSend.setEnabled(false);
			
			String status = encodeMessage();
			if (status != null && !status.isEmpty())
				CHARS_LEFT = Math.max(0, TEXT_MAX_LENGTH - status.length());
			else
				CHARS_LEFT = TEXT_MAX_LENGTH;
			
			// CHARS_LEFT += e.getLength();
			repaint();
		}

		@Override
		public void changedUpdate(DocumentEvent e) {
			System.out.println("UPDATE");
		}

	}

	private class DocumentSizeFilter extends DocumentFilter {
		@Override
		public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr) throws BadLocationException {
			// if ((fb.getDocument().getLength() + string.length()) <=
			// CHARS_LEFT)
			if (string.length() <= CHARS_LEFT)
				super.insertString(fb, offset, string, attr);
		}

		@Override
		public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
			if (text != null && text.length() <= CHARS_LEFT) {
				// super.insertString(fb, offset, text, attrs);
				super.replace(fb, offset, length, text, attrs);
			}
		}
	}
	
	private class StatusUpdater extends SwingWorker<Status, Void> {
		private String status;
		private boolean hasSecret;
		
		public StatusUpdater(String status, boolean hasSecret) {
			this.status = status;
			this.hasSecret = hasSecret;
		}

		@Override
		protected Status doInBackground() throws Exception {
			Status updatedStatus = TwitterClient.getInstance().updateStatus(status);
			if (updatedStatus == null)
				return null;
			
			if (hasSecret) {
				BufferedImage image = StegoUtils.watermarkProfileImage(TwitterClient.getInstance().getProfileImage());
				if (image != null) {
					TwitterClient.getInstance().updateProfileImage(image);
				}
			}
			
			return updatedStatus;
		}
		
		@Override
		protected void done() {
			try {
				Status updatedStatus = get();
				if (updatedStatus != null) {
					areaText.setText("");
					fieldSecretText.setText("");
					areaText.requestFocusInWindow();
					TimelinePane.getInstance().addStatus(updatedStatus);
				}
				else
					JOptionPane.showMessageDialog(MessagePane.this, "<html><b>Status update failed.</b>", "Status Update Error", JOptionPane.ERROR_MESSAGE);
			}
			catch (Exception e) {}
			finally {
				progressBar.setVisible(false);
			}
		}
	}
	
	private class BorderlessTextField extends JTextField {

		private static final long serialVersionUID = -7326628303614704433L;

		@Override
		public void setBorder(Border border) {
			// do nothing
		}
		
	}

}
