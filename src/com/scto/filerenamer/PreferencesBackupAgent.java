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

import android.annotation.TargetApi;
import android.app.backup.BackupAgentHelper;
import android.app.backup.SharedPreferencesBackupHelper;

/**
 * Saves application preferences to the backup manager.
 */
@TargetApi(8)
public class PreferencesBackupAgent extends BackupAgentHelper
{
	private static final String BACKUP_KEY = "prefs";

	@Override
	public void onCreate()
	{
		// This is the preference name used by PreferenceManager.getDefaultSharedPreferences
		String prefs = getPackageName() + "_preferences";
		addHelper(BACKUP_KEY, new SharedPreferencesBackupHelper( this, prefs ) );
	}
}
