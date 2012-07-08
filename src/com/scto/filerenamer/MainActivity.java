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

import android.app.*;
import android.content.*;
import android.content.pm.*;
import android.content.res.*;
import android.os.*;
import android.support.v4.app.*;
import android.util.*;
import android.view.*;
import android.widget.*;
import com.scto.filerenamer.ExitDialog.*;
import com.scto.filerenamer.HelpDialog.*;
import com.scto.filerenamer.WhatsNewDialog.*;

import android.support.v4.app.FragmentManager;

public class MainActivity extends FragmentActivity implements 
				SharedPreferences.OnSharedPreferenceChangeListener,
				HelpDialogListener,
				ExitDialogListener,
				WhatsNewDialogListener
				
{
 	private static final String TAG = MainActivity.class.getSimpleName();
	private static SharedPreferences mSharedPreferences;
	private static ActionBar mActionBar = null;
	
	private static int mThemeId = -1;
	
	Button bRename, bSettings, bAbout, bHelp, bExit;
	TextView tvDisplay, tvTitle;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate( Bundle savedInstanceState )
	{
		Eula.showEula( this, getApplicationContext() );
	
		try
		{
			mSharedPreferences = Prefs.getSharedPreferences( this );
		}
		catch( NullPointerException e )
		{
			if( BuildConfig.DEBUG )
			{
				Log.w( "[" + TAG + "]", "mSharedPreferences == NullPointerException :" + e.getMessage() );
			}			
		}
		
		mSharedPreferences.registerOnSharedPreferenceChangeListener( this );

		if( Prefs.getThemeType( this ) == false )
		{
			mThemeId = R.style.AppTheme_Light;
			setTheme( mThemeId );
		}
		else
		{
			mThemeId = R.style.AppTheme_Dark;
			setTheme( mThemeId );			
		}		
		
		mActionBar = getActionBar();
		if( mActionBar != null )
		{
			mActionBar.setDisplayHomeAsUpEnabled( false );
			mActionBar.setDisplayShowHomeEnabled( true );
			mActionBar.setDisplayShowTitleEnabled( true );
			//mActionBar.setNavigationMode( ActionBar.NAVIGATION_MODE_TABS );
		}
		else
		{
			if( BuildConfig.DEBUG )
			{
				Log.w( "[" + TAG + "]", "mActionBar == null" );
			}
		}

		Bundle extras = getIntent().getExtras();
		if( extras != null )
		{
			this.setTitle( extras.getString( "dir" ) + " :: " + getString( R.string.app_name ) );
		}
		else
		{
			this.setTitle( " :: " + getString( R.string.app_name ) );
		}
		
        super.onCreate( savedInstanceState );
        setContentView( R.layout.main );
		
		tvDisplay = ( TextView ) findViewById( R.id.tvDisplay );

		bRename = ( Button ) findViewById( R.id.bRename );
		bSettings = ( Button ) findViewById( R.id.bSettings );
		bHelp = ( Button ) findViewById( R.id.bHelp );
		bExit = ( Button ) findViewById( R.id.bExit );
		
		bRename.setOnClickListener( new View.OnClickListener()
		{
			public void onClick( View v )
			{
				Intent openAndroidFileBrowser = new Intent("com.scto.filerenamer.ANDROIDFILEBROWSER");
				openAndroidFileBrowser.putExtra("what", "renamer" );
				openAndroidFileBrowser.putExtra("theme", mThemeId );
				startActivity( openAndroidFileBrowser );
			}
		});

		bSettings.setOnClickListener( new View.OnClickListener()
		{
			public void onClick( View v )
			{
				Intent openPreferencesActivity = new Intent( "com.scto.filerenamer.PREFERENCESACTIVITY" );
				startActivity( openPreferencesActivity );
			}
		});

		bHelp.setOnClickListener( new View.OnClickListener()
		{
			public void onClick( View v )
			{
				FragmentManager fm = getSupportFragmentManager();
				HelpDialog helpDialog = new HelpDialog();
				helpDialog.show( fm, "dlg_help" );
			}
		});

		bExit.setOnClickListener( new View.OnClickListener()
		{
			public void onClick( View v )
			{
				FragmentManager fm = getSupportFragmentManager();
				ExitDialog exitDialog = new ExitDialog();
				exitDialog.show( fm, "dlg_exit" );
			}
		});
		
		init();
	}

	@Override
	public void onBackPressed()
	{
		if( BuildConfig.DEBUG )
		{
			Log.i( "[" + TAG + "]", "onBackPressed() : Clicked" );			
		}
	}
	
	@Override
	protected void onSaveInstanceState( Bundle outState )
	{
        outState.putInt( "theme", mThemeId );
		super.onSaveInstanceState( outState );
	}
	
	@Override
	public void onConfigurationChanged( Configuration newConfig )
	{
		super.onConfigurationChanged( newConfig );
	}
	
	@Override
    public void onFinishHelpDialog( boolean exit )
	{
		if( exit == true )
		{

		}
    }
	
	@Override
    public void onFinishExitDialog( boolean exit )
	{
		if( exit == true )
		{
			finish();
		}
    }
		
	@Override
    public void onFinishWhatsNewDialog( boolean exit )
	{
		if( exit == true )
		{

		}
    }
	
	@Override
	public void onSharedPreferenceChanged( SharedPreferences settings, String key )
	{
		loadPreference( key );
	}

	private void loadPreference( String key )
	{
		if ("change_theme".equals(key))
		{
			if( Prefs.getThemeType( this ) == false )
			{
				Prefs.setThemeType( this, false );
				mThemeId = R.style.AppTheme_Light;
			}
			else
			{
				Prefs.setThemeType( this, true );
				mThemeId = R.style.AppTheme_Dark;
			}
			updateTheme();
		}
	}

	public void updateTheme()
	{
		setTheme( mThemeId );
		this.recreate();
	}
	
	@Override
	public void onStart()
	{
		super.onStart();
	}
	
	@Override
	public void onStop()
	{
		super.onStop();
	}
		
	@Override
	public void onPause()
	{
		super.onPause();
	}
	
	@Override
	public void onResume()
	{
		super.onResume();
	}
	
	@Override
	public void onDestroy()
	{
		mSharedPreferences.unregisterOnSharedPreferenceChangeListener( this );
		super.onDestroy();
	}
	
	@Override
	public void onRestart()
	{
		super.onRestart();
	}
		
	private void init() {
		int currentVersionNumber = 0;
		int savedVersionNumber = Prefs.getVersionNumber( this );

		try
		{
   	 		PackageInfo pi = getPackageManager().getPackageInfo(getPackageName(), 0);
    	 	currentVersionNumber = pi.versionCode;
   	 	}
		catch( Exception e )
		{

		}

   	 	if( currentVersionNumber > savedVersionNumber )
		{   	 		
			FragmentManager fm = getSupportFragmentManager();
			WhatsNewDialog whatsNewDialog = new WhatsNewDialog();
			whatsNewDialog.show( fm, "dlg_whats_new" );
			
			Prefs.setVersionNumber( this, currentVersionNumber );
   	 	}
	}
}
