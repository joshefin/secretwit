/*******************************************************************************
 * Copyright (c) 2011 Željko Zirikoviæ.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package com.revolucion.secretwit.ui.timeline;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.ListCellRenderer;
import javax.swing.ListModel;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import javax.swing.plaf.basic.BasicListUI;

import org.jdesktop.animation.timing.Animator;
import org.jdesktop.animation.timing.TimingTargetAdapter;
import org.jdesktop.animation.timing.interpolation.PropertySetter;

public class TimelineListUI extends BasicListUI {

	private Animator animator;

	// private boolean firstTimeInitialization = true;
	private float cellAlpha = 1.0f;
	
	private int minAnimIndex;
	private int maxAnimIndex;

	private RolloverListener rolloverListener;
	private ComponentAdapter highlightComponentListener;
	private ListDataListener dataListener;

	private int rolledOverIndex = -1;

	@Override
	protected void installDefaults() {
		super.installDefaults();

		animator = new Animator(800);
		animator.setAcceleration(0.2f);
		animator.setDeceleration(0.6f);
		animator.addTarget(new PropertySetter(this, "cellAlpha", 0.0f));
		animator.addTarget(new TimingTargetAdapter() {
			@Override
			public void end() {
				minAnimIndex = -1;
				maxAnimIndex = -1;
				// firstTimeInitialization = false;
			}
		});
	}

	@Override
	protected void uninstallDefaults() {
		super.uninstallDefaults();

		animator = null;
	}

	@Override
	protected void installListeners() {
		super.installListeners();

		rolloverListener = new RolloverListener();

		highlightComponentListener = new ComponentAdapter() {
			public void componentMoved(ComponentEvent e) {
				fadeOutRolloverIndication();
				resetRolloverIndex();
			};
		};

		dataListener = new ListDataListener() {
			@Override
			public void intervalAdded(ListDataEvent e) {
				if (!animator.isRunning()) {
					minAnimIndex = e.getIndex0();
					maxAnimIndex = e.getIndex1();
					cellAlpha = 1.0f;
					animator.start();
				}
			}

			@Override
			public void intervalRemoved(ListDataEvent e) {}

			@Override
			public void contentsChanged(ListDataEvent e) {}
		};

		list.addMouseListener(rolloverListener);
		list.addMouseMotionListener(rolloverListener);
		list.addComponentListener(highlightComponentListener);

		if (list.getModel() != null)
			list.getModel().addListDataListener(dataListener);
	}

	@Override
	protected void uninstallListeners() {
		super.uninstallListeners();

		list.removeMouseListener(rolloverListener);
		list.removeMouseMotionListener(rolloverListener);
		list.removeComponentListener(highlightComponentListener);

		if (list.getModel() != null)
			list.getModel().removeListDataListener(dataListener);

		rolloverListener = null;
		highlightComponentListener = null;
	}

	@Override
	protected void paintCell(Graphics g, int row, Rectangle rowBounds, ListCellRenderer cellRenderer, ListModel dataModel, ListSelectionModel selModel, int leadIndex) {
		// The effect is applied for all cells if it's the first time they
		// appear, and always for newly inserted first cell
		// if (animator.isRunning() && (firstTimeInitialization || row == 0)) {
		if (animator.isRunning() && row >= minAnimIndex && row <= maxAnimIndex) {
			// Calculate cell position, set offset and paint it
			Rectangle newRowBounds = new Rectangle(rowBounds);
			newRowBounds.x = (int) (-newRowBounds.width * cellAlpha);

			super.paintCell(g, row, newRowBounds, cellRenderer, dataModel, selModel, leadIndex);

			// Paint overlay for fade in effect
			Graphics2D g2 = (Graphics2D) g.create();
			g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, cellAlpha));
			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			g2.setColor(Color.white);
			g2.fillRect(rowBounds.x, rowBounds.y, rowBounds.width, rowBounds.height);
			g2.dispose();
		}
		else
			super.paintCell(g, row, rowBounds, cellRenderer, dataModel, selModel, leadIndex);
	}

	private void fadeOutRolloverIndication() {
		if (rolledOverIndex < 0)
			return;

		Rectangle rect = list.getCellBounds(rolledOverIndex, rolledOverIndex);
		list.repaint(rect);
	}

	private void fadeInRolloverIndication() {
		if (rolledOverIndex < 0)
			return;

		Rectangle rect = list.getCellBounds(rolledOverIndex, rolledOverIndex);
		list.repaint(rect);
	}

	public float getCellAlpha() {
		return cellAlpha;
	}

	public void setCellAlpha(float cellAlpha) {
		this.cellAlpha = cellAlpha;
		list.repaint();
	}

	public int getRolledOverIndex() {
		return rolledOverIndex;
	}

	public void setRolledOverIndex(int rolledOverIndex) {
		this.rolledOverIndex = rolledOverIndex;
	}

	public void resetRolloverIndex() {
		rolledOverIndex = -1;
	}

	private final class RolloverListener implements MouseListener, MouseMotionListener {

		private void handleMove(MouseEvent e) {
			int roIndex = list.locationToIndex(e.getPoint());
			if ((roIndex >= 0) && (roIndex < list.getModel().getSize())) {
				// test actual hit
				if (!list.getCellBounds(roIndex, roIndex).contains(e.getPoint())) {
					roIndex = -1;
				}
			}
			if ((roIndex < 0) || (roIndex >= list.getModel().getSize())) {
				fadeOutRolloverIndication();
				resetRolloverIndex();
			}
			else {
				// check if this is the same index
				if ((rolledOverIndex >= 0) && (rolledOverIndex == roIndex))
					return;

				fadeOutRolloverIndication();

				// rollover on a new row
				rolledOverIndex = roIndex;
				fadeInRolloverIndication();
			}
		}

		@Override
		public void mouseDragged(MouseEvent e) {
			if (!list.isEnabled())
				return;
			handleMove(e);
		}

		@Override
		public void mouseMoved(MouseEvent e) {
			if (!list.isEnabled())
				return;
			handleMove(e);
		}

		@Override
		public void mouseClicked(MouseEvent e) {}

		@Override
		public void mousePressed(MouseEvent e) {}

		@Override
		public void mouseReleased(MouseEvent e) {}

		@Override
		public void mouseEntered(MouseEvent e) {}

		@Override
		public void mouseExited(MouseEvent e) {
			fadeOutRolloverIndication();
			resetRolloverIndex();
		}

	}

}
