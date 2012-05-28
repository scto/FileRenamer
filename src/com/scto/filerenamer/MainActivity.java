/*
 * Copyright (C) 2012, Thomas Schmid / scto <tschmid35@gmail.com>
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
import android.os.*;
import android.view.*;
import android.widget.*;

public class MainActivity extends Activity
{
	Button bRename, bSettings, bAbout, bHelp, bExit;
	TextView tvDisplay;
	
	final int EXIT_DIALOG 		= 0;
	
	ActionBar mActionBar = null;

	String what;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
	{		
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
		
		tvDisplay = (TextView) findViewById(R.id.tvDisplay);

		bRename = (Button) findViewById(R.id.bRename);
		bSettings = (Button) findViewById(R.id.bSettings);
		bAbout = (Button) findViewById(R.id.bAboutUs);
		bHelp = (Button) findViewById(R.id.bHelp);
		bExit = (Button) findViewById(R.id.bExit);
		
		mActionBar = getActionBar();
		if(mActionBar != null)
		{
			mActionBar.show();
		}
		
		bRename.setOnClickListener(new View.OnClickListener()
		{
			public void onClick(View v)
			{
				Intent openFileExplorerActivity = new Intent("com.scto.filerenamer.FILEEXPLORERACTIVITY");
				openFileExplorerActivity.putExtra("what", "renamer");
				startActivity(openFileExplorerActivity);
			}
		});

		bSettings.setOnClickListener(new View.OnClickListener()
		{
			public void onClick(View v)
			{
				Intent openPreferencesActivity = new Intent("com.scto.filerenamer.PREFERENCESACTIVITY");
				startActivity(openPreferencesActivity);
			}
		});

		bAbout.setOnClickListener(new View.OnClickListener()
		{
			public void onClick(View v)
			{
				Intent openAboutUsActivity = new Intent("com.scto.filerenamer.ABOUTACTIVITY");
				startActivity(openAboutUsActivity);
			}
		});

		bHelp.setOnClickListener(new View.OnClickListener()
		{
			public void onClick(View v)
			{
				Intent openHelpActivity = new Intent("com.scto.filerenamer.HELPACTIVITY");
				startActivity(openHelpActivity);
			}
		});

		bExit.setOnClickListener(new View.OnClickListener()
		{
			public void onClick(View v)
			{
				showDialog(EXIT_DIALOG);
			}
		});
		
	}
	
	@Override
	protected Dialog onCreateDialog(int dialogId)
	{
		Dialog myDialog = null;
		switch(dialogId)
		{
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
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setIcon(R.drawable.ic_menu_exit);
		builder.setTitle(R.string.alertDialogExitTitleTextView);
		builder.setMessage(R.string.alertDialogExitMessageHeaderTextView); 
		builder.setPositiveButton(R.string.alertDialogExitOkButton, new DialogInterface.OnClickListener() 
		{
			public void onClick(DialogInterface dialog, int id)
			{
				// Close dialog, close finish
				dialog.dismiss();
				finish();
			}
		});

		builder.setNegativeButton(R.string.alertDialogExitNoButton, new DialogInterface.OnClickListener()
		{
			public void onClick(DialogInterface dialog, int id)
			{
				// Only close dialog
				dialog.dismiss();
			}
		});

		// nicht schliessen mit BACK-Button
		builder.setCancelable(false);
		AlertDialog dialog = builder.create();
		return dialog; 
	}	
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		//super.onCreateOptionsMenu(menu);
		MenuInflater menuInflater = getMenuInflater();
		menuInflater.inflate(R.menu.list_menu, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		Dialog myDialog = null;
		
		switch(item.getItemId())
		{
			case R.id.menu_about:
			{
				Toast.makeText(this, "Tapped about", Toast.LENGTH_SHORT).show();
				Intent openAboutActivity = new Intent("com.scto.filerenamer.ABOUTACTIVITY");
				startActivity(openAboutActivity);
				//myDialog = createAboutUsDialog();
				//showDialog(EXIT_DIALOG);
				return true;
			}
			case R.id.menu_exit:
			{
				Toast.makeText(this, "Tapped exit", Toast.LENGTH_SHORT).show();
				myDialog = createExitDialog();
				showDialog(EXIT_DIALOG);
				return true;
			}
			case R.id.menu_help:
			{
				Toast.makeText(this, "Tapped exit", Toast.LENGTH_SHORT).show();
				Intent openHelpActivity = new Intent("com.scto.filerenamer.HELPACTIVITY");
				startActivity(openHelpActivity);				
				//myDialog = createHelpDialog();
				//showDialog(HELP_DIALOG);
				return true;
			}
			case R.id.menu_settings:
			{
				Toast.makeText(this, "Tapped settings", Toast.LENGTH_SHORT).show();
				Intent openPreferencesActivity = new Intent("com.scto.filerenamer.PREFERENCESACTIVITY");
				startActivity(openPreferencesActivity);
				return true;
			}
			default:
			{
				return super.onOptionsItemSelected(item);
			}
		}
	}

	@Override
	protected void onDestroy()
	{
		super.onDestroy();
		finish();
	}	
}
	
