/*
 * Copyright (C) 2012 Thomas Schmid <tschmid35@gmail.com>
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

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
