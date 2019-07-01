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

public class FastAlertSeverity {
    public static final FastAlertSeverity FATAL = new FastAlertSeverity(1,
            "FATAL", "Fatal");
    public static final FastAlertSeverity ERROR = new FastAlertSeverity(2,
            "ERROR", "Error");
    public static final FastAlertSeverity WARN = new FastAlertSeverity(3,
            "WARN", "Warning");
    public static final FastAlertSeverity INFO = new FastAlertSeverity(4,
            "INFO", "Information");
    private int code;
    private String shortName;
    private String description;

    public FastAlertSeverity(int code, String shortName, String description) {
        this.code = code;
        this.shortName = shortName;
        this.description = description;
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
}
