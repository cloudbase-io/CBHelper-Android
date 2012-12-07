/* Copyright (C) 2012 cloudbase.io
 
 This program is free software; you can redistribute it and/or modify it under
 the terms of the GNU General Public License, version 2, as published by
 the Free Software Foundation.
 
 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 for more details.
 
 You should have received a copy of the GNU General Public License
 along with this program; see the file COPYING.  If not, write to the Free
 Software Foundation, 59 Temple Place - Suite 330, Boston, MA
 02111-1307, USA.
 */
package com.cloudbase;

import java.util.Random;

import org.apache.http.util.EncodingUtils;

import android.text.TextUtils;

class CBBoundary {

    /* The pool of ASCII chars to be used for generating a multipart boundary. */
    private final static char[] MULTIPART_CHARS =
            "-_1234567890abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();   //$NON-NLS-1$

    private final String boundary;
    private final byte[] startingBoundary;
    private final byte[] closingBoundary;
    
    /* package */ CBBoundary(String boundary) {
        if (TextUtils.isEmpty(boundary)) {
            boundary = generateBoundary();
        }
        this.boundary = boundary;
        
        final String starting = "--" + boundary + CBMultipartEntity.CRLF;         //$NON-NLS-1$
        final String closing  = "--" + boundary + "--" + CBMultipartEntity.CRLF;  //$NON-NLS-1$
        
        startingBoundary = EncodingUtils.getAsciiBytes(starting);
        closingBoundary  = EncodingUtils.getAsciiBytes(closing);
    }
    
    /* package */ String getBoundary() {
        return boundary;
    }

    /* package */ byte[] getStartingBoundary() {
        return startingBoundary;
    }

    /* package */ byte[] getClosingBoundary() {
        return closingBoundary;
    }
    
    private static String generateBoundary() {
        // Boundary delimiters must not appear within the encapsulated material, 
        // and must be no longer than 70 characters, not counting the two
        // leading hyphens.
        Random rand = new Random();
        final int count = rand.nextInt(11) + 30; // a random size from 30 to 40
        StringBuilder buffer = new StringBuilder(count);
        for (int i = 0; i < count; i++) {
            buffer.append(MULTIPART_CHARS[rand.nextInt(MULTIPART_CHARS.length)]);
        }
        return buffer.toString();
    }
}

