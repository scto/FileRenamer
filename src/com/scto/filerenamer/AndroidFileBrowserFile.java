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

import android.graphics.drawable.Drawable;
import java.util.Comparator;

public class AndroidFileBrowserFile implements Comparable< AndroidFileBrowserFile >
{
	private boolean mIsFolder = false;
   	private String mText = "";
	private Drawable mIcon;
	private boolean mSelectable = true;
   	private String mPerms = "";
   	private String mSize = "";

	public AndroidFileBrowserFile( String text, Drawable bullet, boolean isFolder, String permissions, String fileSize )
	{
		mIsFolder = isFolder;
		mIcon = bullet;
		mText = text;
		mPerms = permissions;
		mSize = fileSize;
	}

	public boolean isFolder()
	{
		return mIsFolder;
	}

	public void setFolder( boolean isFolder )
	{
		mIsFolder = isFolder;
	}
	
	public boolean isSelectable()
	{
		return mSelectable;
	}
	
	public void setSelectable( boolean selectable )
	{
		mSelectable = selectable;
	}
		
	public void setIcon( Drawable icon )
	{
		mIcon = icon;
	}
	
	public Drawable getIcon()
	{
		return mIcon;
	}
	
	public void setText( String text )
	{
		mText = text;
	}
	
	public String getText()
	{
		return mText;
	}

	public void setPerms( String perms )
	{
		mPerms = perms;
	}
	
	public String getPerms()
	{
		return mPerms;
	}

	public void setSize( String size )
	{
		mSize = size;
	}

	public String getSize()
	{
		return mSize;
	}
	
	@Override
	public int compareTo( AndroidFileBrowserFile other )
	{
		if( this.mText != null )
			return this.mText.compareTo( other.getText() ); 
		else 
			throw new IllegalArgumentException();
	}
	
	public static Comparator< AndroidFileBrowserFile > mAndroidFileBrowserFileComparator = new Comparator< AndroidFileBrowserFile >()
	{
	    public int compare( AndroidFileBrowserFile androidFileBrowserFile1, AndroidFileBrowserFile androidFileBrowserFile2 )
		{
			boolean androidFileBrowserFileName1 = androidFileBrowserFile1.isFolder();
			boolean androidFileBrowserFileName2 = androidFileBrowserFile2.isFolder();
			if( androidFileBrowserFileName2 == androidFileBrowserFileName1 )
			{
				return androidFileBrowserFile1.getText().compareToIgnoreCase( androidFileBrowserFile2.getText() );	
			}
			else if( androidFileBrowserFileName2 )
			{
				return 1;
			}
			return -1;
	    }
	};
}
