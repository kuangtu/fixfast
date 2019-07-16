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
package org.openfast.error;

import java.util.HashMap;
import java.util.Map;
import org.openfast.Message;

public class ErrorCode {
    private static final Map ALERT_CODES = new HashMap();
    private final int code;
    private final String shortName;
    private final String description;
    private final FastAlertSeverity severity;
    private final ErrorType type;

    public ErrorCode(ErrorType type, int code, String shortName, String description, FastAlertSeverity severity) {
        ALERT_CODES.put(new Integer(code), this);
        this.type = type;
        this.code = code;
        this.shortName = shortName;
        this.description = description;
        this.severity = severity;
    }

    public void throwException(String message) {
        throw new FastException(message, this);
    }

    public int getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    public String getShortName() {
        return shortName;
    }

    public FastAlertSeverity getSeverity() {
        return severity;
    }

    public static ErrorCode getAlertCode(Message alertMsg) {
        return (ErrorCode) ALERT_CODES.get(new Integer(alertMsg.getInt(2)));
    }

    public ErrorType getType() {
        return type;
    }

    public String toString() {
        return shortName + ": " + description;
    }

    public boolean equals(Object obj) {
        if (obj == this)
            return true;
        if (obj == null || !(obj instanceof ErrorCode))
            return false;
        ErrorCode other = (ErrorCode) obj;
        return other.code == this.code && other.getType().equals(this.getType());
    }
}
