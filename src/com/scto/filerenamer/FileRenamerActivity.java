/* * Copyright (C) 2011 The Android Open Source Project *
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at *
* http://www.apache.org/licenses/LICENSE-2.0 *
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License. */

package com.scto.filerenamer;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageInfo;

import android.os.Bundle;

import android.preference.PreferenceManager;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;

import android.util.Log;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TabWidget;
import android.widget.TextView;

import java.util.ArrayList;

public class FileRenamerActivity extends FragmentActivity implements SharedPreferences.OnSharedPreferenceChangeListener
{
 	private static final String TAG = FileRenamerActivity.class.getSimpleName();
	
	TabHost mTabHost;
	ViewPager mViewPager;
	TabsAdapter mTabsAdapter;

	public static final int MAX_ADAPTER_COUNT = 6;
	private static SharedPreferences sSettings;
	private static int mThemeId = -1;
 	
	public static final int[] TITLES = 
	{
		R.string.addNumber,
		R.string.addCustom,
		R.string.addDate,
		R.string.findAndReplace,
		R.string.removeChars
	};
		
	private int mTabCount;
	
	@Override
	protected void onCreate( Bundle savedInstanceState )
	{
		super.onCreate( savedInstanceState );
		
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
		
		setContentView( R.layout.fragment_tabs_pager );
		
		mTabHost = ( TabHost )findViewById( android.R.id.tabhost );
		mTabHost.setup();

		mViewPager = ( ViewPager )findViewById( R.id.pager );        
		mViewPager.setPageMargin( getResources().getInteger( R.integer.viewpager_margin_width ) );
        mViewPager.setPageMarginDrawable( R.drawable.viewpager_margin );
        mViewPager.setCurrentItem( 1 );
		
		mTabsAdapter = new TabsAdapter( this, mTabHost, mViewPager );
		mTabsAdapter.addTab( mTabHost.newTabSpec( getString( R.string.addNumber ) ).setIndicator( getString( R.string.addNumber ) ), AddNumbersFragment.class, null );
		mTabsAdapter.addTab( mTabHost.newTabSpec( getString( R.string.addCustom ) ).setIndicator( getString( R.string.addCustom ) ), AddCustomFragment.class, null );
		mTabsAdapter.addTab( mTabHost.newTabSpec( getString( R.string.addDate ) ).setIndicator( getString( R.string.addDate ) ), AddCharsFragment.class, null );
		mTabsAdapter.addTab( mTabHost.newTabSpec( getString( R.string.findAndReplace ) ).setIndicator( getString( R.string.findAndReplace ) ), AddDateFragment.class, null );
		mTabsAdapter.addTab( mTabHost.newTabSpec( getString( R.string.removeChars ) ).setIndicator( getString( R.string.removeChars ) ), RemoveCharsFragment.class, null );

		Bundle extras = getIntent().getExtras();
		if( extras != null )
		{
			this.setTitle( extras.getString( "dir" ) + " :: " + getString( R.string.app_name ) );
		}
		
		if( savedInstanceState != null )
		{
			mTabHost.setCurrentTabByTag( savedInstanceState.getString( "tab" ) );
		}
	}		

	@Override
	protected void onSaveInstanceState( Bundle outState )
	{
		super.onSaveInstanceState( outState );
		outState.putString( "tab", mTabHost.getCurrentTabTag() );
		outState.putInt( "theme", mThemeId );
	}

