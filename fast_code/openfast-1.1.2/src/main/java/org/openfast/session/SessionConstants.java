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
package org.openfast.session;


import org.openfast.error.ErrorCode;
import org.openfast.error.ErrorType;
import org.openfast.error.FastAlertSeverity;


public interface SessionConstants {
    ErrorType SESSION = new ErrorType("Session");

    // Session Control Protocol (SCP) Errors
    ErrorCode TEMPLATE_NOT_SUPPORTED = new ErrorCode(SESSION, 11, "TNOTSUPP", "Template not supported", FastAlertSeverity.ERROR);
    ErrorCode TEMPLATE_UNKNOWN = new ErrorCode(SESSION, 12, "TUNKNOWN", "Template unknown", FastAlertSeverity.ERROR);
    ErrorCode UNAUTHORIZED = new ErrorCode(SESSION, 13, "EAUTH", "Unauthorized", FastAlertSeverity.FATAL);
    ErrorCode PROTCOL_ERROR = new ErrorCode(SESSION, 14, "EPROTO", "Protocol Error", FastAlertSeverity.ERROR);
    ErrorCode CLOSE = new ErrorCode(SESSION, 15, "CLOSE", "Session Closed", FastAlertSeverity.INFO);
    ErrorCode UNDEFINED = new ErrorCode(SESSION, -1, "UNDEFINED", "Undefined Alert Code", FastAlertSeverity.ERROR);
    
    SessionProtocol SCP_1_0 = new SessionControlProtocol_1_0();
    SessionProtocol SCP_1_1 = new SessionControlProtocol_1_1();

	String VENDOR_ID = "http://openfast.org/OpenFAST/1.1";
}
