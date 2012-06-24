package com.scto.filerenamer;

import java.util.ArrayList;
import com.scto.filerenamer.ExecuteAsRootBase;

public class CheckForRoot extends ExecuteAsRootBase
{
	private ArrayList< String > mListPaths = null;
	private int mListPathSize;
	
	private String mMountRootRW = "mount -o remount,rw rootfs /";
	private String mMountRootRO = "mount -o remount,r rootfs /";

	/*
	private void initPathList()
	{
		mListPaths.add( "/" );
		mListPaths.add( "/acct" );
		mListPaths.add( "/cache" );
		mListPaths.add( "/config" );
		mListPaths.add( "/d" );
		mListPaths.add( "/data" );
		mListPaths.add( "/dev" );
		mListPaths.add( "/etc" );
		mListPaths.add( "/mnt" );
		mListPaths.add( "/proc" );
		mListPaths.add( "/root" );
		mListPaths.add( "/sbin" );
		mListPaths.add( "/sys" );
		mListPaths.add( "/system" );
		mListPaths.add( "/vendor" );
	}	
	*/
	
	@Override
	protected ArrayList< String > getCommandsToExecute()
	{
		ArrayList< String > commands = null;
		if( canRunRootCommands() )
		{
			commands.add( mMountRootRW );
			commands.add( "ls -l /" );
			commands.add( mMountRootRO );
			return commands;
		}
		else
		{
			return null;
		}
	}
}

