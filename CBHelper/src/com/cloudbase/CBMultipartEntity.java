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
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.entity.AbstractHttpEntity;

public class CBMultipartEntity extends AbstractHttpEntity implements Cloneable {
	/* package */ static final String CRLF = "\r\n";    //$NON-NLS-1$
    
    private List<CBPart> parts = new ArrayList<CBPart>();
    
    private CBBoundary boundary;
    
    public CBMultipartEntity(String boundaryStr) {
        super();
        boundary = new CBBoundary(boundaryStr);
        setContentType("multipart/form-data; boundary=\"" + boundary.getBoundary() + '"');  //$NON-NLS-1$
    }
    
    public CBMultipartEntity() {
        this(null);
    }
    
    public void addPart(CBPart part) {
        parts.add(part);
    }
    
    public boolean isRepeatable() {
        return true;
    }

    public long getContentLength() {
        long result = 0;
        for (CBPart part : parts) {
            result += part.getContentLength(boundary);
        }
        result += boundary.getClosingBoundary().length;
        return result;
    }
    
    /**
     * Returns <code>null</code> since it's not designed to be used for server responses.
     */
    public InputStream getContent() throws IOException {
        return null;
    }
    
    public void writeTo(final OutputStream out) throws IOException {
        if (out == null) {
            throw new IllegalArgumentException("Output stream may not be null");    //$NON-NLS-1$
        }
        for (CBPart part : parts) {
            part.writeTo(out, boundary);
        }
        out.write(boundary.getClosingBoundary());
        out.flush();
    }

    /**
     * Tells that this entity is not streaming.
     *
     * @return <code>false</code>
     */
    public boolean isStreaming() {
        return false;
    }

    public Object clone() throws CloneNotSupportedException {
        throw new CloneNotSupportedException("MultipartEntity does not support cloning"); //$NON-NLS-1$ // TODO
    }
}