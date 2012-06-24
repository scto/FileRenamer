package com.scto.filerenamer;

import java.io.File;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.scto.filerenamer.IconifiedText;
import com.scto.filerenamer.IconifiedTextListAdapter;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnClickListener;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.drawable.Drawable;
import android.content.ContentUris;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.ListView;

import com.scto.filerenamer.DebugLog;
import com.scto.filerenamer.ExecuteAsRootBase;
import java.io.*;

/**
 * From www.anddev.org 
 * Building a Filebrowser.
 */

public class AndroidFileBrowser extends ListActivity implements SharedPreferences.OnSharedPreferenceChangeListener
{

	private enum DISPLAYMODE{ ABSOLUTE, RELATIVE; }

	private final DISPLAYMODE displayMode = DISPLAYMODE.RELATIVE;
	private List< IconifiedText > directoryEntries = new ArrayList< IconifiedText >();
	private File currentDirectory = new File( "/" );

 	private static final String TAG = PreferencesActivity.class.getSimpleName();
	private static SharedPreferences sSettings;
	private static int mThemeId = 0;
	
	private ArrayList< String > mListEntries = new ArrayList< String >();
	
	@Override
	protected ArrayList< String > getCommandsToExecute()
	{
		ArrayList< String > str = new ArrayList< String >();
		
		String mMountRootRW = "mount -o remount,rw rootfs /";
		String mMountRootRO = "mount -o remount,r rootfs /";
	
		str.add( mMountRootRW );
		str.add( "ls -l /" );
		str.add( mMountRootRO );
		return str;
	}
	
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
		browseToRoot();
	}

	@Override
	protected void onSaveInstanceState( Bundle outState )
	{
		super.onSaveInstanceState( outState );
		outState.putInt( "theme", mThemeId );
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
	
	/**
	 * This function browses to the 
	 * root-directory of the file-system.
	 */
	private void browseToRoot()
	{
		if( ExecuteAsRootBase.canRunRootCommands() )
		{
			DebugLog.w( TAG, "Can run Root command!" );
		}
		else
		{
			DebugLog.e( TAG, "Can't run Root command!" );
		}
		mListEntries = getCommandsToExecute();
		
		browseTo( new File( "/" ) );
    }

	/**
	 * This function browses up one level 
	 * according to the field: currentDirectory
	 */
	private void upOneLevel()
	{
		if( this.currentDirectory.getParent() != null )
		{
			this.browseTo( this.currentDirectory.getParentFile() );
		}
	}

	private void browseTo( final File aDirectory )
	{
		// On relative we display the full path in the title.
		if( this.displayMode == DISPLAYMODE.RELATIVE )
		{
			this.setTitle( aDirectory.getAbsolutePath() + " :: " + 
						  getString( R.string.app_name ) );
		}
		
		if( aDirectory.isDirectory() )
		{
			this.currentDirectory = aDirectory;
			fill( aDirectory.listFiles() );
		}
		else
		{
			AlertDialog.Builder builder = new AlertDialog.Builder( this );
			builder.setIcon( R.drawable.folder );
			builder.setTitle( "Question" );
			builder.setMessage( "Do you want to open that file?\n" + aDirectory.getName() ); 
			builder.setPositiveButton( "Ok", new DialogInterface.OnClickListener() 
				{
					public void onClick( DialogInterface dialog, int id )
					{
						// Lets start an intent to View the file, that was clicked...
						AndroidFileBrowser.this.openFile(aDirectory);
					}
				});

			builder.setNegativeButton( "Cancel", new DialogInterface.OnClickListener()
				{
					public void onClick( DialogInterface dialog, int id )
					{
						// Do nothing ^^
					}
				});

			builder.setCancelable( false );
			AlertDialog dialog = builder.create();
		}
	}

	private void openFile( File aFile )
	{
		Intent myIntent = new Intent( android.content.Intent.ACTION_VIEW, 
									 Uri.parse( "file://" + aFile.getAbsolutePath() ) );
		startActivity( myIntent );
	}

	private void fill( File[] files )
	{
		this.directoryEntries.clear();

		// Add the "." == "current directory"
		this.directoryEntries.add( new IconifiedText(
									  getString( R.string.current_dir ), 
									  getResources().getDrawable( R.drawable.folder ) ) );		
		// and the ".." == 'Up one level'
		if( this.currentDirectory.getParent() != null )
		{
			this.directoryEntries.add( new IconifiedText(
										  getString( R.string.up_one_level ), 
										  getResources().getDrawable( R.drawable.uponelevel ) ) );
		}

		Drawable currentIcon = null;
		for( File currentFile : files )
		{
			if( currentFile.isDirectory() )
			{
				currentIcon = getResources().getDrawable( R.drawable.folder );
			}
			else
			{
				String fileName = currentFile.getName();
				/* Determine the Icon to be used, 
				 * depending on the FileEndings defined in:
				 * res/values/fileendings.xml. */
				if( checkEndsWithInStringArray( fileName, getResources().
											  getStringArray( R.array.fileEndingImage ) ) )
				{
					currentIcon = getResources().getDrawable( R.drawable.image ); 
				}
				else if( checkEndsWithInStringArray( fileName, getResources().
													getStringArray( R.array.fileEndingWebText ) ) )
				{
					currentIcon = getResources().getDrawable( R.drawable.webtext );
				}
				else if( checkEndsWithInStringArray(fileName, getResources().
													getStringArray( R.array.fileEndingPackage ) ) )
				{
					currentIcon = getResources().getDrawable( R.drawable.packed );
				}
				else if( checkEndsWithInStringArray( fileName, getResources().
													getStringArray( R.array.fileEndingAudio ) ) )
				{
					currentIcon = getResources().getDrawable( R.drawable.audio );
				}
				else
				{
					currentIcon = getResources().getDrawable( R.drawable.text );
				}				
			}
			switch( this.displayMode )
			{
				case ABSOLUTE:
					/* On absolute Mode, we show the full path */
					this.directoryEntries.add( new IconifiedText( currentFile
																.getPath(), currentIcon ) );
					break;
				case RELATIVE: 
					/* On relative Mode, we have to cut the
					 * current-path at the beginning */
					int currentPathStringLenght = this.currentDirectory.getAbsolutePath().length();
					this.directoryEntries.add( new IconifiedText(
												  currentFile.getAbsolutePath().
												  substring( currentPathStringLenght ),
												  currentIcon ) );

					break;
			}
		}
		Collections.sort( this.directoryEntries );

		IconifiedTextListAdapter itla = new IconifiedTextListAdapter( this );
		itla.setListItems( this.directoryEntries );		
		this.setListAdapter( itla );
	}

	@Override
	protected void onListItemClick( ListView l, View v, int position, long id )
	{
		super.onListItemClick( l, v, position, id );

		String selectedFileString = this.directoryEntries.get( position ).getText();
		if( selectedFileString.equals( getString( R.string.current_dir ) ) )
		{
			// Refresh
			this.browseTo( this.currentDirectory );
		}
		else if( selectedFileString.equals( getString( R.string.up_one_level ) ) )
		{
			this.upOneLevel();
		}
		else
		{
			File clickedFile = null;
			switch( this.displayMode )
			{
				case RELATIVE:
					clickedFile = new File( this.currentDirectory.getAbsolutePath()
										   + this.directoryEntries.get( position ).getText() );
					break;
				case ABSOLUTE:
					clickedFile = new File( this.directoryEntries.get( position ).getText() );
					break;
			}
			if( clickedFile != null )
				this.browseTo( clickedFile );
		}
	}

	/** Checks whether checkItsEnd ends with
	 * one of the Strings from fileEndings */
	private boolean checkEndsWithInStringArray( String checkItsEnd, String[] fileEndings )
	{
		for( String aEnd : fileEndings )
		{
			if( checkItsEnd.endsWith( aEnd ) )
				return true;
		}
		return false;
	}
}
