package com.scto.filerenamer;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

public class ExitDialog extends DialogFragment
{	
    public interface ExitDialogListener
	{
        void onFinishExitDialog( boolean exit );
    }

    public ExitDialog()
	{
        // Empty constructor required for DialogFragment
    }

	@Override
	public Dialog onCreateDialog( Bundle savedInstanceState )
	{
        return new AlertDialog.Builder( getActivity() )
			.setIcon( R.drawable.ic_menu_exit )
			.setTitle( R.string.dialog_exit_title )
			.setMessage( R.string.dialog_exit_message )
			.setPositiveButton(R.string.dialog_exit_positive_button,
			new DialogInterface.OnClickListener()
			{
				public void onClick( DialogInterface dialog, int whichButton )
				{
					//( ( FragmentAlertDialog )getActivity() ).doPositiveClick();
					ExitDialogListener activity = (ExitDialogListener) getActivity();
					activity.onFinishExitDialog( true );
					dismiss();
					
				}
			})
			.setNegativeButton( R.string.dialog_exit_negative_button,
			new DialogInterface.OnClickListener()
			{
				public void onClick( DialogInterface dialog, int whichButton )
				{
					//( ( FragmentAlertDialog )getActivity() ).doNegativeClick();
					ExitDialogListener activity = (ExitDialogListener) getActivity();
					activity.onFinishExitDialog( false );
					dismiss();
				}
			})
			.setCancelable( false )
			.create();
	}
}
