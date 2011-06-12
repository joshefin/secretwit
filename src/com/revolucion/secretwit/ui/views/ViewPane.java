/*******************************************************************************
 * Copyright (c) 2011 Zeljko Zirikovic.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GPL which 
 * accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package com.revolucion.secretwit.ui.views;

import java.awt.BorderLayout;

import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import org.jdesktop.animation.timing.Animator;
import org.jdesktop.animation.transitions.ScreenTransition;
import org.jdesktop.animation.transitions.TransitionTarget;

import com.revolucion.secretwit.ui.MessagePane;
import com.revolucion.secretwit.ui.SignupPane;
import com.revolucion.secretwit.ui.timeline.TimelinePane;

public class ViewPane extends JPanel implements TransitionTarget {

	private static final long serialVersionUID = -2728286406738008633L;

	private JPanel paneSignup;
	private JPanel paneTimeline;

	private ViewType currentScreen;
	private ScreenTransition screenTransition;

	private Animator animator;
	
	private ViewChangeListener viewChangeListener;

	public ViewPane() {
		setLayout(new BorderLayout());
		setBorder(new EmptyBorder(0, 0, 0, 0));
		setOpaque(false);

		initViews();

		add(paneTimeline, BorderLayout.CENTER);

		initEffects();
	}

	private void initViews() {
		paneTimeline = new JPanel();
		paneTimeline.setLayout(new BorderLayout());
		paneTimeline.setOpaque(false);

		paneTimeline.add(TimelinePane.getInstance(), BorderLayout.CENTER);
		paneTimeline.add(MessagePane.getInstance(), BorderLayout.SOUTH);

		paneSignup = new SignupPane();
	}

	private void initEffects() {
		currentScreen = ViewType.TIMELINE;

		animator = new Animator(500);
		animator.setAcceleration(.2f);
		animator.setDeceleration(.4f);

		screenTransition = new ScreenTransition(this, this, animator);
	}

	private void startTransition(ViewType newScreen) {
		if (newScreen != currentScreen) {
			currentScreen = newScreen;
			screenTransition.start();
			
			if (viewChangeListener != null)
				viewChangeListener.viewChanged(currentScreen);
		}
	}

	@Override
	public void setupNextScreen() {
		removeAll();
		switch (currentScreen) {
			case TIMELINE:
				add(paneTimeline, BorderLayout.CENTER);
				break;
			case SIGNUP:
				add(paneSignup, BorderLayout.CENTER);
				break;
			default:
				break;
		}
	}

	public void showTimelineView() {
		startTransition(ViewType.TIMELINE);
	}

	public void showSignupView() {
		startTransition(ViewType.SIGNUP);
	}

	public ViewType getCurrentScreen() {
		return currentScreen;
	}
	
	public void setViewChangeListener(ViewChangeListener viewChangeListener) {
		this.viewChangeListener = viewChangeListener;
	}

}
