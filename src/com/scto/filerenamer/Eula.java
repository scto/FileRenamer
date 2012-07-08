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

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;

import java.io.IOException;
import java.io.BufferedReader;
import java.io.Closeable;
import java.io.InputStreamReader;

/**
 * Displays an EULA ("End User License Agreement") that the user has to accept before
 * using the application. Your application should call {@link Eula#showEula(android.app.Activity)}
 * in the onCreate() method of the first activity. If the user accepts the EULA, it will never
 * be shown again. If the user refuses, {@link android.app.Activity#finish()} is invoked
 * on your activity.
 */
class Eula
{
	private static Context mContext;
	private static Activity mActivity;
	//private static final String PREFERENCE_EULA_ACCEPTED = "eula_accepted";
	//private static final String PREFERENCES_EULA = "filerenamer";

    /**
     * Displays the EULA if necessary. This method should be called from the onCreate()
     * method of your main Activity.
     *
     * @param activity The Activity to finish if the user rejects the EULA.
     */
    static void showEula( final Activity activity, final Context context )
	{
		mActivity = activity;
		mContext = context;
        //final SharedPreferences preferences = activity.getSharedPreferences( PREFERENCES_EULA, Activity.MODE_PRIVATE );
        if( !Prefs.getEulaAccepted( context ) )
		{
            final AlertDialog.Builder builder = new AlertDialog.Builder( mActivity );
            builder.setTitle( R.string.eula_title );
            builder.setCancelable( true  );
            builder.setPositiveButton( R.string.eula_accept, new DialogInterface.OnClickListener()
			{
                public void onClick( DialogInterface dialog, int which )
				{
                    accept();
                }
            });
            builder.setNegativeButton( R.string.eula_refuse, new DialogInterface.OnClickListener()
			{
                public void onClick( DialogInterface dialog, int which )
				{
                    refuse( mActivity );
                }
            });
            builder.setOnCancelListener( new DialogInterface.OnCancelListener()
			{
                public void onCancel( DialogInterface dialog )
				{
                    refuse( mActivity );
                }
            });
			builder.setMessage( readFile( mActivity, R.raw.eula ) );
			builder.create().show();
        }
    }

    private static void accept()
	{
		Prefs.setEulaAccepted( mContext, true );
    }

    private static void refuse( Activity activity )
	{
        mActivity.finish();
    }

    static void showDisclaimer( Activity activity )
	{
        final AlertDialog.Builder builder = new AlertDialog.Builder( activity );
        builder.setMessage(readFile( activity, R.raw.disclaimer ) );
        builder.setCancelable( true );
        builder.setTitle( R.string.disclaimer_title );
        builder.setPositiveButton( R.string.disclaimer_accept, null );
        builder.create().show();
    }

    private static CharSequence readFile( Activity activity, int id )
	{
        BufferedReader in = null;
        try
		{
            in = new BufferedReader( new InputStreamReader( activity.getResources().openRawResource( id ) ) );
            String line;
            StringBuilder buffer = new StringBuilder();
            while( ( line = in.readLine() ) != null )
			{
				buffer.append( line ).append( '\n' );
			}
            return buffer;
        }
		catch( IOException e )
		{
            return "";
        }
		finally
		{
            closeStream( in );
        }
    }

	/**
	 * Closes the specified stream.
	 *
	 * @param stream The stream to close.
	 */
    private static void closeStream( Closeable stream )
	{
        if( stream != null )
		{
            try
			{
                stream.close();
            }
			catch( IOException e )
			{

            }
        }
    }
}
