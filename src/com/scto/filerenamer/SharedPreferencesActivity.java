package com.scto.filerenamer;

import android.app.*;
import android.content.*;
import android.os.*;
import android.view.*;
import android.view.View.*;
import android.widget.*;

public class SharedPreferencesActivity extends Activity implements OnClickListener
{
	EditText sharedData;
	TextView dataResults;
	public static String filename = "MySharedString";
	SharedPreferences someData;
	
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.sharedpreferences);
		setupVariables();
		someData = getSharedPreferences(filename, 0);
	}

	private void setupVariables()
	{
		// TODO Auto-generated method stub
		Button save = (Button) findViewById(R.id.bSave);
		Button load = (Button) findViewById(R.id.bLoad);
		sharedData = (EditText) findViewById(R.id.etSharedPrefs);
		dataResults = (TextView) findViewById(R.id.tvLoadSharedPrefs);
		save.setOnClickListener(this);
		load.setOnClickListener(this);
	}

	public void onClick(View v)
	{
		// TODO Auto-generated method stub
		switch(v.getId())
		{
			case R.id.bSave:
				String stringData = sharedData.getText().toString();
				SharedPreferences.Editor editor = someData.edit();
				editor.putString("sharedString", stringData);
				editor.commit();
				break;
		
			case R.id.bLoad:
				someData = getSharedPreferences(filename, 0);
				String dataReturned = someData.getString("sharedString", "Couldn't Load Data");
				dataResults.setText(dataReturned);
				break;
		}
	}
}
