package com.scto.filerenamer;

import android.app.*;
import android.content.*;
import android.os.*;
import android.preference.*;
import android.util.*;
import android.view.*;
import android.widget.*;
import java.io.*;
import java.util.*;

public class FileExplorerActivity extends ListActivity implements SharedPreferences.OnSharedPreferenceChangeListener
{	
	private static final String TAG = FileExplorerActivity.class.getSimpleName();
	private static SharedPreferences sSettings;
	private static int mThemeId = -1;
	
	Button bSdCard;
	Button bUp;
	TextView tvFileExplorerTextView;
	
	Boolean context_menu_opened = Boolean.valueOf(false);
	
	String[] filelist;
	String[] files;
	
	int i = 0;
	
	Intent intent;
	File sdcard;
	
	String curDir;
	String tempDir;
	String what;
	
	public void buttonClicked(String paramString)
	{
		context_menu_opened = Boolean.valueOf(false);
		
		if((paramString == "Open") && (new File(this.curDir).isDirectory()))
		{
			listUpdate();
		}
		if(paramString == "Select this")
		{
			AlertDialog localAlertDialog;
			if(!what.contains("renamer"))
			{
				/*
				if(!this.what.contains("tag"))
				{
					if(this.what.contains("dup"))
						if(!new File(this.cur_dir).isDirectory())
						{
							this.cur_dir = this.temp_dir;
						}
						else if(new File(this.cur_dir).list().length != 0)
						{
							this.intent = new Intent(this, duplicate.class);
							this.intent.putExtra("dir", this.cur_dir);
							startActivity(this.intent);
							onDestroy();
						}
						else
						{
							localAlertDialog = new AlertDialog.Builder(this).create();
							localAlertDialog.setTitle("Error:");
							localAlertDialog.setMessage("No Files Found in this folder");
							localAlertDialog.setButton("Ok", new DialogInterface.OnClickListener()
								{
									public void onClick(DialogInterface paramDialogInterface, int paramInt)
									{
										FileList.this.finish();
									}
								});
							localAlertDialog.show();
						}
				}
				else if(!new File(this.cur_dir).isDirectory())
				{
					if(!this.cur_dir.endsWith(".mp3"))
					{
						this.cur_dir = this.temp_dir;
					}
					else
					{
						this.intent = new Intent(this, itag.class);
						this.intent.putExtra("dir", new File(this.cur_dir).getParent().toString());
						this.intent.putExtra("fn", new File(this.cur_dir).getName());
						startActivity(this.intent);
						onDestroy();
					}
				}
				else if(new File(this.cur_dir).list().length != 0)
				{
					this.intent = new Intent(this, tag.class);
					this.intent.putExtra("dir", this.cur_dir);
					startActivity(this.intent);
					onDestroy();
				}
				else
				{
					localAlertDialog = new AlertDialog.Builder(this).create();
					localAlertDialog.setTitle("Error:");
					localAlertDialog.setMessage("No Files Found in this folder");
					localAlertDialog.setButton("Ok", new DialogInterface.OnClickListener()
						{
							public void onClick(DialogInterface paramDialogInterface, int paramInt)
							{
								FileList.this.finish();
							}
						});
					localAlertDialog.show();
				}
				*/
			}
			else if(!new File(curDir).isDirectory())
			{
				curDir = tempDir;
			}
			else if(new File(curDir).list().length != 0)
			{
				Intent openFileRenamerActivity = new Intent("com.scto.filerenamer.FILERENAMERACTIVITY");
				openFileRenamerActivity.putExtra("dir", this.curDir);
				startActivity(openFileRenamerActivity);
				onDestroy();
			}
			else
			{
				localAlertDialog = new AlertDialog.Builder(this).create();
				localAlertDialog.setTitle("Error:");
				localAlertDialog.setMessage("No Files Found in this folder");
				localAlertDialog.setButton("Ok", new DialogInterface.OnClickListener()
				{
					public void onClick(DialogInterface paramDialogInterface, int paramInt)
					{
						FileExplorerActivity.this.finish();
					}
				});
				localAlertDialog.show();
			}
		}
		if(paramString == "Cancel")
		{
			curDir = tempDir;
		}
	}
	
