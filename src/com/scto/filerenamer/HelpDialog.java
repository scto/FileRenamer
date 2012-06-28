package com.scto.filerenamer;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

public class HelpDialog extends DialogFragment
{	
    public interface HelpDialogListener
	{
        void onFinishHelpDialog( boolean exit );
    }

    public HelpDialog()
	{
        // Empty constructor required for DialogFragment
    }

	@Override
	public Dialog onCreateDialog( Bundle savedInstanceState )
	{
        return new AlertDialog.Builder( getActivity() )
			.setIcon( R.drawable.ic_menu_help )
			.setTitle( R.string.dialog_help_title )
			.setMessage( R.string.dialog_help_message )
			.setPositiveButton(R.string.dialog_help_positive_button,
			new DialogInterface.OnClickListener()
			{
				public void onClick( DialogInterface dialog, int whichButton )
				{
					//( ( FragmentAlertDialog )getActivity() ).doPositiveClick();
					HelpDialogListener activity = ( HelpDialogListener ) getActivity();
					activity.onFinishHelpDialog( true );
					dismiss();

				}
			})
			.setCancelable( false )
			.create();
	}
}
