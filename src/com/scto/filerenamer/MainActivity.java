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
import android.content.pm.ApplicationInfo;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageInfo;

import android.os.Bundle;

import android.preference.PreferenceManager;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;

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

public class MainActivity extends Activity implements SharedPreferences.OnSharedPreferenceChangeListener
{
 	private static final String TAG = MainActivity.class.getSimpleName();
	private static SharedPreferences sSettings;
	private static int mThemeId = -1;
	
	Button bRename, bSettings, bAbout, bHelp, bExit;
	TextView tvDisplay;

	final int ABOUT_DIALOG 		= 0;
	final int HELP_DIALOG 		= 1;
	final int EXIT_DIALOG 		= 2;
	
	ActionBar mActionBar = null;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate( Bundle savedInstanceState )
	{
		SharedPreferences settings = getSettings(this);
		settings.registerOnSharedPreferenceChangeListener(this);
		sSettings = settings;

		if( sSettings.getBoolean( "change_theme", false ) == false )
		{
			mThemeId = R.style.AppTheme_Light;
			setTheme( mThemeId );
		}
		else
		{
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

		bAbout.setOnClickListener( new View.OnClickListener()
		{
			public void onClick( View v )
			{
				showDialog( ABOUT_DIALOG );
			}
		});

		bHelp.setOnClickListener( new View.OnClickListener()
		{
			public void onClick( View v )
			{
				showDialog( HELP_DIALOG );
			}
		});

		bExit.setOnClickListener( new View.OnClickListener()
		{
			public void onClick( View v )
			{
				showDialog( EXIT_DIALOG );
			}
		});
	}

	@Override
	protected void onSaveInstanceState( Bundle outState )
	{
		super.onSaveInstanceState( outState );
		outState.putInt( "theme", mThemeId );
	}
	
	@Override
	protected Dialog onCreateDialog( int dialogId )
	{
		Dialog myDialog = null;
		switch( dialogId )
		{
			case ABOUT_DIALOG:
				{
					myDialog = createAboutDialog();
					break;
				}
			case HELP_DIALOG:
				{
					myDialog = createHelpDialog();
					break;
				}
			case EXIT_DIALOG:
				{
					myDialog = createExitDialog(); 
					break;
				}
		}
		return myDialog;
	}


	private AlertDialog createExitDialog()
	{
		AlertDialog.Builder builder = new AlertDialog.Builder( this );
		builder.setIcon( R.drawable.icon );
		builder.setTitle( R.string.dialog_exit_title );
		builder.setMessage( R.string.dialog_exit_message ); 
		builder.setPositiveButton( R.string.dialog_exit_positive_button, new DialogInterface.OnClickListener() 
		{
			public void onClick( DialogInterface dialog, int id )
			{
				dialog.dismiss();
				finish();
			}
		});

		builder.setNegativeButton( R.string.dialog_exit_negative_button, new DialogInterface.OnClickListener()
		{
			public void onClick( DialogInterface dialog, int id )
			{
				dialog.dismiss();
			}
		});

		builder.setCancelable( false );
		AlertDialog dialog = builder.create();
		return dialog; 
	}

	private AlertDialog createHelpDialog()
	{
		AlertDialog.Builder builder = new AlertDialog.Builder( this );
		builder.setIcon( R.drawable.icon );
		builder.setTitle( R.string.dialog_help_title );
		builder.setMessage( R.string.dialog_help_message ); 
		builder.setPositiveButton( R.string.dialog_help_positive_button, new DialogInterface.OnClickListener()
		{
			public void onClick( DialogInterface dialog, int id )
			{
				dialog.dismiss();
			}
		});

		builder.setCancelable( false );
		AlertDialog dialog = builder.create();
		return dialog; 
	}

	private AlertDialog createAboutDialog()
	{
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setIcon( R.drawable.icon );
		builder.setTitle( R.string.dialog_about_title );
		builder.setMessage( R.string.dialog_about_message ); 
		builder.setPositiveButton( R.string.dialog_about_positive_button, new DialogInterface.OnClickListener()
		{
			public void onClick( DialogInterface dialog, int id )
			{
				dialog.dismiss();
			}
		});

		builder.setCancelable( false );
		AlertDialog dialog = builder.create();
		return dialog; 
	}

	@Override
	public boolean onCreateOptionsMenu( Menu menu )
	{
		//super.onCreateOptionsMenu(menu);
		MenuInflater menuInflater = getMenuInflater();
		menuInflater.inflate( R.menu.list_menu, menu );
		return true;
	}

	@Override
	public boolean onOptionsItemSelected( MenuItem item )
	{
		Dialog myDialog = null;

		switch(item.getItemId())
		{
			case R.id.menu_about:
			{
				Toast.makeText( this, "Tapped about", Toast.LENGTH_SHORT ).show();
				myDialog = createAboutDialog();
				showDialog( ABOUT_DIALOG );
				return true;
			}
			case R.id.menu_exit:
			{
				Toast.makeText( this, "Tapped exit", Toast.LENGTH_SHORT ).show();
				myDialog = createExitDialog();
				showDialog( EXIT_DIALOG );
				return true;
			}
			case R.id.menu_help:
			{
				Toast.makeText( this, "Tapped help", Toast.LENGTH_SHORT ).show();
				myDialog = createHelpDialog();
				showDialog( HELP_DIALOG );
				return true;
			}
			case R.id.menu_settings:
			{
				Toast.makeText( this, "Tapped settings", Toast.LENGTH_SHORT ).show();
				Intent openPreferencesActivity = new Intent( "com.scto.filerenamer.PREFERENCESACTIVITY" );
				startActivity( openPreferencesActivity );
				return true;
			}
			default:
			{
				return super.onOptionsItemSelected( item );
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
}