    @Override
    public boolean onCreateOptionsMenu( Menu menu )
	{
        MenuInflater inflater = getMenuInflater();
        inflater.inflate( R.menu.main_menu, menu );
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu( Menu menu )
	{
        return super.onPrepareOptionsMenu( menu );
    }

    @Override
    public boolean onOptionsItemSelected( MenuItem item )
	{
        switch( item.getItemId() )
		{
			case R.id.menu_settings:
			{
				Intent intent = new Intent( this, PreferencesActivity.class );
				intent.putExtra( "theme", mThemeId );
				startActivity( intent );
				return true;
			}
			case android.R.id.home:
			{
				Intent intent = new Intent( this, MainActivity.class );
				intent.addFlags( Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP );
				startActivity( intent );
				return true;
			}	
			default:
			{
				return super.onOptionsItemSelected( item );
			}
        }
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
	}

	public void updateTheme()
	{
		setTheme( mThemeId );
		this.recreate();
	}	
	
	public static class TabsAdapter extends FragmentPagerAdapter implements 
				TabHost.OnTabChangeListener,
				ViewPager.OnPageChangeListener
	{
		private final Context mContext;
		private final TabHost mTabHost;
		private final ViewPager mViewPager;
		private final ArrayList<TabInfo> mTabs = new ArrayList< TabInfo >();

		static final class TabInfo
		{
			private final String tag;
			private final Class< ? > clss;
			private final Bundle args;

			TabInfo( String _tag, Class< ? > _class, Bundle _args )
			{
				tag = _tag;
				clss = _class;
				args = _args;
			}
		}

		static class DummyTabFactory implements TabHost.TabContentFactory
		{
			private final Context mContext;

			public DummyTabFactory( Context context )
			{
				mContext = context;
			}

			@Override public View createTabContent( String tag )
			{
				View v = new View( mContext );
				v.setMinimumWidth( 0 );
				v.setMinimumHeight( 0 );
				return v;
			}
		}

		public TabsAdapter( FragmentActivity activity, TabHost tabHost, ViewPager pager )
		{
			super( activity.getSupportFragmentManager() );
			mContext = activity;
			mTabHost = tabHost;
			mViewPager = pager;
			mTabHost.setOnTabChangedListener( this );
			mViewPager.setAdapter( this );
			mViewPager.setOnPageChangeListener( this );
		}

		public void addTab( TabHost.TabSpec tabSpec, Class< ? > clss, Bundle args )
		{
			tabSpec.setContent( new DummyTabFactory( mContext ) );
			String tag = tabSpec.getTag();

			TabInfo info = new TabInfo( tag, clss, args );
			mTabs.add( info );
			mTabHost.addTab( tabSpec );
			notifyDataSetChanged();
		}

		/*
		@Override
		public Object instantiateItem( ViewGroup container, int position )
		{
			int type = mTabOrder[ position ];
			ListView view = mLists[ type ];

			if( view == null )
			{
				MainActivity activity = mActivity;
				LayoutInflater inflater = activity.getLayoutInflater();
				MainAdapter adapter;
				TextView header = null;

				switch( type )
				{
					case Utils.TYPE_ADD_NUMBER:
						adapter = mAddNumberAdapter = new FileAdapter( activity, Utils.TYPE_ADD_NUMBER, null );
						mAddNumberHeader = header = ( TextView )inflater.inflate( R.layout.main_row, null );
						break;
					case Utils.TYPE_ADD_CUSTOM:
						adapter = mAddCustomAdapter = new FileAdapter( activity, Utils.TYPE_ADD_CUSTOM, null );
						mAddCustomHeader = header = ( TextView )inflater.inflate( R.layout.main_row, null );
						break;
					case Utils.TYPE_ADD_CHAR:
						adapter = mAddCharAdapter = new FileAdapter( activity, Utils.TYPE_ADD_CHAR, null );
						mAddCharHeader = header = ( TextView )inflater.inflate( R.layout.main_row, null);
						break;
					case Utils.TYPE_ADD_DATE:
						adapter = mAddDateAdapter = new FileAdapter( activity, Utils.TYPE_ADD_DATE, null );
						mAddDateHeader = header = ( TextView )inflater.inflate( R.layout.main_row, null );
						break;
					case Utils.TYPE_ADD_TIME:
						adapter = mAddTimeAdapter = new FileAdapter( activity, Utils.TYPE_ADD_TIME, null );
						mAddTimeHeader = header = ( TextView )inflater.inflate( R.layout.main_row, null );
						break;
					case Utils.TYPE_REMOVE_CHAR:
						adapter = mRemoveCharAdapter = new FileAdapter( activity, Utils.TYPE_REMOVE_CHAR, null );
						mRemoveCharHeader = header = ( TextView )inflater.inflate( R.layout.main_row, null );
						break;
					default:
						throw new IllegalArgumentException( "Invalid utils type: " + type);
				}

				view = ( ListView )inflater.inflate( R.layout.listview, null );
				view.setOnCreateContextMenuListener( this );
				view.setOnItemClickListener( this );
				view.setTag( type );
				if( header != null )
				{
					header.setText( mHeaderText );
					header.setTag( type );
					view.addHeaderView( header );
				}
				view.setAdapter( adapter );
				enableFastScroll( view );
				adapter.setFilter( mFilter );

				mAdapters[ type ] = adapter;
				mLists[ type ] = view;
				mRequeryNeeded[ type ] = true;
			}

			requeryIfNeeded( type );
			container.addView( view );
			return view;
		}
		*/
		
		@Override
		public int getCount()
		{
			return mTabs.size();
		}

		@Override
		public CharSequence getPageTitle( int position )
		{
			return mContext.getResources().getText( TITLES[ position ] );
		}
		
		@Override
		public Fragment getItem( int position )
		{
			TabInfo info = mTabs.get( position );
			return Fragment.instantiate( mContext, info.clss.getName(), info.args );
		}

		@Override
		public void onTabChanged( String tabId )
		{
			int position = mTabHost.getCurrentTab();
			mViewPager.setCurrentItem( position );
		}

		@Override
		public void onPageScrolled( int position, float positionOffset, int positionOffsetPixels )
		{
			
		}

		@Override
		public void onPageSelected( int position )
		{
			TabWidget widget = mTabHost.getTabWidget();
			int oldFocusability = widget.getDescendantFocusability();
			widget.setDescendantFocusability( ViewGroup.FOCUS_BLOCK_DESCENDANTS );
			mTabHost.setCurrentTab( position );
			widget.setDescendantFocusability( oldFocusability );
		}

		@Override
		public void onPageScrollStateChanged( int state )
		{
			
		}
	}
}
