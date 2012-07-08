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

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.zip.GZIPInputStream;

import android.content.Context;

public final class MimeTypes
{
	private HashMap< String, String > extensionToMime;
  
	private void initExtensions( Context context )
	{
		extensionToMime = new HashMap< String, String >();
    	try
		{
			InputStream is = new GZIPInputStream( context.getResources().openRawResource( R.raw.mimes ) );
			ByteArrayOutputStream os = new ByteArrayOutputStream();
			byte[] buf = new byte[ 16384 ];
			while( true )
			{
				int r = is.read( buf );
				if( r <= 0 )
					break;
				os.write( buf, 0, r );
			}
			String[] lines = os.toString().split("\n");
			String mime = null;
			for( int i = 0; i < lines.length; i++ )
			{
    	    	String val = lines[ i ];
	        	if( val.length() == 0 )
					mime = null;
        		else if( mime == null )
					mime = val;
	        	else
					extensionToMime.put( val, mime );
			}
		}
		catch( Exception e )
		{
			throw new RuntimeException( "failed to open mime db", e );
    	}
	}
  
	public String getMimeByExtension( Context context, String extension )
	{
    	if( extensionToMime == null )
		{
      		initExtensions( context );
    	}
    	return extensionToMime.get( extension );
  	}
}
