/*******************************************************************************
 * Copyright (c) 2016 Cadence Design Systems, Inc. and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Aparna Argade(Cadence Design Systems, Inc.) - initial API and implementation
 *     Patrick Tasse - Test viewport scrolling (Bug 504483)
 *******************************************************************************/
package org.eclipse.swtbot.nebula.nattable.finder.test2;

import static org.hamcrest.Matchers.lessThanOrEqualTo;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

import org.eclipse.swtbot.nebula.nattable.finder.widgets.Position;
import org.eclipse.swtbot.nebula.nattable.finder.widgets.SWTBotNatTable;
import org.eclipse.swtbot.swt.finder.junit.SWTBotJunit4ClassRunner;
import org.eclipse.swtbot.swt.finder.keyboard.Keystrokes;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;

@RunWith(SWTBotJunit4ClassRunner.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TestNatTableOtherFeatures extends _801_VerticalCompositionWithFeaturesExample {

	/* Tests counts of visible and total counts */
	@Test
	public void testCount() {
		SWTBotNatTable nattable = bot.nattable();
		int rowCount = nattable.rowCount();
		int totalRowCount = nattable.preferredRowCount();
		assertThat(rowCount, lessThanOrEqualTo(totalRowCount));
		int columnCount = nattable.columnCount();
		int totalColumnCount = nattable.preferredColumnCount();
		assertThat(columnCount, lessThanOrEqualTo(totalColumnCount));
		assertEquals("Total thirty two rows are created", 32, totalRowCount);
		assertEquals("Total five columns are created", 5, nattable.preferredColumnCount());
	}

	/* Edits nattable cell */
	@Test
	public void testDoubleClickAndGetSetCellData() {
		SWTBotNatTable nattable = bot.nattable();
		int row = 5, col = 1;
		assertEquals("Cell data of 5,1", "aabb", nattable.getCellDataValueByPosition(row, col));
		nattable.doubleclick(row, col);
		bot.sleep(100);
		nattable.setCellDataValueByPosition(row, col, "xxyy");
		nattable.pressShortcut(Keystrokes.LF);
		bot.sleep(100);
		assertEquals("Cell data of 5,1", "xxyy", nattable.getCellDataValueByPosition(row, col));
	}

	/*
	 * Applies filter for "abc". The nattable then shows only those cells which
	 * match the filter
	 */
	@Test
	public void testClickAndGetSetCellData() {
		SWTBotNatTable nattable = bot.nattable();
		int row = 1, col = 0;
		nattable.click(row, col);
		bot.sleep(100);
		nattable.setCellDataValueByPosition(row, col, "abc");
		bot.sleep(100);
		nattable.pressShortcut(Keystrokes.LF);
		bot.sleep(100);
		assertEquals("Cell data of 2,0", "abc", nattable.getCellDataValueByPosition(row, col));
	}

	/* Sorts nattable column */
	@Test
	public void testClickColumnHeader() {
		SWTBotNatTable nattable = bot.nattable();
		int row = 0, col = 0;
		nattable.click(row, col);
		bot.sleep(100);
		assertEquals("Cell data of 2,0", "abc", nattable.getCellDataValueByPosition(2, col));
		assertEquals("Cell data of 6,0", "def", nattable.getCellDataValueByPosition(6, col));
		assertEquals("Cell data of 10,0", "ghi", nattable.getCellDataValueByPosition(10, col));
		assertEquals("Cell data of 14,0", "jkl", nattable.getCellDataValueByPosition(14, col));
	}

	/**
	 * Tests scrolling up and down.
	 */
	@Test
	public void testScrollAndReadCellData() {
		SWTBotNatTable nattable = bot.nattable();
		// viewport position is any cell below the header and filter rows
		Position position = new Position(2, 0);

		// scroll down
		assertEquals("Cell data after scrolling down", "ghi",
				nattable.getCellDataValueByPosition(nattable.scrollViewport(position, 20, 0)));

		// scroll up
		assertEquals("Cell data after scrolling up", "mno",
				nattable.getCellDataValueByPosition(nattable.scrollViewport(position, 7, 0)));

		//scroll to header
		assertEquals("Column Cell data after scrolling", "Birthday",
				nattable.getCellDataValueByPosition(nattable.scrollToColumnHeader(position, 0, 4)));
	}

	/**
	 * Tests scrolling with position that is not a viewport.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testScrollNotViewport() {
		SWTBotNatTable nattable = bot.nattable();
		// row 1 is filter row, not a viewport
		Position position = new Position(1, 0);
		nattable.scrollViewport(position, 0, 0);
	}

	/**
	 * Tests scrolling with scrollable layer position that is out of range.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testScrollOutOfRange() {
		SWTBotNatTable nattable = bot.nattable();
		// viewport position is any cell below the header and filter rows
		Position position = new Position(2, 0);
		// scrollable layer has 30 rows
		nattable.scrollViewport(position, 0, 30);
	}

}