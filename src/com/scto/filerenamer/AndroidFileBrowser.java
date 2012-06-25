package com.scto.filerenamer;

import android.app.*;
import android.content.*;
import android.graphics.drawable.*;
import android.net.*;
import android.os.*;
import android.preference.*;
import android.util.*;
import android.view.*;
import android.widget.*;
import java.io.*;
import java.util.*;

/**
 * From www.anddev.org 
 * Building a Filebrowser.
 */

public class AndroidFileBrowser extends ListActivity implements SharedPreferences.OnSharedPreferenceChangeListener
{

	private enum DISPLAYMODE{ ABSOLUTE, RELATIVE; }

	private final DISPLAYMODE mDisplayMode = DISPLAYMODE.RELATIVE;
	private List< IconifiedText > mDirectoryEntries = new ArrayList< IconifiedText >();
	private File mCurrentDirectory;
	private File mRootDirectory;
	private boolean mIsRoot = false;
	private File mSdcard;
	private String mstrCurrentDirectory;
	
 	private static final String TAG = PreferencesActivity.class.getSimpleName();
	private static SharedPreferences sSettings;
	private static int mThemeId = 0;
	
	private ArrayList< String > mListEntries = new ArrayList< String >();
	
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
		
		mSdcard = Environment.getExternalStorageDirectory();
		mstrCurrentDirectory = mSdcard.toString();
		Object localObject = Environment.getExternalStorageState();
		if( ( !( ( String )localObject ).contains( "mounted_ro" ) )
		 && ( !( ( String )localObject ).contains( "checking" ) )
		 && ( !( ( String )localObject ).contains( "unmountable" ) )
		 && ( !( ( String )localObject ).contains( "bad_removal" ) )
		 && ( !( ( String )localObject ).contains( "removed" ) )
		 && ( !( ( String )localObject ).contains( "nofs" ) )
		 && ( !( ( String )localObject ).contains( "unmounted" ) )
		 && ( !( ( String )localObject ).contains( "shared" ) ) )
		{
			browseToRoot( mstrCurrentDirectory );	
		}
		
		
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
	private void browseToRoot( String strPath )
	{
		browseTo( new File( strPath ) );
    }

	/**
	 * This function browses up one level 
	 * according to the field: currentDirectory
	 */
	private void upOneLevel()
	{
		if( this.mCurrentDirectory.getParent() != null )
		{
			this.browseTo( this.mCurrentDirectory.getParentFile() );
		}
	}

	private void browseTo( final File aDirectory )
	{
		// On relative we display the full path in the title.
		if( this.mDisplayMode == DISPLAYMODE.RELATIVE )
		{
			this.setTitle( aDirectory.getAbsolutePath() + " :: " + 
						  getString( R.string.app_name ) );
		}
		
		if( aDirectory.isDirectory() )
		{
			this.mCurrentDirectory = aDirectory;
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
		this.mDirectoryEntries.clear();

		// Add the "." == "current directory"
		this.mDirectoryEntries.add( new IconifiedText(
					getString( R.string.current_dir ), 
					getResources().getDrawable( R.drawable.folder ) ) );		
		
		// and the ".." == 'Up one level'
		if( this.mCurrentDirectory.getParent() != null )
		{
			if( this.mCurrentDirectory.equals( this.mSdcard ) )
			{
				Log.w( TAG, "[fill] [mCurrentDirectory is equal to mSdcard]" );
			}
			else
			{
				Log.w( TAG, "[fill] [mCurrentDirectory is not equal to mSdcard]" );
				this.mDirectoryEntries.add( new IconifiedText( getString( R.string.up_one_level ), getResources().getDrawable( R.drawable.uponelevel ) ) );
			}
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
			switch( this.mDisplayMode )
			{
				case ABSOLUTE:
					/* On absolute Mode, we show the full path */
					this.mDirectoryEntries.add( new IconifiedText( currentFile
																.getPath(), currentIcon ) );
					break;
				case RELATIVE: 
					/* On relative Mode, we have to cut the
					 * current-path at the beginning */
					int currentPathStringLenght = this.mCurrentDirectory.getAbsolutePath().length();
					this.mDirectoryEntries.add( new IconifiedText(
												  currentFile.getAbsolutePath().
												  substring( currentPathStringLenght ),
												  currentIcon ) );

					break;
			}
		}
		Collections.sort( this.mDirectoryEntries );

		IconifiedTextListAdapter itla = new IconifiedTextListAdapter( this );
		itla.setListItems( this.mDirectoryEntries );		
		this.setListAdapter( itla );
	}

	@Override
	protected void onListItemClick( ListView l, View v, int position, long id )
	{
		super.onListItemClick( l, v, position, id );

		String selectedFileString = this.mDirectoryEntries.get( position ).getText();
		if( selectedFileString.equals( getString( R.string.current_dir ) ) )
		{
			// Refresh
			this.browseTo( this.mCurrentDirectory );
		}
		else if( selectedFileString.equals( getString( R.string.up_one_level ) ) )
		{
			this.upOneLevel();
		}
		else
		{
			File clickedFile = null;
			switch( this.mDisplayMode )
			{
				case RELATIVE:
					clickedFile = new File( this.mCurrentDirectory.getAbsolutePath()
										   + this.mDirectoryEntries.get( position ).getText() );
					break;
				case ABSOLUTE:
					clickedFile = new File( this.mDirectoryEntries.get( position ).getText() );
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
