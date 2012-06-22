package com.scto.filerenamer;

import android.app.*;
import android.content.*;
import android.content.pm.*;
import android.os.*;
import android.util.*;

public class Utils
{
	public static final int TYPE_ADD_NUMBER = 0;
	public static final int TYPE_ADD_CUSTOM = 1;
	public static final int TYPE_ADD_CHAR = 2;
	public static final int TYPE_ADD_DATE = 3;
	public static final int TYPE_ADD_TIME = 4;
	public static final int TYPE_REMOVE_CHAR = 5;
	
	private static int sTheme;
	
	public final static int THEME_DEFAULT = 0;
	public final static int THEME_LIGHT = 1;
	public final static int THEME_DARK = 2;

	boolean mFakeTarget;
	private ApplicationInfo mFakeInfo;
	private String mErrorMessage;
	
	public static void changeToTheme( Activity activity, int theme )
	{
		sTheme = theme;
		activity.finish();
		activity.startActivity( new Intent( activity, activity.getClass() ) );		
	}
	
	public static void onActivityCreateSetTheme( Activity activity )
	{
		switch( sTheme )
		{
			default:
			case THEME_DEFAULT:
				break;
			case THEME_LIGHT:
				activity.setTheme( R.style.AppTheme_Light );
				break;
			case THEME_DARK:
				activity.setTheme( R.style.AppTheme_Dark );
				break;
		}
	}
	
	/*	
	@Override
	public ApplicationInfo getApplicationInfo()
	{
		ApplicationInfo info;
		if( mFakeTarget )
		{
			info = mFakeInfo;
			if( info == null )
			{
				info = new ApplicationInfo( super.getApplicationInfo() );
				info.targetSdkVersion = Build.VERSION_CODES.GINGERBREAD;
				mFakeInfo = info;
			}
		}
		else
		{
			info = super.getApplicationInfo();
		}
		return info;
	}
	*/
	
	@Override
	public boolean onError( int what, int extra )
	{
		Log.e("FileRenamer", "Program error: " + what + ' ' + extra);
		return true;
	}
	
	/**
	 * Return the error message set when FLAG_ERROR is set.
	 */
	public String getErrorMessage()
	{
		return mErrorMessage;
	}
	
}
