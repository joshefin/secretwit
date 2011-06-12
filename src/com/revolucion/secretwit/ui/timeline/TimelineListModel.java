/*******************************************************************************
 * Copyright (c) 2011 Željko Zirikoviæ.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package com.revolucion.secretwit.ui.timeline;

import java.util.LinkedList;
import java.util.List;

import javax.swing.AbstractListModel;

public class TimelineListModel<T> extends AbstractListModel {

	private static final long serialVersionUID = 3561810175567587505L;

	private LinkedList<T> elements = new LinkedList<T>();
	private int maxSize = 100;

	public TimelineListModel(int maxSize) {
		this.maxSize = maxSize;
	}

	@Override
	public int getSize() {
		return elements.size();
	}

	@Override
	public Object getElementAt(int index) {
		if (index < 0 || index > elements.size())
			return null;

		return elements.get(index);
	}

	public void clear() {
		int lastIndex = elements.size() - 1;
		elements.clear();
		if (lastIndex >= 0)
			fireIntervalRemoved(this, 0, lastIndex);
	}

	public void add(T element) {
		if (element != null) {
			elements.addFirst(element);
			fireIntervalAdded(this, 0, 0);

			if (getSize() > maxSize) {
				int lastIndex = getSize() - 1;
				elements.removeLast();
				fireIntervalRemoved(this, lastIndex, lastIndex);
			}
		}
	}

	public void addAll(List<T> elements) {
		// Leave only new elements
		elements.removeAll(this.elements);
		if (!elements.isEmpty()) {
			// Add all to begining
			this.elements.addAll(0, elements);

			// If max size is exceeded, remove some elements
			if (getSize() > maxSize) {
				while (getSize() > maxSize) {
					int lastIndex = getSize() - 1;
					this.elements.removeLast();
					fireIntervalRemoved(this, lastIndex, lastIndex);
				}
			}

			// Fire event
			fireIntervalAdded(this, 0, elements.size() - 1);
		}

		/*
		if (elements != null && !elements.isEmpty()) {
			for (int i = elements.size() - 1; i >= 0; i--) {
				T element = elements.get(i);
				if (!this.elements.contains(element))
					add(element);
			}
		}
		*/
	}

}
