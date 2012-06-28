package com.scto.filerenamer;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;

import android.os.Bundle;

import android.preference.PreferenceManager;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TabWidget;
import android.widget.TextView;
import android.widget.Button;
import android.widget.Toast;

import java.util.ArrayList;

import com.scto.filerenamer.DebugLog;
import com.scto.filerenamer.BuildConfig;
import com.scto.filerenamer.AboutDialog.AboutDialogListener;
import com.scto.filerenamer.HelpDialog.HelpDialogListener;
import com.scto.filerenamer.ExitDialog.ExitDialogListener;
import com.scto.filerenamer.WhatsNewDialog.WhatsNewDialogListener;
import com.scto.filerenamer.Eula;

public class MainActivity extends FragmentActivity implements 
				SharedPreferences.OnSharedPreferenceChangeListener,
				AboutDialogListener,
				HelpDialogListener,
				ExitDialogListener,
				WhatsNewDialogListener
				
{
 	private static final String TAG = MainActivity.class.getSimpleName();
	private static SharedPreferences sSettings;
	private static ActionBar mActionBar = null;
	
	private static int mThemeId = -1;
	private static int mRootAccess = -1;

	private static final String PRIVATE_PREF = "filerenamer";
	private static final String VERSION_KEY = "version_number";
	
	Button bRename, bSettings, bAbout, bHelp, bExit;
	TextView tvDisplay, tvTitle;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate( Bundle savedInstanceState )
	{
		Eula.showEula( this );
		
		if( BuildConfig.DEBUG )
		{
			Log.i( "[" + TAG + "]", "onCreate( Bundle savedInstanceState )" );
		}
		SharedPreferences settings = getSettings(this);
		settings.registerOnSharedPreferenceChangeListener(this);
		sSettings = settings;

		if( sSettings.getBoolean( "change_theme", false ) == false )
		{
			if( BuildConfig.DEBUG )
			{
				Log.i( "[" + TAG + "]", "sSettings.getBoolean == false" );
				Log.i( "[" + TAG + "]", "mThemeId = R.style.AppTheme_Light" );
			}			
			mThemeId = R.style.AppTheme_Light;
			setTheme( mThemeId );
		}
		else
		{
			if( BuildConfig.DEBUG )
			{
				Log.i( "[" + TAG + "]", "sSettings.getBoolean == true" );
				Log.i( "[" + TAG + "]", "mThemeId = R.style.AppTheme_Dark" );
			}			
			mThemeId = R.style.AppTheme_Dark;
			setTheme( mThemeId );			
		}		
		
        super.onCreate( savedInstanceState );
        setContentView( R.layout.main );
		
		tvDisplay = ( TextView ) findViewById( R.id.tvDisplay );

		bRename = ( Button ) findViewById( R.id.bRename );
		bSettings = ( Button ) findViewById( R.id.bSettings );
		bAbout = ( Button ) findViewById( R.id.bAboutUs );
		bHelp = ( Button ) findViewById( R.id.bHelp );
		bExit = ( Button ) findViewById( R.id.bExit );

		mActionBar = getActionBar();
		if( mActionBar != null )
		{
			mActionBar.show();
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
			if( BuildConfig.DEBUG )
			{
				Log.i( "[" + TAG + "]", "getIntent().getExtras() : extras != null" );
			}
			this.setTitle( extras.getString( "dir" ) + " :: " + getString( R.string.app_name ) );
		}
		else
		{
			if( BuildConfig.DEBUG )
			{
				Log.w( "[" + TAG + "]", "getIntent().getExtras() : extras == null" );
			}			
			this.setTitle( " :: " + getString( R.string.app_name ) );
		}
		
		bRename.setOnClickListener( new View.OnClickListener()
		{
			public void onClick( View v )
			{
				if( BuildConfig.DEBUG )
				{
					Log.i( "[" + TAG + "]", "bRename.setOnClickListener() : Clicked" );
				}			
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
				if( BuildConfig.DEBUG )
				{
					Log.i( "[" + TAG + "]", "bSettings.setOnClickListener() : Clicked" );
				}			
				Intent openPreferencesActivity = new Intent( "com.scto.filerenamer.PREFERENCESACTIVITY" );
				startActivity( openPreferencesActivity );
			}
		});

		bAbout.setOnClickListener( new View.OnClickListener()
		{
			public void onClick( View v )
			{
				if( BuildConfig.DEBUG )
				{
					Log.i( "[" + TAG + "]", "bAbout.setOnClickListener() : Clicked" );
				}			
				FragmentManager fm = getSupportFragmentManager();
				AboutDialog aboutDialog = new AboutDialog();
				aboutDialog.show( fm, "dlg_about" );
			}
		});

		bHelp.setOnClickListener( new View.OnClickListener()
		{
			public void onClick( View v )
			{
				if( BuildConfig.DEBUG )
				{
					Log.i( "[" + TAG + "]", "bHelp.setOnClickListener() : Clicked" );
				}			
				FragmentManager fm = getSupportFragmentManager();
				HelpDialog helpDialog = new HelpDialog();
				helpDialog.show( fm, "dlg_help" );
			}
		});

		bExit.setOnClickListener( new View.OnClickListener()
		{
			public void onClick( View v )
			{
				if( BuildConfig.DEBUG )
				{
					Log.i( "[" + TAG + "]", "bExit.setOnClickListener() : Clicked" );
				}			
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
			if( BuildConfig.DEBUG )
			{
				Log.i( "[" + TAG + "]", "onBackPressed() : Clicked" );
			}			
		}
	}
	
	@Override
	protected void onSaveInstanceState( Bundle outState )
	{
		super.onSaveInstanceState( outState );
		outState.putInt( "theme", mThemeId );
	}

	@Override
    public void onFinishAboutDialog( boolean exit )
	{
		if( BuildConfig.DEBUG )
		{
			Log.i( "[" + TAG + "]", "onFinishAboutDialog() : Recived" );
		}
		if( exit == true )
		{
			if( BuildConfig.DEBUG )
			{
				Log.i( "[" + TAG + "]", "onFinishAboutDialog() : exit == true" );
			}			
		}
    }
	
	@Override
    public void onFinishHelpDialog( boolean exit )
	{
		if( BuildConfig.DEBUG )
		{
			Log.i( "[" + TAG + "]", "onFinishHelpDialog() : Recived" );
		}
		if( exit == true )
		{
			if( BuildConfig.DEBUG )
			{
				Log.i( "[" + TAG + "]", "onFinishHelpDialog() : exit == true" );
			}			
		}
    }
	
	@Override
    public void onFinishExitDialog( boolean exit )
	{
		if( BuildConfig.DEBUG )
		{
			Log.i( "[" + TAG + "]", "onFinishExitDialog() : Recived" );
		}
		if( exit == true )
		{
			if( BuildConfig.DEBUG )
			{
				Log.i( "[" + TAG + "]", "onFinishExitDialog() : exit == true" );
			}			
			finish();
		}
    }
		
	@Override
    public void onFinishWhatsNewDialog( boolean exit )
	{
		if( BuildConfig.DEBUG )
		{
			Log.i( "[" + TAG + "]", "onFinishWhatsNewDialog() : Recived" );
		}
		if( exit == true )
		{
			if( BuildConfig.DEBUG )
			{
				Log.i( "[" + TAG + "]", "onFinishWhatsNewDialog() : exit == true" );
			}			
		}
    }
	
	public static SharedPreferences getSettings( Context context )
	{
		if( sSettings == null )
		{
			sSettings = PreferenceManager.getDefaultSharedPreferences( context );
		}
		return sSettings;
	}

	@Override
	public void onSharedPreferenceChanged( SharedPreferences settings, String key )
	{
		loadPreference( key );
	}

	private void loadPreference(String key)
	{
		SharedPreferences settings = getSettings(this);
		if ("change_theme".equals(key))
		{
			boolean theme = settings.getBoolean( "change_theme", false );
			if( theme == false )
			{
				mThemeId = R.style.AppTheme_Light;
			}
			else
			{
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
		super.onDestroy();	
	}
	
	@Override
	public void onRestart()
	{
		super.onRestart();
	}
	
	@Override
	public void recreate()
	{
		super.recreate();
	}
	
	private void init() {
    	SharedPreferences settings = getSharedPreferences(PRIVATE_PREF, Context.MODE_PRIVATE);
    	int currentVersionNumber = 0;

		int savedVersionNumber = settings.getInt(VERSION_KEY, 0);

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
			
   	 		Editor editor = settings.edit();
   	 		editor.putInt( VERSION_KEY, currentVersionNumber );
   	 		editor.commit();
   	 	}
	}
}
