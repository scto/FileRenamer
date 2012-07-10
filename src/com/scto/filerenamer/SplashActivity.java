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
import android.os.*;
import android.preference.*;
import android.util.*;
import android.view.*;
import java.util.*;
import android.widget.*;
 
public class SplashActivity extends Activity
{
 	private static final String TAG = SplashActivity.class.getSimpleName();
	private static SharedPreferences mSettings;
	private ViewSwitcher mViewSwitcher;
	boolean mSplash;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate( Bundle savedInstanceState )
	{
        super.onCreate(savedInstanceState);

		mSettings = Prefs.getSharedPreferences( getApplicationContext() );
		if( Prefs.getSplashScreen( getApplicationContext() ) == true )
		{
			mSplash = true;
			new LoadViewTask().execute();			
		}
		else
		{
			Intent openMainActivity = new Intent( "com.scto.filerenamer.MAINACTIVITY" );
			startActivity( openMainActivity );
		}		
	}
	
    private class LoadViewTask extends AsyncTask< Void, Integer, Void >
    {
    	private TextView mTextView;
    	private ProgressBar mProgressBar;

		@Override
		protected void onPreExecute() 
		{
	        mViewSwitcher = new ViewSwitcher( SplashActivity.this );
			mViewSwitcher.addView( ViewSwitcher.inflate( SplashActivity.this, R.layout.splash, null ) );

			mTextView = ( TextView ) mViewSwitcher.findViewById( R.id.TextViewProgress );
			mProgressBar = ( ProgressBar ) mViewSwitcher.findViewById( R.id.ProgressBar );
			mProgressBar.setMax( 100 );

			setContentView( mViewSwitcher );
		}

		@Override
		protected Void doInBackground( Void... params ) 
		{
			try 
			{
				synchronized( this ) 
				{
					int counter = 0;
					while( counter <= 100 )
					{
						this.wait( 50 );
						counter++;
						publishProgress( counter );
					}
				}
			} 
			catch( InterruptedException e ) 
			{
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onProgressUpdate( Integer... values ) 
		{
			if( values[ 0 ] <= 100 )
			{
				mTextView.setText( "Progress: " + Integer.toString( values[ 0 ] ) + "%" );
				mProgressBar.setProgress( values[ 0 ] );
			}
		}

		@Override
		protected void onPostExecute( Void result ) 
		{
			Intent openMainActivity = new Intent( "com.scto.filerenamer.MAINACTIVITY" );
			startActivity( openMainActivity );
		}
    }

    @Override
    public void onBackPressed() 
    {
    	if( mViewSwitcher.getDisplayedChild() == 0 )
    	{
    		return;
    	}
    	else
    	{
    		super.onBackPressed();
    	}
    }
	
	@Override	
	protected void onPause()
	{
		super.onPause();
		finish();
	}
}
