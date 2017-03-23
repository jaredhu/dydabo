/*
 * Copyright (C) 2017 viswadas leher <vleher@gmail.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.dydabo.blackbox.common;

import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 *
 * @author viswadas leher <vleher@gmail.com>
 */
public class DyDaBoUtilsNGTest {

    public DyDaBoUtilsNGTest() {
    }

    @org.testng.annotations.BeforeClass
    public static void setUpClass() throws Exception {
    }

    @org.testng.annotations.AfterClass
    public static void tearDownClass() throws Exception {
    }

    @org.testng.annotations.BeforeMethod
    public void setUpMethod() throws Exception {
    }

    @org.testng.annotations.AfterMethod
    public void tearDownMethod() throws Exception {
    }

    @Test
    public void testConstructor() {
        DyDaBoUtils result = new DyDaBoUtils();
        Assert.assertNotNull(result);
    }

    /**
     * Test of isBlankOrNull method, of class DyDaBoUtils.
     */
    @Test(dataProvider = "stringtestdata")
    public void testIsBlankOrNull(boolean expResult, String[] str) {
        boolean result = DyDaBoUtils.isBlankOrNull(str);
        Assert.assertEquals(result, expResult);
    }

    @DataProvider(name = "stringtestdata")
    public Object[][] stringTestData() {
        return new Object[][]{
            {true, new String[]{"", " ", "test"}},
            {true, new String[]{"", null, "test"}},
            {true, new String[]{"abcd", " ", "test"}},
            {false, new String[]{"axd", " test ", "test"}},
            {true, new String[]{"erwer", "werwer ", null}}
        };
    }

    /**
     * Test of isValidRegex method, of class DyDaBoUtils.
     */
    @Test
    public void testIsValidRegex() {
        Assert.assertEquals(DyDaBoUtils.isValidRegex(""), false);
        Assert.assertEquals(DyDaBoUtils.isValidRegex(".*"), true);
        Assert.assertEquals(DyDaBoUtils.isValidRegex(null), false);
        Assert.assertEquals(DyDaBoUtils.isValidRegex("[]"), false);
        Assert.assertEquals(DyDaBoUtils.isValidRegex("{}"), false);
    }

    /**
     * Test of parseJsonString method, of class DyDaBoUtils.
     */
    @Test
    public void testParseJsonString() {
        Assert.assertNull(DyDaBoUtils.parseJsonString(null));
        Assert.assertNull(DyDaBoUtils.parseJsonString("{"));
        Assert.assertNull(DyDaBoUtils.parseJsonString("{]sss"));
    }

}
