package com.scto.filerenamer;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

public class AboutDialog extends DialogFragment
{	
    public interface AboutDialogListener
	{
        void onFinishAboutDialog( boolean exit );
    }

    public AboutDialog()
	{
        // Empty constructor required for DialogFragment
    }

	@Override
	public Dialog onCreateDialog( Bundle savedInstanceState )
	{
        return new AlertDialog.Builder( getActivity() )
			.setIcon( R.drawable.ic_menu_about )
			.setTitle( R.string.dialog_about_title )
			.setMessage( R.string.dialog_about_message )
			.setPositiveButton(R.string.dialog_about_positive_button,
			new DialogInterface.OnClickListener()
			{
				public void onClick( DialogInterface dialog, int whichButton )
				{
					//( ( FragmentAlertDialog )getActivity() ).doPositiveClick();
					AboutDialogListener activity = ( AboutDialogListener ) getActivity();
					activity.onFinishAboutDialog( true );
					dismiss();

				}
			})
			.create();
	}
}
