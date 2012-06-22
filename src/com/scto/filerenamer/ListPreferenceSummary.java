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

import android.content.*;
import android.preference.*;
import android.util.*;
 
/**
 * Overrides ListPreference to show the selected value as the summary.
 *
 * (ListPreference should supposedly be able to do this itself if %s is in the
 * summary, but as far as I can tell that behavior is broken.)
 */
public class ListPreferenceSummary extends ListPreference
{
	public ListPreferenceSummary( Context context, AttributeSet attrs )
	{
		super( context, attrs );
	}

	@Override
	public CharSequence getSummary()
	{
		return getEntry();
	}

	@Override
	protected void onDialogClosed( boolean positiveResult )
	{
		super.onDialogClosed( positiveResult );
		notifyChanged();
	}
}
