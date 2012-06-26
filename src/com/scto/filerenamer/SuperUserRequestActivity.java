package com.scto.filerenamer;

import android.app.*;
import android.os.*;
import android.util.*;
import java.io.*;
import java.util.*;
import java.lang.Process;

public class SuperUserRequestActivity extends Activity
{
	public static Process suProcess = null;

	ExecuteThread executeThread = new ExecuteThread();

	int ExecuteResponse = 999;
	int ExecuteFinished = 1000;

	String processName = "";
	String processScript = "";
	
	DataOutputStream stdin = null;
	DataInputStream stdout = null;
	DataInputStream stderr = null;

	String scriptOutput = "";
	Boolean scriptRunning = false;
	Boolean stdoutFinished = false;
	Boolean stderrFinished = false;

	Thread stdoutThread = null;
	Thread stderrThread = null;
	Thread stdinThread = null;
		
    @Override
    protected void onCreate( Bundle savedInstanceState )
    {
        super.onCreate( savedInstanceState );
		if( canRunRootCommands() )
		{
        	if( execute() == true )
			{
				Log.d( "ROOT", "execute was true" );
			}
			else
			{
				Log.d( "ROOT", "execute was false" );
			}
		}
		else
		{
			Log.d( "ROOT", "Can't run Root commands" );
		}
		finish();
		
		/*
		Intent resultIntent = new Intent( null );
		resultIntent.putExtra();
		*/
    }
	
    public static boolean canRunRootCommands()
	{
        boolean retval = false;

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
					Log.d("ROOT", "Can't get root access or denied by user");
				}
				else if (true == currUid.contains("uid=0"))
				{
					retval = true;
					exitSu = true;
					Log.d("ROOT", "Root access granted");
				}
				else
				{
					retval = false;
					exitSu = true;
					Log.d("ROOT", "Root access rejected: " + currUid);
				}

				if (exitSu)
				{
					os.writeBytes("exit\n");
					os.flush();
				}
			}
        }
        catch (Exception e)
        {
			// Can't get root !
			// Probably broken pipe exception on trying to write to output
			// stream after su failed, meaning that the device is not rooted

			retval = false;
			Log.d("ROOT", "Root access rejected [" +
				  e.getClass().getName() + "] : " + e.getMessage());
        }

        return retval;
	}


	protected ArrayList< String > getCommandsToExecute()
	{
        // TODO Auto-generated method stub
        ArrayList< String > commands = new ArrayList< String >();
		//commands.add( "mount -o remount,rw rootfs /" );
		commands.add( "ls -R /data > /mnt/sdcard/my_data_files.txt" );
		//commands.add( "mount -o remount,ro rootfs /" );
        return commands;
    }

	public final boolean execute()
	{
        boolean retval = false;

        try
        {
			ArrayList< String > commands = getCommandsToExecute();
			if (null != commands && commands.size() > 0)
			{
				Process suProcess = Runtime.getRuntime().exec("su");

				DataOutputStream os = 
					new DataOutputStream(suProcess.getOutputStream());

				// Execute commands that require root access
				for (String currCommand : commands)
				{
					os.writeBytes(currCommand + "\n");
					os.flush();
				}

				os.writeBytes("exit\n");
				os.flush();

				try
				{
					int suProcessRetval = suProcess.waitFor();
					if (255 != suProcessRetval)
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
				catch (Exception ex)
				{
					//Log.e("Error executing root action", ex);
					Log.e("LOGEXCEPTION", "Error executing root action", ex);
				}
			}
        }
        catch (IOException ex)
        {
			Log.w("ROOT", "Can't get root access", ex);
        }
        catch (SecurityException ex)
        {
			Log.w("ROOT", "Can't get root access", ex);
        }
        catch (Exception ex)
        {
			Log.w("ROOT", "Error executing internal operation", ex);
        }

        return retval;
	}
	
	private Handler messageHandler = new Handler()
	{
		@Override
		public void handleMessage( Message msg )
		{
			if( msg.what == ExecuteResponse )
			{
				if( scriptOutput.length() > 500 )
				{
					scriptOutput = scriptOutput.substring( scriptOutput.length() - 500, scriptOutput.length() );
				}
			}

			if( msg.what == ExecuteFinished )
			{
			}

			super.handleMessage(msg);
		}

	};
	
	class ExecuteThread extends Thread
	{
		public void run()
		{
			super.setPriority( MIN_PRIORITY );
			Execute();
		}

		void Execute()
		{
			try
			{
				suProcess = Runtime.getRuntime().exec( processName );

				stdin = new DataOutputStream( suProcess.getOutputStream() );
				stdout = new DataInputStream( suProcess.getInputStream() );
				stderr = new DataInputStream( suProcess.getErrorStream() );				

				stdinThread = new Thread()
				{
					public void run()
					{
						super.setPriority( MIN_PRIORITY );

						while( scriptRunning )
						{
							try
							{
								super.sleep( 200 );
								messageHandler.sendMessage( Message.obtain( messageHandler, ExecuteResponse, "" ) );
							}
							catch( Exception e )
							{
								
							}
						}
					}
				};

				stdoutThread = new Thread()
				{
					public void run()
					{						
						super.setPriority( MIN_PRIORITY );
						try
						{
							String line;
							while( ( line = stdout.readLine() ) != null )
							{
								super.sleep( 10 );
								scriptOutput += line+"\n";
							}
							stdoutFinished = true;
						}
						catch( Exception e )
						{
							
						}
					}
				};
				
				stderrThread = new Thread()
				{
					public void run()
					{
						super.setPriority( MIN_PRIORITY );

						try
						{
							String line;
							while( ( line = stderr.readLine() ) != null )
							{
								super.sleep( 10 );
								scriptOutput += "stderr: " + line+"\n";
							}
							stderrFinished = true;

						}
						catch( Exception e )
						{
							
						}
					}
				};

				scriptRunning = true;

				stdoutThread.start();
				stderrThread.start();
				stdinThread.start();

				stdin.writeBytes( processScript + " \n" );
				stdin.writeBytes( "exit\n" );

				stdin.flush();

				suProcess.waitFor();

				while( !stdoutFinished || !stderrFinished )
				{
				}

				stderr.close();
				stdout.close();
				stdin.close();

				stdoutThread = null;
				stderrThread = null;
				stdinThread = null;

				messageHandler.sendMessage( Message.obtain( messageHandler, ExecuteResponse, "" ) );

				scriptRunning = false;

				suProcess.destroy();				

				messageHandler.sendMessage( Message.obtain( messageHandler, ExecuteFinished, "" ) );

			}
			catch( Exception e )
			{
				messageHandler.sendMessage( Message.obtain( messageHandler, ExecuteResponse, "Error while trying to execute.." ) );
			}
		}
	}
}

