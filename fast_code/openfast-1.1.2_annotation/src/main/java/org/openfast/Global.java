/*
The contents of this file are subject to the Mozilla Public License
Version 1.1 (the "License"); you may not use this file except in
compliance with the License. You may obtain a copy of the License at
http://www.mozilla.org/MPL/

Software distributed under the License is distributed on an "AS IS"
basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
License for the specific language governing rights and limitations
under the License.

The Original Code is OpenFAST.

The Initial Developer of the Original Code is The LaSalle Technology
Group, LLC.  Portions created by The LaSalle Technology Group, LLC
are Copyright (C) The LaSalle Technology Group, LLC. All Rights Reserved.

Contributor(s): Jacob Northey <jacob@lasalletech.com>
                Craig Otis <cotis@lasalletech.com>
 */
package org.openfast;

import java.io.ByteArrayOutputStream;

import org.openfast.error.ErrorCode;
import org.openfast.error.ErrorHandler;

public final class Global {
    private static ErrorHandler errorHandler = ErrorHandler.DEFAULT;
    private static int currentImplicitId = (int) (System.currentTimeMillis() % 10000);
    private static final ThreadLocal<ByteArrayOutputStream> buffers = new ThreadLocal<ByteArrayOutputStream>();

    public static void setErrorHandler(ErrorHandler handler) {
        if (handler == null) {
            throw new NullPointerException();
        }
        Global.errorHandler = handler;
    }

    public static void handleError(ErrorCode error, String message) {
        errorHandler.error(error, message);
    }

    public static void handleError(ErrorCode error, String message, Throwable source) {
        errorHandler.error(error, message, source);
    }

    public static QName createImplicitName(QName name) {
        return new QName(name + "@" + currentImplicitId++, name.getNamespace());
    }

    private Global() {}

    public static ByteArrayOutputStream getBuffer() {
        ByteArrayOutputStream buffer = buffers.get();
        if(buffer == null) {
            buffer = new ByteArrayOutputStream();
            buffers.set(buffer);
            // No reset after creation necessary
            return buffer;
        }
                
        buffer.reset();
        return buffer;
    }
    
    
}
