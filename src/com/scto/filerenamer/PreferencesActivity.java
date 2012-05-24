package com.scto.filerenamer;

import android.content.*;
import android.os.*;
import android.preference.*;

public class PreferencesActivity extends PreferenceActivity
{
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.prefs);
	}	
}
