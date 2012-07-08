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

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Writer;
import java.util.HashMap;

public class Tools {

    static final String TAG = "Tools";

    private static Tools instance;

    HashMap<String, String> props;

    public static ShellCommand.SH sh = new ShellCommand().sh;
    public static ShellCommand.SH su = new ShellCommand().su;

    public Tools()
	{

    }

    public static Tools getInstance()
	{
        if( instance == null )
		{
            return new Tools();
        }
		else
		{
            return instance;
		}
    }

    private static String[] getMounts( final String path )
	{
        try
		{
            BufferedReader br = new BufferedReader( new FileReader( "/proc/mounts" ), 256 );
            String line = null;
            while( ( line = br.readLine() ) != null )
			{
                if( line.contains( path ) ) 
				{
                    return line.split( " " );
                }
            }
            br.close();
        }
		catch( FileNotFoundException e ) 
		{
            Log.d( TAG, "/proc/mounts does not exist" );
        }
		catch( IOException e )
		{
            Log.d( TAG, "Error reading /proc/mounts" );
        }
        return null;
    }

    public static boolean mountRo()
	{
        final String mounts[] = getMounts( "/system" );
        if( mounts != null && mounts.length >= 3 )
		{
            final String device = mounts[ 0 ];
            final String path = mounts[ 1 ];
            final String point = mounts[ 2 ];
            return new ShellCommand().su.runWaitFor( "mount -o ro,remount -t " + point + " " + device + " " + path ).success();
        }
        return false;
    }

    public static boolean mountRw()
	{
        final String mounts[] = getMounts( "/system" );
        if( mounts != null && mounts.length >= 3 )
		{
            final String device = mounts[ 0 ];
            final String path = mounts[ 1 ];
            final String point = mounts[ 2 ];
            return new ShellCommand().su.runWaitFor( "mount -o rw,remount -t " + point + " " + device + " " + path ).success();
        }
        return false;
    }

    public static void writeFile( String fileContents, File fileToWrite )
	{
        if( fileToWrite == null || !fileToWrite.exists() )
		{
            fileToWrite.getParentFile().mkdirs();
            try
			{
                fileToWrite.createNewFile();
            }
			catch( IOException e )
			{
                e.printStackTrace();
            }
        }

        Writer outWriter;
        try
		{
            outWriter = new BufferedWriter( new FileWriter( fileToWrite ) );
            outWriter.write( fileContents );
            outWriter.close();
        }
		catch( IOException e )
		{
            e.printStackTrace();
        }
    }
}
