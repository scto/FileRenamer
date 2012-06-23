/*
 Copyright (C) 2011 The Android Open Source Project

 Licensed under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at

 http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
 */

package com.scto.filerenamer;

import android.content.Context;
import android.content.res.TypedArray;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;

public class SeekBarPreference extends DialogPreference implements SeekBar.OnSeekBarChangeListener
{
	private int mValue;
	private TextView mValueText;

	public SeekBarPreference( Context context, AttributeSet attrs )
	{
		super( context, attrs );
	}

	@Override
	public CharSequence getSummary()
	{
		return getSummary( mValue );
	}

	@Override
	protected Object onGetDefaultValue( TypedArray a, int index )
	{
		return a.getInt( index, 100 );
	}

	@Override
	protected void onSetInitialValue( boolean restoreValue, Object defaultValue )
	{
		mValue = restoreValue ? getPersistedInt( mValue ) : ( Integer )defaultValue;
    }

	private String getSummary( int value )
	{
		if( "shake_threshold".equals( getKey() ) )
		{
			return String.valueOf( value / 10.0f );
		}
		else
		{
			return String.format( "%d%% (%+.1fdB)", value, 20 * Math.log10( Math.pow( value / 100.0, 3 ) ) );
		}
	}

	@Override
	protected View onCreateDialogView()
	{
		View view = super.onCreateDialogView();

		mValueText = ( TextView )view.findViewById( R.id.value );
		mValueText.setText( getSummary( mValue ) );

		SeekBar seekBar = ( SeekBar )view.findViewById( R.id.time_bar );
		seekBar.setMax( 150 );
		seekBar.setProgress( mValue );
		seekBar.setOnSeekBarChangeListener( this );

		return view;
	}

	@Override
	protected void onDialogClosed( boolean positiveResult )
	{
		notifyChanged();
	}

	@Override
	public void onProgressChanged( SeekBar seekBar, int progress, boolean fromUser )
	{
		if( fromUser )
		{
			mValue = progress;
			mValueText.setText( getSummary( progress ) );
			persistInt( progress );
		}
	}

	@Override
	public void onStartTrackingTouch( SeekBar seekBar )
	{

	}

	@Override
	public void onStopTrackingTouch( SeekBar seekBar )
	{

	}
}
