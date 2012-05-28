package com.scto.filerenamer;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;
import android.support.v4.view.*;

public class ViewPagerActivity extends Activity
{
	ViewPager myPager;
	MyPagerAdapter adapter;
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.viewpager);
		myPager = (ViewPager) findViewById(R.id.fivePanelPager);
		adapter = new MyPagerAdapter(this, myPager);		
	}
		
	public class MyPagerAdapter extends PagerAdapter implements ViewPager.OnPageChangeListener
	{
		private final Context mContext;
		private final ViewPager mViewPager;
		
		public MyPagerAdapter(Activity activity, ViewPager pager)
		{
			mContext = activity;
			mViewPager = pager;
			mViewPager.setAdapter(this);
			mViewPager.setCurrentItem(2);
			mViewPager.setOnPageChangeListener(this);		
		}
		
		public int getCount()
		{
			return 5;
		}
		
		public Object instantiateItem(View collection, int position)
		{
			LayoutInflater inflater = (LayoutInflater) collection.getContext()
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

			int resId = 0;
			switch (position)
			{
				case 0:
				{
					resId = R.layout.farleft;
					break;
				}
				case 1:
					{
						resId = R.layout.left;
						break;
					}
				case 2:
					{
						resId = R.layout.middle;
						break;
					}
				case 3:
					{
						resId = R.layout.right;
						break;
					}
				case 4:
					{
						resId = R.layout.farright;
						break;
					}
			}
			View view = inflater.inflate(resId, null);
			((ViewPager) collection).addView(view, 0);
			return view;
		}

		@Override
		public void destroyItem(View arg0, int arg1, Object arg2)
		{
			((ViewPager) arg0).removeView((View) arg2);
		}

		private String[] titles = new String[]
		{
			" AddNumbers ",
			" AddCustom ",
			" AddDate ",
			" FindAndReplace ",
			" RemoveChars "
		};

		@Override
		public String getPageTitle(int position) {
			return titles[position];
		}
		
		@Override
		public void finishUpdate(View arg0)
		{
			// TODO Auto-generated method stub
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1)
		{
			return arg0 == ((View) arg1);
		}

		@Override
		public void restoreState(Parcelable arg0, ClassLoader arg1)
		{
			// TODO Auto-generated method stub
		}

		@Override
		public Parcelable saveState()
		{
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public void startUpdate(View arg0)
		{
			// TODO Auto-generated method stub
		}
		
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels)
		{
        }

        @Override
        public void onPageSelected(int position)
		{
			Context context = getApplicationContext();
			CharSequence text = titles[position];
			int duration = Toast.LENGTH_SHORT;

			Toast toast = Toast.makeText(context, text, duration);
			toast.show();
        }

        @Override
        public void onPageScrollStateChanged(int state)
		{
        }
	}
}
