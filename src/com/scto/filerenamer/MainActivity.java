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

		//Eula.showDisclaimer( this );
		Eula.showEula( this, getApplicationContext() );
		
		mActionBar = getActionBar();
		if( mActionBar != null )
		{
			mActionBar.setDisplayHomeAsUpEnabled( false );
			mActionBar.setDisplayShowHomeEnabled( true );
			mActionBar.setDisplayShowTitleEnabled( true );
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
		
		/*
		ChangeLog cl = new ChangeLog( this );
		if( cl.firstRun() )
		{
			cl.getLogDialog().show();
		}
		*/
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
		// Checks the orientation of the screen
		if( newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE )
		{
			Toast.makeText(this, "landscape", Toast.LENGTH_SHORT).show();
		}
		else if( newConfig.orientation == Configuration.ORIENTATION_PORTRAIT )
		{
			Toast.makeText(this, "portrait", Toast.LENGTH_SHORT).show();
		}
	}
	
	@Override
    public void onFinishHelpDialog( boolean exit )
	{
		if( exit == true )
		{
			Toast.makeText( this, TAG + "onFinishHelpDialog()", Toast.LENGTH_SHORT );
		}
    }
	
	@Override
    public void onFinishExitDialog( boolean exit )
	{
		if( exit == true )
		{
			Toast.makeText( this, TAG + "onFinishExitDialog()", Toast.LENGTH_SHORT );
			finish();
		}
    }
		
	@Override
    public void onFinishWhatsNewDialog( boolean exit )
	{
		if( exit == true )
		{
			Toast.makeText( this, TAG + "onFinishWhatsNewDialog()", Toast.LENGTH_SHORT );
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
		
	private void init() 
	{
		String currentVersionName = "";
		String savedVersionName = Prefs.getVersionName( this );

		try
		{
   	 		PackageInfo pi = getPackageManager().getPackageInfo( getPackageName(), 0 );
    	 	currentVersionName = pi.packageName;
   	 	}
		catch( Exception e )
		{

		}

   	 	if( !currentVersionName.equals( savedVersionName ) )
		{   	 		
			FragmentManager fm = getSupportFragmentManager();
			WhatsNewDialog whatsNewDialog = new WhatsNewDialog();
			whatsNewDialog.show( fm, "dlg_whats_new" );
			
			Prefs.setVersionName( this, currentVersionName );
   	 	}
	}
}
