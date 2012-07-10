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

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import android.util.Log;

// http://muzikant-android.blogspot.ch/2011/02/how-to-get-root-access-and-execute.html
public abstract class ExecuteAsRootBase
{
	public static boolean canRunRootCommands()
	{
    	boolean retval = false;
    	Process suProcess;
    
    	try
    	{
			suProcess = Runtime.getRuntime().exec( "su" );
      
			DataOutputStream os = new DataOutputStream( suProcess.getOutputStream() );
      		DataInputStream osRes = new DataInputStream( suProcess.getInputStream() );
      
      		if( null != os && null != osRes )
      		{
        		// Getting the id of the current user to check if this is root
        		os.writeBytes( "id\n" );
        		os.flush();

        		String currUid = osRes.readLine();
        		boolean exitSu = false;
        		if( null == currUid )
        		{
          			retval = false;
          			exitSu = false;
          			Log.d( "ROOT", "Can't get root access or denied by user" );
        		}
        		else if( true == currUid.contains( "uid=0" ) )
        		{
          			retval = true;
          			exitSu = true;
          			Log.d( "ROOT", "Root access granted" );
        		}
        		else
        		{
          			retval = false;
          			exitSu = true;
          			Log.d( "ROOT", "Root access rejected: " + currUid );
        		}

        		if( exitSu )
        		{
          			os.writeBytes( "exit\n" );
          			os.flush();
        		}
      		}
    	}
    	catch( Exception e )
    	{
      		// Can't get root !
      		// Probably broken pipe exception on trying to write to output
      		// stream after su failed, meaning that the device is not rooted
      
      		retval = false;
      		Log.d( "ROOT", "Root access rejected [" +
            	e.getClass().getName() + "] : " + e.getMessage() );
    	}

    	return retval;
  	}
  	
  	public final boolean execute()
  	{
    	boolean retval = false;
    
    	try
    	{
      		ArrayList< String > commands = getCommandsToExecute();
      		if( null != commands && commands.size() > 0 )
      		{
        		Process suProcess = Runtime.getRuntime().exec( "su" );

        		DataOutputStream os = 
            		new DataOutputStream( suProcess.getOutputStream() );

        		// Execute commands that require root access
        		for( String currCommand : commands )
        		{
          			os.writeBytes( currCommand + "\n" );
          			os.flush();
        		}

        		os.writeBytes( "exit\n" );
        		os.flush();

        		try
        		{
          			int suProcessRetval = suProcess.waitFor();
          			if( 255 != suProcessRetval )
          			{
            			// Root access granted
            			retval = true;
          			}
          			else
          			{
            			// Root access denied
            			retval = false;
          			}
        		}
        		catch( Exception ex )
        		{
          			Log.e( "ROOT", "Error executing root action", ex );
        		}
      		}
    	}
    	catch( IOException ex )
    	{
      		Log.w( "ROOT", "Can't get root access", ex );
    	}
    	catch( SecurityException ex )
    	{
      		Log.w( "ROOT", "Can't get root access", ex );
    	}
    	catch( Exception ex )
    	{
      		Log.w( "ROOT", "Error executing internal operation", ex );
    	}
    
    	return retval;
  	}
  
  	protected abstract ArrayList< String > getCommandsToExecute();
}
