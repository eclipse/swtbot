/*******************************************************************************
 * Copyright (c) 2009, 2019 SWTBot Committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 * 
 * Contributors:
 *     Ketan Padegaonkar - initial API and implementation
 *******************************************************************************/
package org.eclipse.swtbot.swt.finder.keyboard;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.jface.bindings.keys.KeyStroke;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Widget;
import org.eclipse.swtbot.swt.finder.utils.SWTUtils;
import org.eclipse.swtbot.swt.finder.utils.internal.Assert;
import org.hamcrest.SelfDescribing;

/**
 * Sends keyboard notifications using {@link Display#post(Event)}.
 * 
 * @author Ketan Padegaonkar &lt;KetanPadegaonkar [at] gmail [dot] com&gt;
 * @version $Id$
 * @see Display#KeyTable
 */
public class SWTKeyboardStrategy extends AbstractKeyboardStrategy {

	private final Display				display;

	private Widget	widget;

	private static final Set<Integer>	specialKeys	= new HashSet<Integer>();

	SWTKeyboardStrategy() {
		this.display = SWTUtils.display();
	}

	@Override
	public void init(Widget widget, SelfDescribing description) {
		this.widget = widget;
	}
	
	@Override
	public void pressKey(KeyStroke key) {
		Assert.isTrue(display.post(keyEvent(key, SWT.KeyDown)), "Could not post keyevent.");
		display.wake();
	}

	@Override
	public void releaseKey(KeyStroke key) {
		Assert.isTrue(display.post(keyEvent(key, SWT.KeyUp)), "Could not post keyevent.");
		display.wake();
	}

	private Event keyEvent(KeyStroke key, int type) {
		Event e = new Event();
		e.type = type;
		e.keyCode = keycode(key);
		e.character = character(key);
		e.widget = widget;
		return e;
	}

	private char character(KeyStroke key) {
		int naturalKey = key.getNaturalKey();
		if (specialKeys.contains(naturalKey))
			return 0;
		return (char) naturalKey;
	}

	private int keycode(KeyStroke key) {
		int naturalKey = key.getNaturalKey();
		int modifierKeys = key.getModifierKeys();
		// if it is a modifier key
		if (modifierKeys != 0)
			return modifierKeys;
		// if it is a special key
		if (specialKeys.contains(naturalKey))
			return naturalKey;
		return 0;
	}

	/* these keys are special and translated using Display#KeyTable. */
	static {
		/* function keys */
		addSpecialKeyMapping(SWT.F1);
		addSpecialKeyMapping(SWT.F2);
		addSpecialKeyMapping(SWT.F3);
		addSpecialKeyMapping(SWT.F4);
		addSpecialKeyMapping(SWT.F5);
		addSpecialKeyMapping(SWT.F6);
		addSpecialKeyMapping(SWT.F7);
		addSpecialKeyMapping(SWT.F8);
		addSpecialKeyMapping(SWT.F9);
		addSpecialKeyMapping(SWT.F10);
		addSpecialKeyMapping(SWT.F11);
		addSpecialKeyMapping(SWT.F12);

		/* direction and page navigation keys */
		addSpecialKeyMapping(SWT.HOME);
		addSpecialKeyMapping(SWT.END);
		addSpecialKeyMapping(SWT.PAGE_UP);
		addSpecialKeyMapping(SWT.PAGE_DOWN);
		addSpecialKeyMapping(SWT.ARROW_RIGHT);
		addSpecialKeyMapping(SWT.ARROW_DOWN);
		addSpecialKeyMapping(SWT.ARROW_LEFT);
		addSpecialKeyMapping(SWT.ARROW_UP);

		/* whitespace keys */
		addSpecialKeyMapping(SWT.BS);
		addSpecialKeyMapping(SWT.CR);
		addSpecialKeyMapping(SWT.DEL);
		addSpecialKeyMapping(SWT.INSERT);
		addSpecialKeyMapping(SWT.ESC);
		addSpecialKeyMapping(SWT.LF);
		addSpecialKeyMapping(SWT.TAB);
	}

	private static void addSpecialKeyMapping(int swtKey) {
		specialKeys.add(swtKey);
	}

}
