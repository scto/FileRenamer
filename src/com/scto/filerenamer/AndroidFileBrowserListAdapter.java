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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Pattern;
 
public class AndroidFileBrowserListAdapter extends BaseAdapter
{
	private Context mContext;
	private List< AndroidFileBrowserFile > mItems = new ArrayList< AndroidFileBrowserFile >();
	private LayoutInflater mInflater;
	
	public AndroidFileBrowserListAdapter( Context context )
	{
		mContext = context;
		mInflater = ( LayoutInflater )mContext.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
	}

	public void addItem( AndroidFileBrowserFile it )
	{
		mItems.add( it );
		notifyDataSetChanged();
	}

	public void setListItems( List< AndroidFileBrowserFile > lit )
	{
		mItems = lit;
		notifyDataSetChanged();
	}

	public int getCount()
	{
		return mItems.size();
	}

	public Object getItem( int position )
	{
		return mItems.get( position );
	}

	public boolean areAllItemsSelectable()
	{
		return false;
	}

	public boolean isSelectable( int position )
	{ 
		try
		{
			return mItems.get( position ).isSelectable();
		}
		catch( IndexOutOfBoundsException aioobe )
		{
			return this.isSelectable( position );
		}
	}

	public long getItemId( int position )
	{
		return position;
	}

	private static class ViewHolder
	{
		public int id;
		public ImageView icon;
		public TextView text;
		public TextView perms;
		public TextView size;
	}
	
	public View getView( int position, View convertView, ViewGroup parent )
	{
		ViewHolder holder;
		
		if( convertView == null )
		{
			convertView = mInflater.inflate( R.layout.listview_row, null );
			holder = new ViewHolder();
			holder.id = position;
			holder.icon = ( ImageView )convertView.findViewById( R.id.icon );
			holder.text = ( TextView )convertView.findViewById( R.id.text );
			holder.perms = ( TextView )convertView.findViewById( R.id.perms );
			holder.size = ( TextView )convertView.findViewById( R.id.size );
			convertView.setTag( holder );
		}
		
		holder = ( ViewHolder )convertView.getTag();
		holder.id = position;
		holder.icon.setImageDrawable( mItems.get( position ).getIcon() );
		holder.text.setText( mItems.get( position ).getText() );
		holder.perms.setText( mItems.get( position ).getPerms() );
		holder.size.setText( mItems.get( position ).getSize() );
		return convertView;
	}
}
