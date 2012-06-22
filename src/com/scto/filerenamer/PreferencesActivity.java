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
import android.graphics.*;
import android.os.*;
import android.preference.*;
import android.view.*;
import android.webkit.*;
import java.util.*;
import android.content.*;

import com.scto.filerenamer.DebugLog;

/**
 * The preferences activity in which one can change application preferences.
 */
public class PreferencesActivity extends PreferenceActivity implements SharedPreferences.OnSharedPreferenceChangeListener
{
 	private static final String TAG = PreferencesActivity.class.getSimpleName();
	private static SharedPreferences sSettings;
	private static int mThemeId = 0;

	
	/**
	 * Initialize the activity, loading the preference specifications.
	 */
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
		if ("change_theme".equals(key))
		{
			boolean theme = settings.getBoolean("change_theme", false);
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
		finish();
		/*
		Intent intent = new Intent( this, PreferencesActivity.class );
		intent.putExtra( "theme", mThemeId );
		startActivity( intent );
		this.recreate();
		*/
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
		int theme = 0;
		Bundle extras = getIntent().getExtras();
		if( extras != null )
		{
			theme = extras.getInt( "theme" );
			DebugLog.i( "Extras != null" );
		}
		else
		{
			DebugLog.e( "Extras = null" );
		}
		if( theme != 0 )
		{
			setTheme( theme );
			DebugLog.i( "Theme != 0" );
		}
		else
		{
			DebugLog.e( "theme = 0" );
		}
		
		loadHeadersFromResource( R.xml.preference_headers, target );
	}

	@Override
	public boolean onOptionsItemSelected( MenuItem item )
	{
		if( item.getItemId() == android.R.id.home )
		{
			finish();
			return true;
		}
		else
		{
			return super.onOptionsItemSelected( item );
		}
	}

	public static class PersonalActivity extends PreferenceActivity
	{
		private static SharedPreferences sSettings;
		private static boolean mThemeId = false;
		
		@SuppressWarnings("deprecation")
		@Override
		public void onCreate( Bundle savedInstanceState )
		{
			super.onCreate( savedInstanceState );

			int theme = 0;
			Bundle extras = getIntent().getExtras();
			if( extras != null )
			{
				theme = extras.getInt( "theme" );
				DebugLog.i( "Extras != null" );
			}
			else
			{
				DebugLog.e( "Extras = null" );
			}
			if( theme != 0 )
			{
				setTheme( theme );
				DebugLog.i( "Theme != 0" );
			}
			else
			{
				DebugLog.e( "theme = 0" );
			}
			
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

			int theme = 0;
			Bundle extras = getActivity().getIntent().getExtras();
			if( extras != null )
			{
				theme = extras.getInt( "theme" );
				DebugLog.i( "Extras != null" );
			}
			else
			{
				DebugLog.e( "Extras = null" );
			}
			if( theme != 0 )
			{
				getActivity().setTheme( theme );
				DebugLog.i( "Theme != 0" );
			}
			else
			{
				DebugLog.e( "theme = 0" );
			}
			
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

			int theme = 0;
			Bundle extras = getIntent().getExtras();
			if( extras != null )
			{
				theme = extras.getInt( "theme" );
				DebugLog.i( "Extras != null" );
			}
			else
			{
				DebugLog.e( "Extras = null" );
			}
			if( theme != 0 )
			{
				setTheme( theme );
				DebugLog.i( "Theme != 0" );
			}
			else
			{
				DebugLog.e( "theme = 0" );
			}
			
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

			int theme = 0;
			Bundle extras = getActivity().getIntent().getExtras();
			if( extras != null )
			{
				theme = extras.getInt( "theme" );
				DebugLog.i( "Extras != null" );
			}
			else
			{
				DebugLog.e( "Extras = null" );
			}
			if( theme != 0 )
			{
				getActivity().setTheme( theme );
				DebugLog.i( "Theme != 0" );
			}
			else
			{
				DebugLog.e( "theme = 0" );
			}
			
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

			int theme = 0;
			Bundle extras = getIntent().getExtras();
			if( extras != null )
			{
				theme = extras.getInt( "theme" );
				DebugLog.i( "Extras != null" );
			}
			else
			{
				DebugLog.e( "Extras = null" );
			}
			if( theme != 0 )
			{
				setTheme( theme );
				DebugLog.i( "Theme != 0" );
			}
			else
			{
				DebugLog.e( "theme = 0" );
			}
			
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

			int theme = 0;
			Bundle extras = getActivity().getIntent().getExtras();
			if( extras != null )
			{
				theme = extras.getInt( "theme" );
				DebugLog.i( "Extras != null" );
			}
			else
			{
				DebugLog.e( "Extras = null" );
			}
			if( theme != 0 )
			{
				getActivity().setTheme( theme );
				DebugLog.i( "Theme != 0" );
			}
			else
			{
				DebugLog.e( "theme = 0" );
			}
			
			addPreferencesFromResource( R.xml.preference_misc );
		}
	}

	public static class AboutActivity extends Activity
	{
		@Override
		public void onCreate( Bundle state )
		{
			super.onCreate( state );

			int theme = 0;
			Bundle extras = getIntent().getExtras();
			if( extras != null )
			{
				theme = extras.getInt( "theme" );
				DebugLog.i( "Extras != null" );
			}
			else
			{
				DebugLog.e( "Extras = null" );
			}
			if( theme != 0 )
			{
				setTheme( theme );
				DebugLog.i( "Theme != 0" );
			}
			else
			{
				DebugLog.e( "theme = 0" );
			}
			
			WebView view = new WebView( this );
			view.getSettings().setJavaScriptEnabled( true );
			view.loadUrl( "file:///android_asset/about.html" );
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
			int theme = 0;
			Bundle extras = getActivity().getIntent().getExtras();
			if( extras != null )
			{
				theme = extras.getInt( "theme" );
				DebugLog.i( "Extras != null" );
			}
			else
			{
				DebugLog.e( "Extras = null" );
			}
			if( theme != 0 )
			{
				getActivity().setTheme( theme );
				DebugLog.i( "Theme != 0" );
			}
			else
			{
				DebugLog.e( "theme = 0" );
			}
			
			WebView view = ( WebView )super.onCreateView( inflater, container, savedInstanceState );
			view.setLayerType( View.LAYER_TYPE_SOFTWARE, null );
			view.getSettings().setJavaScriptEnabled( true );
			view.loadUrl( "file:///android_asset/about.html" );
			view.setBackgroundColor( Color.TRANSPARENT );
			return view;
		}
	}
}
