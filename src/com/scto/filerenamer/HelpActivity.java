package com.scto.filerenamer;

import android.app.*;
import android.os.*;
import android.text.*;
import android.text.method.*;
import android.view.*;
import android.widget.*;
import android.content.*;

public class HelpActivity extends Activity
{
	protected void onCreate(Bundle paramBundle)
	{
		super.onCreate(paramBundle);
    	//setContentView(R.layout.about);
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.help);
		builder.setIcon(R.drawable.ic_menu_help);
		
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.help, null);

        TextView text = (TextView) layout.findViewById(R.id.text);
        text.setMovementMethod(LinkMovementMethod.getInstance());
		text.setText(Html.fromHtml("\t\tAddNumber: <a href=https://github.com/scto/FileRenamer/addnumber>\t\t\tAdd a number to file(s)</a><p>\t\tAddCustom: <a href=https://github.com/scto/FileRenamer/addcustom>\t\t\tAdd a custom string to file(s)</a></p><p>\t\tAddDate: <a href=https://github.com/scto/FileRenamer/adddate>\t\t\tAdd a date string to file(s)</a></p><p>\t\tRemoveChars: <a href=https://github.com/scto/FileRenamer/removechars>\t\t\tRemove chars to file(s)</a></p><p>\t\tFindAndReplace: <a href=https://github.com/scto/FileRenamer/findandreplace>\t\t\tFind and replace</a></p>"));        
		builder.setView(layout);
		builder.setPositiveButton(R.string.helpActivityOkButton, new DialogInterface.OnClickListener() 
		{
			public void onClick(DialogInterface dialog, int id)
			{
				// Close dialog and finish the application
				dialog.dismiss();
				finish();
			}
		});
		// Don't allow escape key
		builder.setCancelable(false);		
		AlertDialog alert = builder.show();
	}
}
