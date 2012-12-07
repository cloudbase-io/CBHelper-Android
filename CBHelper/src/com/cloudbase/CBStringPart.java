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

import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;

import org.apache.http.protocol.HTTP;

public final class CBStringPart extends CBBasePart {
    
    private final byte[] valueBytes;
    
    /**
     * @param name String - name of parameter (may not be <code>null</code>).
     * @param value String - value of parameter (may not be <code>null</code>).
     * @param charset String, if null is passed then default "ISO-8859-1" charset is used.
     * 
     * @throws IllegalArgumentException if either <code>value</code> 
     *         or <code>name</code> is <code>null</code>.
     * @throws RuntimeException if <code>charset</code> is unsupported by OS.
     */
    public CBStringPart(String name, String value, String charset) {
        if (name == null) {
            throw new IllegalArgumentException("Name may not be null");     //$NON-NLS-1$
        }
        if (value == null) {
            throw new IllegalArgumentException("Value may not be null");    //$NON-NLS-1$
        }
        
        final String partName = CBUrlEncodingHelper.encode(name, HTTP.DEFAULT_PROTOCOL_CHARSET);
        
        if (charset == null) {
            charset = HTTP.DEFAULT_CONTENT_CHARSET;
        }
        final String partCharset = charset;
        
        try {
            this.valueBytes = value.getBytes(partCharset);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
        
        headersProvider = new IHeadersProvider() {
            public String getContentDisposition() {
                return "Content-Disposition: form-data; name=\"" + partName + '"';                  //$NON-NLS-1$
            }
            public String getContentType() {
                return "Content-Type: " + HTTP.PLAIN_TEXT_TYPE + HTTP.CHARSET_PARAM + partCharset;  //$NON-NLS-1$
            }
            public String getContentTransferEncoding() {
                return "Content-Transfer-Encoding: 8bit";                                           //$NON-NLS-1$
            }
        };
    }

    /**
     * Default "ISO-8859-1" charset is used.
     * @param name String - name of parameter (may not be <code>null</code>).
     * @param value String - value of parameter (may not be <code>null</code>).
     * 
     * @throws IllegalArgumentException if either <code>value</code> 
     *         or <code>name</code> is <code>null</code>.
     */
    public CBStringPart(String name, String value) {
        this(name, value, null);
    }
    
    public long getContentLength(CBBoundary boundary) {
        return getHeader(boundary).length + valueBytes.length + CRLF.length;
    }

    public void writeTo(final OutputStream out, CBBoundary boundary) throws IOException {
        out.write(getHeader(boundary));
        out.write(valueBytes);
        out.write(CRLF);
    }
}
