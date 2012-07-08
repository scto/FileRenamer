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
import android.graphics.*;
import android.graphics.drawable.*;
import android.os.*;
import android.preference.*;
import android.util.*;
import android.view.*;
import android.widget.*;
import java.io.*;
import java.util.*;

public class AndroidFileBrowser extends ListActivity implements
 						SharedPreferences.OnSharedPreferenceChangeListener,
						View.OnLongClickListener
{
 	private static final String TAG = AndroidFileBrowser.class.getSimpleName();

	private static SharedPreferences mSettings;	
	private static Intent mIntent;
	private static ActionBar mActionBar;
	private static ActionMode mActionMode;
	private static int mThemeId = 0;
	
	private enum DISPLAYMODE{ ABSOLUTE, RELATIVE; }
	private final DISPLAYMODE mDisplayMode = DISPLAYMODE.RELATIVE;

	private List< AndroidFileBrowserFile > mDirectoryEntries = new ArrayList< AndroidFileBrowserFile >();
	private File mCurrentDirectory;
	private File mSdcard;
	private String mstrSelectedFileString;
	private int mSelectedFileEntryPosition;
		
	/** Called when the activity is first created. */
	@Override
	public void onCreate( Bundle savedInstanceState )
	{
		SharedPreferences settings = getSettings(this);
		settings.registerOnSharedPreferenceChangeListener(this);
		mSettings = settings;

		if( mSettings.getBoolean( "change_theme", false ) == false )
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
		ListView lv = getListView();
		lv.setCacheColorHint( Color.TRANSPARENT );
		lv.setFastScrollEnabled( true );
		
		mActionBar = getActionBar();
		if( mActionBar != null )
		{
			mActionBar.setDisplayHomeAsUpEnabled( true );
			mActionBar.setDisplayShowHomeEnabled( true );
			mActionBar.setDisplayShowTitleEnabled( true );
			//mActionBar.setNavigationMode( ActionBar.NAVIGATION_MODE_TABS );
		}
		
		mSdcard = Environment.getExternalStorageDirectory();
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
			browseToRoot( mSdcard.toString() );	
		}
		else
		{
			localObject = new AlertDialog.Builder( this );
			( (AlertDialog.Builder )localObject )
					.setMessage( "Sdcard not found!" )
					.setTitle( "Error!" )
					.setCancelable( false )
					.setPositiveButton( "Exit", new DialogInterface.OnClickListener()
					{
						public void onClick(DialogInterface paramDialogInterface, int paramInt)
						{
							AndroidFileBrowser.this.finish();
						}
					});
			( ( AlertDialog.Builder )localObject ).create().show();
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
		if( mSettings == null )
		{
			mSettings = PreferenceManager.getDefaultSharedPreferences( context );
		}
		return mSettings;
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

	private void browseToRoot( String strPath )
	{
		if( !new ShellCommand().canSU() )
		{
			Toast.makeText( this, "SU not found", Toast.LENGTH_SHORT ).show();
		}
		browseTo( new File( strPath ) );
    }
	
	//new ShellCommand().su.runWaitFor( "mv /sdcard/Android/data/com.aokp.backup/files/backups /sdcard/AOKP_Backup/" );
	
	private void upOneLevel()
	{
		if( this.mCurrentDirectory.getParent() != null )
		{
			this.browseTo( this.mCurrentDirectory.getParentFile() );
		}
	}

	private void browseTo( final File aDirectory )
	{
		if( this.mDisplayMode == DISPLAYMODE.RELATIVE )
		{
			this.setTitle( aDirectory.getAbsolutePath() + " :: " + getString( R.string.app_name ) );
		}
		
		if( aDirectory.isDirectory() )
		{
			if( aDirectory.canRead() )
			{	this.mCurrentDirectory = aDirectory;
				fill( aDirectory.listFiles() );
			}
			else
			{
				Toast.makeText( this.getApplicationContext(), "Can't read folder due to permissions", Toast.LENGTH_SHORT ).show();
			}
		}
	}

	private void fill( File[] files )
	{
		this.mDirectoryEntries.clear();

		// Add the "." == "current directory"
		this.mDirectoryEntries.add( new AndroidFileBrowserFile( getString( R.string.current_dir ), getResources().getDrawable( R.drawable.folder ), true, "", "" ) );		
		
		// and the ".." == 'Up one level'
		if( this.mCurrentDirectory.getParent() != null )
		{
			//if( this.mCurrentDirectory.equals( this.mSdcard ) )
			//{

			//}
			//else
			//{
			this.mDirectoryEntries.add( new AndroidFileBrowserFile( getString( R.string.up_one_level ), getResources().getDrawable( R.drawable.uponelevel ), true, "", "" ) );
			//}
		}
		Drawable currentIcon = null;
		boolean isFolder;
		String permissions;
		String fileSize;
		
		for( File currentFile : files )
		{
			permissions = getFilePermission( currentFile );
			fileSize = getFileSize( currentFile );
			
			if( currentFile.isDirectory() )
			{
				isFolder = true;
				currentIcon = getResources().getDrawable( R.drawable.folder );
			}
			else
			{
				isFolder = false;
				String fileName = currentFile.getName();
				/* Determine the Icon to be used, 
				 * depending on the FileEndings defined in:
				 * res/values/fileendings.xml. */
				if( checkEndsWithInStringArray( fileName, getResources().getStringArray( R.array.fileEndingImage ) ) )
				{
					currentIcon = getResources().getDrawable( R.drawable.image ); 
				}
				else if( checkEndsWithInStringArray( fileName, getResources().getStringArray( R.array.fileEndingAndroid ) ) )
				{
					currentIcon = getResources().getDrawable( R.drawable.android );
				}
				else if( checkEndsWithInStringArray( fileName, getResources().getStringArray( R.array.fileEndingExecutable ) ) )
				{
					currentIcon = getResources().getDrawable( R.drawable.application );
				}
				else if( checkEndsWithInStringArray( fileName, getResources().getStringArray( R.array.fileEndingBrowser ) ) )
				{
					currentIcon = getResources().getDrawable( R.drawable.browser );
				}
				else if( checkEndsWithInStringArray( fileName, getResources().getStringArray( R.array.fileEndingPacked ) ) )
				{
					currentIcon = getResources().getDrawable( R.drawable.packed );
				}
				else if( checkEndsWithInStringArray( fileName, getResources().getStringArray( R.array.fileEndingZip ) ) )
				{
					currentIcon = getResources().getDrawable( R.drawable.zip );
				}
				else if( checkEndsWithInStringArray( fileName, getResources().getStringArray( R.array.fileEndingAudio ) ) )
				{
					currentIcon = getResources().getDrawable( R.drawable.audio );
				}
				else if( checkEndsWithInStringArray( fileName, getResources().getStringArray( R.array.fileEndingVideo ) ) )
				{
					currentIcon = getResources().getDrawable( R.drawable.video );
				}
				else if( checkEndsWithInStringArray( fileName, getResources().getStringArray( R.array.fileEndingWord ) ) )
				{
					currentIcon = getResources().getDrawable( R.drawable.word );
				}
				else if( checkEndsWithInStringArray( fileName, getResources().getStringArray( R.array.fileEndingExcel ) ) )
				{
					currentIcon = getResources().getDrawable( R.drawable.excel );
				}
				else if( checkEndsWithInStringArray( fileName, getResources().getStringArray( R.array.fileEndingPowerPoint ) ) )
				{
					currentIcon = getResources().getDrawable( R.drawable.powerpoint );
				}
				else if( checkEndsWithInStringArray( fileName, getResources().getStringArray( R.array.fileEndingText ) ) )
				{
					currentIcon = getResources().getDrawable( R.drawable.text );
				}
				else if( checkEndsWithInStringArray( fileName, getResources().getStringArray( R.array.fileEndingConf ) ) )
				{
					currentIcon = getResources().getDrawable( R.drawable.notes );
				}				
				else if( checkEndsWithInStringArray( fileName, getResources().getStringArray( R.array.fileEndingJar ) ) )
				{
					currentIcon = getResources().getDrawable( R.drawable.jar );
				}
				else if( checkEndsWithInStringArray( fileName, getResources().getStringArray( R.array.fileEndingPdf ) ) )
				{
					currentIcon = getResources().getDrawable( R.drawable.pdf );
				}
				else if( checkEndsWithInStringArray( fileName, getResources().getStringArray( R.array.fileEndingXml ) ) )
				{
					currentIcon = getResources().getDrawable( R.drawable.xml );
				}
				else
				{
					currentIcon = getResources().getDrawable( R.drawable.unknow );
				}				
			}
			switch( this.mDisplayMode )
			{
				case ABSOLUTE:
					this.mDirectoryEntries.add( new AndroidFileBrowserFile( currentFile.getPath(), currentIcon, isFolder, permissions, fileSize ) );
					break;
				case RELATIVE: 
					int currentPathStringLenght = this.mCurrentDirectory.getAbsolutePath().length();
					this.mDirectoryEntries.add( new AndroidFileBrowserFile( currentFile.getAbsolutePath().substring( currentPathStringLenght ), currentIcon, isFolder, permissions, fileSize ) );
					break;
			}
		}
		updateStorageLabel();
		Collections.sort( this.mDirectoryEntries, AndroidFileBrowserFile.mAndroidFileBrowserFileComparator );
		AndroidFileBrowserListAdapter listAdapter = new AndroidFileBrowserListAdapter( this );
		listAdapter.setListItems( this.mDirectoryEntries );		
		this.setListAdapter( listAdapter );
	}
	
	private void updateStorageLabel()
	{
		long total, aval;
		int kb = 1024;
		String srcTitle, destTitle, storageLabel;
		
		StatFs fs = new StatFs( Environment.getExternalStorageDirectory().getPath() );
		total = fs.getBlockCount() * ( fs.getBlockSize() / kb );
		aval = fs.getAvailableBlocks() * ( fs.getBlockSize() / kb );
		srcTitle = getTitle().toString();
		storageLabel = String.format( "\t\t\t\tSdCard: Total %2f GB " + "\t\tAvailable %2f GB", ( double )total / ( kb * kb ), ( double )aval / ( kb * kb ) );
		destTitle = srcTitle + " " + storageLabel;
		setTitle( destTitle );
	}

	public String getFilePermission( File file )
	{
		String per = "-";
		if( file.isDirectory() )
		{
			per += "d";
		}
		if( file.canRead() )
		{
			per += "r";
		}
		if( file.canWrite() )
		{
			per += "w";
		}
		if( file.canExecute() )
		{
			per += "x";
		}
		return per;
	}

	public String getFileSize( File file )
	{
		String fileSize;
		int KB = 1024;
		int MB = KB * KB;
		int GB = MB * KB;
		
		if( file.isFile() )
		{
			double size = file.length();
			if( size > GB )
			{
				fileSize = String.format( "%.2f Gb ", ( double )size / GB );
			}
			else if( size < GB && size > MB )
			{
				fileSize = String.format( "%.2f Mb ", ( double )size / MB );
			}
			else if( size < MB && size > KB )
			{
				fileSize = String.format( "%.2f Kb ", ( double )size / KB );
			}
			else
			{
				fileSize = String.format( "%.2f bytes ", ( double )size );
			}
			return fileSize;
		}
		else
		{
			return "";
		}
	}
	
	@Override
	protected void onListItemClick( ListView l, View v, int position, long id )
	{
		super.onListItemClick( l, v, position, id );
		if( mActionMode != null )
		{
			return;
		}

		mSelectedFileEntryPosition = position;
		mstrSelectedFileString = this.mDirectoryEntries.get( position ).getText();		
		if( mstrSelectedFileString.equals( getString( R.string.up_one_level ) ) )
		{
			this.upOneLevel();
		}
		else if( mstrSelectedFileString.equals( getString( R.string.current_dir ) ) )
		{

		}
		else
		{
			mActionMode = AndroidFileBrowser.this.startActionMode( mContentSelectionActionModeCallback );
			v.setSelected( true );
		}

	}

	public void action( MenuItem item )
	{
		switch( item.getItemId() )
		{
			case R.id.menu_open:
			{
				if( this.mCurrentDirectory.isDirectory() )
				{
					mstrSelectedFileString = this.mDirectoryEntries.get( mSelectedFileEntryPosition ).getText();
					if( mstrSelectedFileString.equals( getString( R.string.current_dir ) ) )
					{
						this.browseTo( this.mCurrentDirectory );
					}
					else if( mstrSelectedFileString.equals( getString( R.string.up_one_level ) ) )
					{
						this.upOneLevel();
					}
					else
					{
						File clickedFile = null;
						switch( this.mDisplayMode )
						{
							case RELATIVE:
								clickedFile = new File( this.mCurrentDirectory.getAbsolutePath() + this.mDirectoryEntries.get( mSelectedFileEntryPosition ).getText() );
								break;
							case ABSOLUTE:
								clickedFile = new File( this.mDirectoryEntries.get( mSelectedFileEntryPosition ).getText() );
								break;
						}
						if( clickedFile != null )
						{
							this.browseTo( clickedFile );
						}
					}
				}
				break;
			}
			case R.id.menu_select:
			{
				AlertDialog localAlertDialog;
				if( this.mCurrentDirectory.isDirectory() )
				{
					mstrSelectedFileString = this.mDirectoryEntries.get( mSelectedFileEntryPosition ).getText();
					if( mstrSelectedFileString.equals( getString( R.string.current_dir ) ) )
					{
						// Refresh
						this.mIntent = new Intent( this, FileRenamerActivity.class );
						this.mIntent.putExtra( "dir", this.mCurrentDirectory.toString() );
						Log.w( "[" + TAG + "]", "this.mCurrentDirectory.toString(): " + this.mCurrentDirectory.toString() );
						startActivity( this.mIntent );
						onDestroy();
					}
					else if( mstrSelectedFileString.equals( getString( R.string.up_one_level ) ) )
					{
						this.mIntent = new Intent( this, FileRenamerActivity.class );
						this.mIntent.putExtra( "dir", this.mCurrentDirectory.getParent() );
						Log.w( "[" + TAG + "]", "this.mCurrentDirectory.getParent(): " + this.mCurrentDirectory.getParent() );
						startActivity( this.mIntent );
						onDestroy();
					}
					else
					{
						File clickedFile = null;
						switch( this.mDisplayMode )
						{
							case RELATIVE:
								clickedFile = new File( this.mCurrentDirectory.getAbsolutePath() + this.mDirectoryEntries.get( mSelectedFileEntryPosition ).getText() );
								break;
							case ABSOLUTE:
								clickedFile = new File( this.mDirectoryEntries.get( mSelectedFileEntryPosition ).getText() );
								break;
						}
						if( clickedFile != null )
						{
							this.mIntent = new Intent( this, FileRenamerActivity.class );
							this.mIntent.putExtra( "dir", clickedFile.toString() );
							startActivity( this.mIntent );
							onDestroy();
						}
					}
				}
				else
				{
					localAlertDialog = new AlertDialog.Builder( this ).create();
					localAlertDialog.setTitle( "Error:" );
					localAlertDialog.setMessage( "No Files Found in this folder" );
					localAlertDialog.setButton( "Ok", new DialogInterface.OnClickListener()
					{
						public void onClick( DialogInterface paramDialogInterface, int paramInt )
						{
							AndroidFileBrowser.this.finish();
						}
					});
					localAlertDialog.show();
				}
				break;
			}
			case R.id.menu_cancel:
			{
				break;
			}
		}
	}
	
	private boolean checkEndsWithInStringArray( String checkItsEnd, String[] fileEndings )
	{
		for( String aEnd : fileEndings )
		{
			if( checkItsEnd.endsWith( aEnd ) )
				return true;
		}
		return false;
	}
	
	@Override
	protected void onDestroy()
	{
		super.onDestroy();
		finish();
	}
	
	@Override
	public void onCreateOptionMenu( Menu menu, MenuInflater inflater )
	{
		super.onCreateOptionsMenu( menu );
	}
	
	@Override
	public boolean onOptionsItemSelected( MenuItem item )
	{
		switch( item.getItemId() )
		{
			case android.R.id.home:
			{
				Intent intent = new Intent( this, MainActivity.class );
				intent.addFlags( Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP );
				startActivity( intent );
				return true;
			}
			default:
			{
				super.onOptionsItemSelected( item );	
			}
		}
		return true;
	}
	
	@Override
	public boolean onLongClick( View view )
	{
		if( mActionMode != null )
		{
			return false;
		}
		mActionMode = AndroidFileBrowser.this.startActionMode( mContentSelectionActionModeCallback );
		view.setSelected( true );
		return true;
	}
	
	private ActionMode.Callback mContentSelectionActionModeCallback = new ActionMode.Callback()
	{
		public boolean onCreateActionMode( ActionMode actionMode, Menu menu )
		{
			actionMode.setTitle( "Test" );
			MenuInflater inflater = getMenuInflater();
			inflater.inflate( R.menu.context_menu, menu );
			return true;
		}
	
		public boolean onPrepareActionMode( ActionMode actionMode, Menu menu )
		{
			return false;
		}
	
		public boolean onActionItemClicked( ActionMode actionMode, MenuItem menuItem )
		{
			switch( menuItem.getItemId() )
			{
				case R.id.menu_open:
				{
					action( menuItem );
					actionMode.finish();
					return true;
				}
				case R.id.menu_select:
				{
					action( menuItem );
					actionMode.finish();
					return true;
				}
				case R.id.menu_cancel:
				{
					action( menuItem );
					actionMode.finish();
					return true;
				}
			}
			return false;
		}
	
		public void onDestroyActionMode( ActionMode actionMode )
		{
			mActionMode = null;
		}
	};
}
