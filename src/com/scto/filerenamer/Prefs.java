/*
 * Copyright (C) 2012 Thomas Schmid <tschmid35@gmail.com>
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package com.scto.filerenamer;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class Prefs
{
	private static final String PREFS_APP = "filerenamer";

	public static final String KEY_CHANGE_THEME = "change_theme";
	public static final String KEY_TEXT_COLOR = "text_color";
	public static final String KEY_VERSION_NUMBER = "version_number";
	public static final String KEY_EULA_ACCEPTED = "eula_accepted";
	
	public static SharedPreferences getSharedPreferences( Context c )
	{
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences( c );
		return prefs;
	}
	
    public static boolean getThemeType( Context c )
	{
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences( c );
        return prefs.getBoolean( KEY_CHANGE_THEME, false );
    }

    public static boolean setThemeType( Context c, boolean use )
	{
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences( c );
        return prefs.edit().putBoolean( KEY_CHANGE_THEME, use ).commit();
    }

    public static int getTextColor( Context c )
	{
        SharedPreferences prefs = c.getSharedPreferences( PREFS_APP, Context.MODE_PRIVATE );
        return prefs.getInt( KEY_TEXT_COLOR, -1 );
    }

    public static boolean setTextColor( Context c, int use )
	{
        SharedPreferences prefs = c.getSharedPreferences( PREFS_APP, Context.MODE_PRIVATE );
        return prefs.edit().putInt( KEY_TEXT_COLOR, use ).commit();
    }

    public static int getVersionNumber( Context c )
	{
        SharedPreferences prefs = c.getSharedPreferences( PREFS_APP, Context.MODE_PRIVATE );
        return prefs.getInt( KEY_VERSION_NUMBER, -1 );
    }

    public static boolean setVersionNumber( Context c, int use )
	{
        SharedPreferences prefs = c.getSharedPreferences( PREFS_APP, Context.MODE_PRIVATE );
        return prefs.edit().putInt( KEY_VERSION_NUMBER, use ).commit();
    }

    public static boolean getEulaAccepted( Context c )
	{
        SharedPreferences prefs = c.getSharedPreferences( PREFS_APP, Context.MODE_PRIVATE );
        return prefs.getBoolean( KEY_EULA_ACCEPTED, false );
    }

    public static boolean setEulaAccepted( Context c, boolean use )
	{
        SharedPreferences prefs = c.getSharedPreferences( PREFS_APP, Context.MODE_PRIVATE );
        return prefs.edit().putBoolean( KEY_EULA_ACCEPTED, use ).commit();
    }
}
