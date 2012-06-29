/*
 Copyright (C) 2011 The Android Open Source Project

 Licensed under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at

 http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
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
	private static SharedPreferences sSettings;
	private static int mThemeId = 0;
		
	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate( Bundle savedInstanceState )
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
	
		ActionBar mActionBar = getActionBar();
		if( mActionBar != null )
		{
			mActionBar.setDisplayHomeAsUpEnabled( true );
			mActionBar.setDisplayShowHomeEnabled( true );
			mActionBar.setDisplayShowTitleEnabled( true );
			mActionBar.setNavigationMode( ActionBar.NAVIGATION_MODE_TABS );
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

		if( "change_theme".equals( key ) )
		{
			boolean themeId = settings.getBoolean( "change_theme", false );
			if( themeId == false )
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

			SharedPreferences settings = getSettings(this);
			sSettings = settings;
			
			WebView view = new WebView( this );
			view.getSettings().setJavaScriptEnabled( true );
			if( sSettings.getBoolean( "change_theme", false ) == false )
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
			SharedPreferences settings = getSettings( getActivity() );
			sSettings = settings;
			
			WebView view = ( WebView )super.onCreateView( inflater, container, savedInstanceState );
			view.setLayerType( View.LAYER_TYPE_SOFTWARE, null );
			view.getSettings().setJavaScriptEnabled( true );
			if( sSettings.getBoolean( "change_theme", false ) == false )
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
