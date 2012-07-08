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
import java.io.InputStream;

import android.util.Log;

public class ShellCommand
{
 	private static final String TAG = ShellCommand.class.getSimpleName();
    private Boolean can_su;

    public SH sh;
    public SH su;

    public ShellCommand()
	{
        sh = new SH( "sh" );
        su = new SH( "su" );
    }

    public boolean canSU()
	{
        return canSU( false );
    }

    public boolean canSU( boolean force_check )
	{
        if( can_su == null || force_check )
		{
            CommandResult r = su.runWaitFor( "id" );
            StringBuilder out = new StringBuilder();

            if( r.stdout != null )
			{
                out.append( r.stdout ).append( " ; " );
            }
			if( r.stderr != null )
			{
                out.append( r.stderr );
			}
            Log.v( TAG, "canSU() su[" + r.exit_value + "]: " + out );
            can_su = r.success();
        }
        return can_su;
    }

    public SH suOrSH()
	{
        return canSU() ? su : sh;
    }

    public class CommandResult
	{
        public final String stdout;
        public final String stderr;
        public final Integer exit_value;

        CommandResult( Integer exit_value_in, String stdout_in, String stderr_in )
        {
            exit_value = exit_value_in;
            stdout = stdout_in;
            stderr = stderr_in;
        }

        CommandResult( Integer exit_value_in )
		{
            this( exit_value_in, null, null );
        }

        public boolean success()
		{
            return exit_value != null && exit_value == 0;
        }
    }

    public class SH
	{
        private String SHELL = "sh";

        public SH( String SHELL_in )
		{
            SHELL = SHELL_in;
        }

        public Process run( String s )
		{
            Process process = null;
            try
			{
                process = Runtime.getRuntime().exec( SHELL );
                DataOutputStream toProcess = new DataOutputStream( process.getOutputStream() );
                toProcess.writeBytes( "exec " + s + "\n" );
                toProcess.flush();
            }
			catch( Exception e )
			{
                Log.e( TAG, "Exception while trying to run: '" + s + "' " + e.getMessage() );
                process = null;
            }
            return process;
        }

        private String getStreamLines( InputStream is )
		{
            String out = null;
            StringBuffer buffer = null;
            DataInputStream dis = new DataInputStream( is );

            try
			{
                if( dis.available() > 0 )
				{
                    buffer = new StringBuffer( dis.readLine() );
                    while( dis.available() > 0 )
					{
                        buffer.append( "\n" ).append( dis.readLine() );
                	}
				}
                dis.close();
            }
			catch( Exception ex )
			{
                Log.e( TAG, ex.getMessage() );
            }
            if( buffer != null )
			{
                out = buffer.toString();
            }
			return out;
        }

        public CommandResult runWaitFor( String s )
		{
            Process process = run( s );
            Integer exit_value = null;
            String stdout = null;
            String stderr = null;
            if( process != null )
			{
                try
				{
                    exit_value = process.waitFor();

                    stdout = getStreamLines( process.getInputStream() );
                    stderr = getStreamLines( process.getErrorStream() );

                }
				catch( InterruptedException e )
				{
                    Log.e( TAG, "runWaitFor " + e.toString() );
                }
				catch( NullPointerException e )
				{
                    Log.e( TAG, "runWaitFor " + e.toString() );
                }
            }
            return new CommandResult( exit_value, stdout, stderr );
        }
    }
}
