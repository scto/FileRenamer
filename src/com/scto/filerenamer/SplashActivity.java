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
import android.media.*;
import android.os.*;
import android.preference.*;
import android.util.*;
import android.view.*;
import java.util.*;
 
public class SplashActivity extends Activity
{
 	private static final String TAG = SplashActivity.class.getSimpleName();
	MediaPlayer ourSong;
	boolean music, splash;
	Thread mSplashThread;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
	{
        super.onCreate(savedInstanceState);

		SharedPreferences getPrefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
		splash = getPrefs.getBoolean("checkboxScreen", true);
		if(splash == true)
		{
			Log.d(TAG, "SplashScreen ist auf true gesetzt");
			setContentView(R.layout.splash);
			music = getPrefs.getBoolean("checkboxMusic", true);
			if(music == true)
			{
				Log.d(TAG, "SplashScreenMusic ist auf true gesetzt");
				ourSong = MediaPlayer.create(SplashActivity.this, R.raw.splashsound);
				ourSong.start();
			}
			else
			{
				Log.d(TAG, "SplashScreenMusic ist auf false gesetzt");
			}
			mSplashThread = new Thread()
			{
				public void run()
				{
					try
					{
						if(splash == true)
						{
							if(music == true)
							{
								sleep(14000);
							}
							else
							{
								sleep(3000);
							}
						}
						else
						{
							sleep(0);
						}
					}
					catch (InterruptedException e)
					{
						e.printStackTrace();
					}
					finally
					{
						Intent openMainActivity = new Intent("com.scto.filerenamer.MAINACTIVITY");
						startActivity(openMainActivity);
					}
				}
			};
			mSplashThread.start();			
		}
		else
		{
			Log.d(TAG, "SplashScreen ist auf false gesetzt");
			Intent openMainActivity = new Intent("com.scto.filerenamer.MAINACTIVITY");
			startActivity(openMainActivity);
		}
	}
	
	@Override	
	protected void onPause()
	{
		super.onPause();
		if(splash == true)
		{
			if(music == true)
			{
				ourSong.release();
			}			
		}
		finish();
	}
	
	/**
     * Processes splash screen touch events
     */
    @Override
    public boolean onTouchEvent(MotionEvent evt)
    {
        if(evt.getAction() == MotionEvent.ACTION_DOWN)
        {
            synchronized(mSplashThread){
                mSplashThread.notifyAll();
            }
        }
        return true;
    }
}
