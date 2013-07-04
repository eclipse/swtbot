/*******************************************************************************
 * Copyright (c) 2004, 2009 MAKE Technologies Inc and others
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   MAKE Technologies Inc - initial API and implementation
 *   Mariot Chauvin <mariot.chauvin@obeo.fr> - refactoring
 *******************************************************************************/
package org.eclipse.swtbot.eclipse.gef.finder.widgets;

import java.lang.reflect.Method;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Widget;
import org.eclipse.swtbot.swt.finder.finders.UIThreadRunnable;
import org.eclipse.swtbot.swt.finder.exceptions.WidgetNotFoundException;
import org.eclipse.swtbot.swt.finder.matchers.WidgetMatcherFactory;
import org.eclipse.swtbot.swt.finder.results.VoidResult;
import org.eclipse.swtbot.swt.finder.utils.MessageFormat;
import org.eclipse.swtbot.swt.finder.utils.internal.Assert;
import org.eclipse.swtbot.swt.finder.widgets.AbstractSWTBot;
import org.hamcrest.Matcher;

/**
 * a context menu implementation that finds the menu and invokes it in one UIRunnable so as to avoid the
 * disposed-before-clicked issue that occurs within Eclipse.
 *
 * @author David Green
 *
 */
public class SWTBotGefContextMenu extends AbstractSWTBot<Control>{
	private final String text;
	private final Control control;

	public SWTBotGefContextMenu(final Control control, final String text) {
		super(control);
		this.text = text;
		this.control = control;
	}

	public SWTBotGefContextMenu click() throws WidgetNotFoundException {
		final boolean[] clicked = new boolean[1];
		// must async exec in case the call opens a dialog
		UIThreadRunnable.asyncExec(this.display, new VoidResult() {
			public void run() {
				Menu menu = control.getMenu();
				invokeMenuInternal(menu, WidgetMatcherFactory.withMnemonic(text),clicked, true);
			}
		});
		// use another sync exec to ensure that previous call was processed fully
		UIThreadRunnable.syncExec(this.display,new VoidResult() {
			public void run() {
				// do nothing, just wait for all events to be processed
			}
		});
		if (!clicked[0]) {
			throw new WidgetNotFoundException(text);
		}
		return this;
	}


	private boolean invokeMenuInternal(final Menu bar, final Matcher<? extends Widget> matcher,final boolean[] clickInitiated, final boolean recursive) {
		if (bar != null) {
			bar.notifyListeners(SWT.Show, new Event());
			try {
				MenuItem[] items = bar.getItems();
				for (int i = 0; i < items.length; i++) {
					MenuItem menuItem = items[i];
					if (isSeparator(menuItem)) {
						continue;
					}
					if (matcher.matches(menuItem)) {
						clickInitiated[0] = true;
						click(menuItem);
						return true;
					}
					if (recursive) {
						if (invokeMenuInternal(menuItem.getMenu(), matcher,clickInitiated, recursive)) {
							return true;
						}
					}
				}
			} finally {
				bar.notifyListeners(SWT.Hide, new Event());
			}
		}
		return false;
	}

	private void click(MenuItem menuItem) {
		assertEnabled(menuItem);
		int style = menuItem.getStyle();
		if (hasStyle(style, SWT.CHECK) || hasStyle(style, SWT.RADIO)) {
			menuItem.setSelection(!menuItem.getSelection());
		}
		Event event = new Event();
		event.time = (int) System.currentTimeMillis();
		event.widget = menuItem;
		event.display = menuItem.getDisplay();
		menuItem.notifyListeners(SWT.Selection,event);
	}

	private boolean hasStyle(int style, int flag) {
		return (style & flag) == flag;
	}

	private boolean isSeparator(MenuItem menuItem) {
		// FIXME see https://bugs.eclipse.org/bugs/show_bug.cgi?id=208188
		// FIXED > 20071101 https://bugs.eclipse.org/bugs/show_bug.cgi?id=208188#c2
		return (menuItem.getStyle() & SWT.SEPARATOR) != 0;
	}


	 private void assertEnabled(MenuItem menuItem) {
	        Assert.isTrue(isEnabled(menuItem), MessageFormat.format("Widget {0} is not enabled.", this)); //$NON-NLS-1$ //$NON-NLS-2$
	 }


	protected boolean isEnabled(Widget widget) {
		try {
			Method method = widget.getClass().getMethod("isEnabled");
			return (Boolean) method.invoke(widget);
		} catch (Exception e) {
			return false;
		}
	}
}