	public void listUpdate()
	{
		Log.d(TAG, "listUpdate() : Check button state");
		if(curDir.compareTo(Environment.getExternalStorageDirectory().toString()) != 0)
		{
			Log.d(TAG, "listUpdate() : Enable button Up, disable button SdCard");
			bUp.setEnabled(true);
			bSdCard.setEnabled(false);
		}
		else
		{
			Log.d(TAG, "listUpdate() : Disable button Up, enable button SdCard");
			bUp.setEnabled(false);
			bSdCard.setEnabled(true);
		}

		Object localObject1 = new FilenameFilter()
		{
			//Log.d(TAG, "listUpdate() : localObject1 new FilenameFilter");
			public boolean accept(File paramFile, String paramString)
			{
				int i;
				if(!new File(paramFile + "/" + paramString).isDirectory())
				{
					Log.d(TAG, "listUpdate() : localObject1 new FilenameFilter : Isn't directory");
					i = 0;
				}
				else
				{
					Log.d(TAG, "listUpdate() : localObject1 new FilenameFilter : It's a directory");
					i = 1;
				}

				boolean b = (i != 0);
				return b;
			}
		};

		Object localObject2 = new FilenameFilter()
		{
			//Log.d(TAG, "listUpdate() : localObject2 new FilenameFilter");
			public boolean accept(File paramFile, String paramString)
			{
				int i;
				if(!new File(paramFile + "/" + paramString).isFile())
				{
					Log.d(TAG, "listUpdate() : localObject2 new FilenameFilter : Isn't a file");
					i = 0;
				}
				else
				{
					Log.d(TAG, "listUpdate() : localObject2 new FilenameFilter : It's a file");
					i = 1;
				}

				boolean b = (i != 0);
				return b;
			}
		};

		filelist = new File(curDir).list((FilenameFilter)localObject1);
		files = new File(curDir).list((FilenameFilter)localObject2);

		localObject1 = new ArrayList();
		((ArrayList)localObject1).addAll(Arrays.asList((String[])filelist.clone()));

		ArrayList localArrayList = new ArrayList();
		localArrayList.addAll(Arrays.asList((String[])files.clone()));

		Collections.sort(localArrayList);
		Collections.sort((List)localObject1);

		localObject2 = new ArrayList();
		((ArrayList)localObject2).addAll((Collection)localObject1);
		((ArrayList)localObject2).addAll(localArrayList);

		localObject1 = (String[])((ArrayList)localObject2).toArray(new String[((ArrayList)localObject2).size()]);
		filelist = ((String[])((String[])localObject1).clone());

		Log.d(TAG, "listUpdate() : setListAdapter(new MyFileExplorerArrayAdapter(this, R.layout.fileexplorerrow, ((String[])localObject1), this.curDir))");		
		setListAdapter(new MyFileExplorerArrayAdapter( this, mThemeId, ( ( String[])localObject1), this.curDir, R.layout.fileexplorerrow ) );
	}
	
	@Override
	public boolean onContextItemSelected(MenuItem paramMenuItem)
	{
		Log.d(TAG, "onContextItemSelected()");
		super.onContextItemSelected(paramMenuItem);
		buttonClicked(paramMenuItem.getTitle().toString());
		return true;
	}

	@Override
	public void onContextMenuClosed(Menu paramMenu)
	{
		Log.d(TAG, "onContextMenuClosed()");
		super.onContextMenuClosed(paramMenu);
		if(context_menu_opened.booleanValue())
		{
			curDir = tempDir;
		}
	}
	
