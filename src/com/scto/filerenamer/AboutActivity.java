package com.scto.filerenamer;

import android.*;
import android.app.*;
import android.content.*;
import android.content.res.*;
import android.os.*;
import android.text.*;
import android.text.method.*;
import android.view.*;
import android.widget.*;

public class AboutActivity extends Activity
{
	protected void onCreate(Bundle paramBundle)
	{
		super.onCreate(paramBundle);
    	//setContentView(R.layout.about);
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.about);
		builder.setIcon(R.drawable.ic_menu_about);
		
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.about, null);

        TextView text = (TextView) layout.findViewById(R.id.text);
        text.setMovementMethod(LinkMovementMethod.getInstance());
		
		Resources res = getResources();
		String abouMessage = String.format(res.getString(R.string.aboutMessage));
		CharSequence styledText = Html.fromHtml(abouMessage);
		
		text.setText(styledText);
		builder.setView(layout);
		builder.setPositiveButton(R.string.aboutActivityOkButton, new DialogInterface.OnClickListener() 
		{
			public void onClick(DialogInterface dialog, int id)
			{
				// nichts weiter tun; Dialog schlie�en
				dialog.dismiss();
				finish();
			}
		});
		builder.setCancelable(false);
		AlertDialog alert = builder.show();
	}
}
