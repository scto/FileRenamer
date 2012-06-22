package com.scto.filerenamer;

import android.content.*;
import android.view.*;
import android.widget.*;
import java.io.*;

public class MyFileExplorerArrayAdapter extends ArrayAdapter<String>
{
	private final Context context;
	private final String dir;
	private final int layout;
	private final String[] values;
	private final int mThemeId;
	
	public MyFileExplorerArrayAdapter( Context paramContext, int paramTheme,  String[] paramArrayOfString, String paramString, int paramInt )
	{
		super( paramContext, paramTheme, paramArrayOfString );
		this.context = paramContext;
		this.layout = paramInt;
		this.values = paramArrayOfString;
		this.dir = paramString;
		this.mThemeId = paramTheme;

	}

	@Override
	public View getView( int paramInt, View paramView, ViewGroup paramViewGroup )
	{
		context.setTheme( this.mThemeId );
		LayoutInflater inflater = ( LayoutInflater ) context.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
		View localView = inflater.inflate( this.layout, paramViewGroup, false );
		TextView localTextView = ( TextView ) localView.findViewById( R.id.rowtext );
		ImageView localImageView = ( ImageView ) localView.findViewById( R.id.icon );
		localTextView.setText( values[ paramInt ] );
				
		if( !new File( this.dir + "/" + this.values[ paramInt ] ).isFile() )
		{
			localImageView.setImageResource( R.drawable.ic_folder );
		}
		else
		{
			localImageView.setImageResource( R.drawable.ic_file );
		}
		return localView;
	}
}