    /** Called when the activity is first created. */
    @Override
    protected void onCreate(Bundle savedInstanceState)
	{
		Log.d(TAG, "onCreate()");
		SharedPreferences settings = getSettings(this);
		settings.registerOnSharedPreferenceChangeListener(this);
		sSettings = settings;

		if( sSettings.getBoolean( "change_theme", false ) == false )
		{
			Log.i( TAG, "onCreate: setTheme light" );
			mThemeId = R.style.AppTheme_Light;
			setTheme( mThemeId );
		}
		else
		{
			Log.i( TAG, "onCreate: setTheme dark" );
			mThemeId = R.style.AppTheme_Dark;
			setTheme( mThemeId );			
		}
		
        super.onCreate(savedInstanceState);
		setContentView(R.layout.fileexplorer);
		
		what = getIntent().getExtras().getString("what");
		if(what == null)
		{
			Log.d(TAG, "onCreate() : what == null!");
		}
		
		/*
		if(this.what.contains("tag"))
		{
			Toast.makeText(this, "To Edit a group of MP3 Files,Select the folder which contains that files.", 1).show();
		}
		*/
		
		sdcard = Environment.getExternalStorageDirectory();
		curDir = sdcard.toString();
		Object localObject = Environment.getExternalStorageState();
		
		if((!((String)localObject).contains("mounted_ro"))
			&& (!((String)localObject).contains("checking"))
			&& (!((String)localObject).contains("unmountable"))
			&& (!((String)localObject).contains("bad_removal"))
			&& (!((String)localObject).contains("removed"))
			&& (!((String)localObject).contains("nofs"))
			&& (!((String)localObject).contains("unmounted"))
			&& (!((String)localObject).contains("shared")))
		{
			//tvFileExplorerTextView = (TextView) findViewById(R.id.tvFileExplorerTextView);
			bSdCard = (Button) findViewById(R.id.bSdCard);
			bUp = (Button) findViewById(R.id.bUp);

			bSdCard.setEnabled(true);
			bUp.setEnabled(false);
			
			/*
			ListView lvListView = getListView();
			if(lvListView == null)
			{
				Log.d(TAG, "lvListView == null!");
			}

			lvListView.addHeaderView(this.tvFileExplorerTextView);
			lvListView.addHeaderView(this.bSdCard);
			lvListView.addHeaderView(this.bUp);
			lvListView.setFastScrollEnabled(true);
			
			getListView().addHeaderView(this.bSdCard);
			getListView().addHeaderView(this.bUp);
			getListView().setFastScrollEnabled(true);
			*/
			
			listUpdate();
		
			bSdCard.setOnClickListener(new View.OnClickListener()
			{
				public void onClick(View paramView)
				{
					FileExplorerActivity.this.buttonClicked("Select this");
				}
			});

			bUp.setOnClickListener(new View.OnClickListener()
			{
				public void onClick(View paramView)
				{
					FileExplorerActivity.this.curDir = new File(FileExplorerActivity.this.curDir).getParent();
					FileExplorerActivity.this.listUpdate();
				}
			});
    	}
    	else
    	{
			localObject = new AlertDialog.Builder(this);
			((AlertDialog.Builder)localObject).setMessage("Sdcard not found!").setTitle("Error!").setCancelable(false).setPositiveButton("Exit", new DialogInterface.OnClickListener()
			{
				public void onClick(DialogInterface paramDialogInterface, int paramInt)
				{
					FileExplorerActivity.this.finish();
				}
			});
			((AlertDialog.Builder)localObject).create().show();
    	}
    }
	
	@Override
	protected void onListItemClick(ListView paramListView, View paramView, int paramInt, long paramLong)
	{
		super.onListItemClick(paramListView, paramView, paramInt, paramLong);
		this.tempDir = this.curDir;
		this.context_menu_opened = Boolean.valueOf(true);
		
		this.curDir = this.curDir + "/" + this.filelist[paramInt];
				
		//this.curDir = (this.curDir + "/" + this.filelist[(paramInt - 2)]);
		
		registerForContextMenu(paramListView);
		paramView.showContextMenu();
	}
	
	@Override
	public void onCreateContextMenu(ContextMenu paramContextMenu, View paramView, ContextMenu.ContextMenuInfo paramContextMenuInfo)
	{
		super.onCreateContextMenu(paramContextMenu, paramView, paramContextMenuInfo);
		paramContextMenu.add("Select this");
		paramContextMenu.add("Open");
		paramContextMenu.add("Cancel");
		paramContextMenu.setHeaderTitle("Choose");
	}

	@Override
	protected void onDestroy()
	{
		super.onDestroy();
		finish();
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
				Log.i( TAG, "loadPreference: setTheme light" );
				mThemeId = R.style.AppTheme_Light;
			}
			else
			{
				Log.i( TAG, "loadPreference: setTheme dark" );
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
