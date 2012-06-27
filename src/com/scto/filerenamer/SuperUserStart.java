package com.scto.filerenamer;

import java.util.*;

public class SuperUserStart extends ExecuteAsRootBase
{
	ArrayList< String > commands = new ArrayList< String >();
	
	@Override
	protected ArrayList< String > getCommandsToExecute()
	{
        // TODO Auto-generated method stub
		if( canRunRootCommands() )
		{
			execute();
		}
		commands.add( "mount -o remount,rw rootfs /" );
		commands.add( "find . > /mnt/sdcard/root_file_lists.txt" );
		commands.add( "mount -o remount,ro rootfs /" );
		return commands;			
    }
}

