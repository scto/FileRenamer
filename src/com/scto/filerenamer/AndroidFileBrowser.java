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

/**
 * From www.anddev.org 
 * Building a Filebrowser.
 */

public class AndroidFileBrowser extends ListActivity implements SharedPreferences.OnSharedPreferenceChangeListener
{
 	private static final String TAG = AndroidFileBrowser.class.getSimpleName();
	private static SharedPreferences sSettings;
	private static Intent mIntent;
	private static int mThemeId = 0;
	private static ActionBar mActionBar;
	
	private enum DISPLAYMODE{ ABSOLUTE, RELATIVE; }
	private final DISPLAYMODE mDisplayMode = DISPLAYMODE.RELATIVE;
	private List< IconifiedText > mDirectoryEntries = new ArrayList< IconifiedText >();
	private File mCurrentDirectory;
	private File mSdcard;
	private String mstrSelectedFileString;
	private int mSelectedFileEntryPosition;
	private static ActionMode mActionMode;
	
	private boolean mRootAcess;
	
	Boolean mContextMenuOpened = Boolean.valueOf( false );	
		
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
	
		if( sSettings.getBoolean( "check_root_access_on_startup", false ) == false )
		{
			mRootAcess = false;
		}
		else
		{
			mRootAcess = true;	
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
			mActionBar.setNavigationMode( ActionBar.NAVIGATION_MODE_TABS );
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
		if( "check_root_access_on_startup".equals( key ) )
		{
			boolean root = settings.getBoolean( "check_root_access_on_startup", false );
			if( root = false )
			{
				mRootAcess = false;
			}
			else
			{
				mRootAcess = true;	
			}
		}
	}

	public void updateTheme()
	{
		setTheme( mThemeId );
		this.recreate();
	}	

	public boolean onContextItemSelected( MenuItem paramMenuItem )
	{
		super.onContextItemSelected( paramMenuItem );
		action( paramMenuItem );
		return true;
	}

	public void onContextMenuClosed( Menu paramMenu )
	{
		super.onContextMenuClosed( paramMenu );
		if( this.mContextMenuOpened.booleanValue() )
		{

		}
	}

	public void onCreateContextMenu( ContextMenu paramContextMenu, View paramView, ContextMenu.ContextMenuInfo paramContextMenuInfo )
	{
		super.onCreateContextMenu( paramContextMenu, paramView, paramContextMenuInfo );
		MenuInflater inflater = getMenuInflater();
		inflater.inflate( R.menu.context_menu, paramContextMenu );
		paramContextMenu.setHeaderTitle( R.string.contextMenuTitle );
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
			this.setTitle( aDirectory.getAbsolutePath() + " :: " + getString( R.string.app_name ) );
		}
		
		if( aDirectory.isDirectory() )
		{
			this.mCurrentDirectory = aDirectory;
			fill( aDirectory.listFiles() );
		}
	}

	private void fill( File[] files )
	{
		this.mDirectoryEntries.clear();

		// Add the "." == "current directory"
		this.mDirectoryEntries.add( new IconifiedText( getString( R.string.current_dir ), getResources().getDrawable( R.drawable.folder ) ) );		
		
		// and the ".." == 'Up one level'
		if( this.mCurrentDirectory.getParent() != null )
		{
			if( this.mCurrentDirectory.equals( this.mSdcard ) )
			{

			}
			else
			{
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
				if( checkEndsWithInStringArray( fileName, getResources().getStringArray( R.array.fileEndingImage ) ) )
				{
					currentIcon = getResources().getDrawable( R.drawable.image ); 
				}
				else if( checkEndsWithInStringArray( fileName, getResources().getStringArray( R.array.fileEndingWebText ) ) )
				{
					currentIcon = getResources().getDrawable( R.drawable.webtext );
				}
				else if( checkEndsWithInStringArray( fileName, getResources().getStringArray( R.array.fileEndingPackage ) ) )
				{
					currentIcon = getResources().getDrawable( R.drawable.packed );
				}
				else if( checkEndsWithInStringArray( fileName, getResources().getStringArray( R.array.fileEndingAudio ) ) )
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
					this.mDirectoryEntries.add( new IconifiedText( currentFile.getPath(), currentIcon ) );
					break;
				case RELATIVE: 
					/* On relative Mode, we have to cut the
					 * current-path at the beginning */
					int currentPathStringLenght = this.mCurrentDirectory.getAbsolutePath().length();
					this.mDirectoryEntries.add( new IconifiedText( currentFile.getAbsolutePath().substring( currentPathStringLenght ), currentIcon ) );
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
		if ( Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB )
		{
			this.mContextMenuOpened = Boolean.valueOf( true );
			registerForContextMenu( l );
		}
		mSelectedFileEntryPosition = position;
		mstrSelectedFileString = this.mDirectoryEntries.get( position ).getText();
		if( mstrSelectedFileString.equals( getString( R.string.up_one_level ) ) )
		{
			this.upOneLevel();
		}

		try
		{
			if ( Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB )
			{
				boolean result = v.showContextMenu();
				 if( result )
				 {
					 Log.d( "[" + TAG + "]", "ShowContextMenu Result: true" );
				 }
				 else
				 {
					 Log.d( "[" + TAG + "]", "ShowContextMenu Result: false" );
				 }		
			}
			else
			{
				if( mActionMode != null )
				{
					//return false;
				}
				mActionMode = AndroidFileBrowser.this.startActionMode( mContentSelectionActionModeCallback );
				v.setSelected( true );
			}
		}
		catch( NullPointerException e )
		{
			Log.d( "[" + TAG + "]", "NullPointerException ShowContextMenu: " + e.getMessage() );
			e.printStackTrace();
		}
	}

	public void action( MenuItem item )
	{
		this.mContextMenuOpened = Boolean.valueOf( false );
		switch( item.getItemId() )
		{
			case R.id.menu_open:
			{
				if( this.mCurrentDirectory.isDirectory() )
				{
					mstrSelectedFileString = this.mDirectoryEntries.get( mSelectedFileEntryPosition ).getText();
					if( mstrSelectedFileString.equals( getString( R.string.current_dir ) ) )
					{
						// Refresh
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
						startActivity( this.mIntent );
						onDestroy();
					}
					else if( mstrSelectedFileString.equals( getString( R.string.up_one_level ) ) )
					{
						this.mIntent = new Intent( this, FileRenamerActivity.class );
						this.mIntent.putExtra( "dir", this.mCurrentDirectory.getParent() );
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
			actionMode.setTitle( R.string.contextMenuTitle );
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
