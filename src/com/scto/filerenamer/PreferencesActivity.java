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

import android.annotation.*;
import android.app.*;
import android.content.*;
import android.graphics.*;
import android.os.*;
import android.preference.*;
import android.util.*;
import android.view.*;
import android.webkit.*;
import java.util.*;

public class PreferencesActivity extends PreferenceActivity implements SharedPreferences.OnSharedPreferenceChangeListener
{
 	private static final String TAG = PreferencesActivity.class.getSimpleName();
	private static SharedPreferences mSettings;
	private static int mThemeId = 0;

	
	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate( Bundle savedInstanceState )
	{
		mSettings = Prefs.getSharedPreferences( this );
		mSettings.registerOnSharedPreferenceChangeListener( this );

		if( Prefs.getThemeType( this ) == false )
		{
			mThemeId = R.style.AppTheme_Light;
			setTheme( mThemeId );
		}
		else
		{
			mThemeId = R.style.AppTheme_Dark;
			setTheme( mThemeId );
		}

		ActionBar mActionBar = getActionBar();
		if( mActionBar != null )
		{
			mActionBar.setDisplayHomeAsUpEnabled( true );
			mActionBar.setDisplayShowHomeEnabled( true );
			mActionBar.setDisplayShowTitleEnabled( true );
		}
		else
		{
			if( BuildConfig.DEBUG )
			{
				Log.w( "[" + TAG + "]", "mActionBar == null" );
			}
		}
		
		// Setting Theme must be done before super.onCreate() and addPreferencesFromResource!
		super.onCreate( savedInstanceState );		
		if ( Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB )
		{
			addPreferencesFromResource( R.xml.preferences );
		}
	}
	
	@Override
    public void onSaveInstanceState (Bundle outState)
	{
        super.onSaveInstanceState(outState);
	}
	
	@Override
	public void onSharedPreferenceChanged( SharedPreferences settings, String key )
	{
		loadPreference( key );
	}

	private void loadPreference(String key)
	{
		if ("change_theme".equals(key))
		{
			if( Prefs.getThemeType( this ) == false )
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
		finish();
	}	
	
	@Override
	public void onStart()
	{
		super.onStart();
	}
	
	@TargetApi(11)
	@Override
	public void onBuildHeaders( List<Header> target )
	{

		loadHeadersFromResource( R.xml.preference_headers, target );
	}

	@Override
	public void onCreateOptionMenu( Menu menu, MenuInflater inflater )
	{
		super.onCreateOptionsMenu( menu );
	}
	
	@Override
	public boolean onOptionsItemSelected( MenuItem item )
	{
		if( item.getItemId() == android.R.id.home )
		{
			Intent intent = new Intent( this, MainActivity.class );
			intent.addFlags( Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP );
			startActivity( intent );
			return true;
		}
		else
		{
			return super.onOptionsItemSelected( item );
		}
	}

	public static class PersonalActivity extends PreferenceActivity
	{		
		@SuppressWarnings("deprecation")
		@Override
		public void onCreate( Bundle savedInstanceState )
		{
			super.onCreate( savedInstanceState );
			addPreferencesFromResource( R.xml.preference_personal );
		}
		
	}

	@TargetApi(11)
	public static class PersonalFragment extends PreferenceFragment
	{
		@Override
		public void onCreate( Bundle savedInstanceState )
		{
			super.onCreate( savedInstanceState );
			addPreferencesFromResource( R.xml.preference_personal );
		}		
	}

	public static class FileRenamerActivity extends PreferenceActivity
	{
		@SuppressWarnings("deprecation")
		@Override
		public void onCreate( Bundle savedInstanceState )
		{
			super.onCreate( savedInstanceState );			
			addPreferencesFromResource( R.xml.preference_filerenamer );
		}		
	}

	@TargetApi(11)
	public static class FileRenamerFragment extends PreferenceFragment
	{
		@Override
		public void onCreate( Bundle savedInstanceState )
		{
			super.onCreate( savedInstanceState );
			addPreferencesFromResource( R.xml.preference_filerenamer );
		}
	}
	
	public static class MiscActivity extends PreferenceActivity
	{
		@SuppressWarnings("deprecation")
		@Override
		public void onCreate( Bundle savedInstanceState )
		{
			super.onCreate( savedInstanceState );
			addPreferencesFromResource( R.xml.preference_misc );
		}
	}

	@TargetApi(11)
	public static class MiscFragment extends PreferenceFragment
	{
		@Override
		public void onCreate( Bundle savedInstanceState )
		{
			super.onCreate( savedInstanceState );
			addPreferencesFromResource( R.xml.preference_misc );
		}
	}

	public static class AboutActivity extends Activity
	{
		@Override
		public void onCreate( Bundle state )
		{
			super.onCreate( state );

			WebView view = new WebView( this );
			view.getSettings().setJavaScriptEnabled( true );
			if( Prefs.getThemeType( this ) == false )
			{
				view.loadUrl( "file:///android_asset/about_holo_light.html" );
			}
			else
			{
				view.loadUrl( "file:///android_asset/about_holo_dark.html" );
			}
			view.setBackgroundColor( Color.TRANSPARENT );
			setContentView( view );
		}
	}

	@TargetApi(11)
	public static class AboutFragment extends WebViewFragment
	{
		@Override
		public View onCreateView( LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState )
		{
			WebView view = ( WebView )super.onCreateView( inflater, container, savedInstanceState );
			view.setLayerType( View.LAYER_TYPE_SOFTWARE, null );
			view.getSettings().setJavaScriptEnabled( true );
			if( Prefs.getThemeType( container.getContext() ) == false )
			{
				view.loadUrl( "file:///android_asset/about_holo_light.html" );
			}
			else
			{
				view.loadUrl( "file:///android_asset/about_holo_dark.html" );
			}
			view.setBackgroundColor( Color.TRANSPARENT );
			return view;
		}
	}
}
