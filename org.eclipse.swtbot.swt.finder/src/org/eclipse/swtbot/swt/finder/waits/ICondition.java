/*******************************************************************************
 * Copyright (c) 2008 Ketan Padegaonkar and others.
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
package org.eclipse.swtbot.swt.finder.waits;

import org.eclipse.swtbot.swt.finder.SWTBot;

/**
 * @author Ketan Padegaonkar &lt;KetanPadegaonkar [at] gmail [dot] com&gt;
 * @see Conditions
 * @version $Id$
 * @since 1.2
 */
public interface ICondition {

	/**
	 * Tests if the condition has been met.
	 * 
	 * @return <code>true</code> if the condition is satisfied, <code>false</code> otherwise.
	 * @throws Exception if the test encounters an error while processing the check.
	 */
	boolean test() throws Exception;

	/**
	 * Initializes the condition with the given {@link SWTBot} instance. This should never be <code>null</code>.
	 * 
	 * @param bot the SWTBot instance that this instance may use to evaluate the test.
	 */
	void init(SWTBot bot);

	/**
	 * Gets the failure message when a test fails (returns <code>false</code>).
	 * 
	 * @return the failure message to show in case the test fails.
	 */
	String getFailureMessage();
}
