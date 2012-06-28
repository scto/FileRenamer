package com.scto.filerenamer;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

public class WhatsNewDialog extends DialogFragment
{	
    public interface WhatsNewDialogListener
	{
        void onFinishWhatsNewDialog( boolean exit );
    }

    public WhatsNewDialog()
	{
        // Empty constructor required for DialogFragment
    }

	@Override
	public Dialog onCreateDialog( Bundle savedInstanceState )
	{
        return new AlertDialog.Builder( getActivity() )
			.setIcon( R.drawable.ic_menu_whats_new )
			.setTitle( R.string.dialog_whats_new_title )
			.setMessage( R.string.dialog_whats_new_message )
			.setPositiveButton(R.string.dialog_whats_new_positive_button,
			new DialogInterface.OnClickListener()
			{
				public void onClick( DialogInterface dialog, int whichButton )
				{
					//( ( FragmentAlertDialog )getActivity() ).doPositiveClick();
					WhatsNewDialogListener activity = ( WhatsNewDialogListener ) getActivity();
					activity.onFinishWhatsNewDialog( true );
					dismiss();

				}
			})
			.setCancelable( false )
			.create();
	}
}
