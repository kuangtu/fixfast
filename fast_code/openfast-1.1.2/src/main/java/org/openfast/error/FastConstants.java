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

import org.openfast.QName;

public interface FastConstants {
    QName ANY_TYPE = new QName("any");
    FastAlertSeverity ERROR = FastAlertSeverity.ERROR;
    FastAlertSeverity WARN = FastAlertSeverity.WARN;
    FastAlertSeverity FATAL = FastAlertSeverity.FATAL;
    // Error Types
    ErrorType DYNAMIC = new ErrorType("Dynamic");
    ErrorType STATIC = new ErrorType("Static");
    ErrorType REPORTABLE = new ErrorType("Reportable");
    // Static Errors
    ErrorCode S1_INVALID_XML = new ErrorCode(STATIC, 1, "ERR S1", "Invalid XML", ERROR);
    ErrorCode S2_OPERATOR_TYPE_INCOMP = new ErrorCode(STATIC, 2, "ERR S2", "Incompatible operator and type", ERROR);
    ErrorCode S3_INITIAL_VALUE_INCOMP = new ErrorCode(STATIC, 3, "ERR S3", "Incompatible initial value", ERROR);
    ErrorCode S4_NO_INITIAL_VALUE_FOR_CONST = new ErrorCode(STATIC, 4, "ERR S4",
            "Fields with constant operators must have a default value defined.", ERROR);
    ErrorCode S5_NO_INITVAL_MNDTRY_DFALT = new ErrorCode(STATIC, 5, "ERR S5",
            "No initial value for mandatory field with default operator", ERROR);
    // Dynamic Errors
    ErrorCode D1_FIELD_APP_INCOMP = new ErrorCode(DYNAMIC, 1, "ERR D1", "Field cannot be converted to type of application field",
            ERROR);
    ErrorCode D2_INT_OUT_OF_RANGE = new ErrorCode(DYNAMIC, 2, "ERR D2",
            "The integer value is out of range for the specified integer type.", WARN);
    ErrorCode D3_CANT_ENCODE_VALUE = new ErrorCode(DYNAMIC, 3, "ERR D3", "The value cannot be encoded for the given operator.", ERROR);
    ErrorCode D4_INVALID_TYPE = new ErrorCode(DYNAMIC, 4, "ERR D4",
            "The previous value is not the same type as the type of the current field.", ERROR);
    ErrorCode D5_NO_DEFAULT_VALUE = new ErrorCode(DYNAMIC, 5, "ERR D5",
            "If no prior value is set and the field is not present, there must be a default value or the optional flag must be set.",
            ERROR);
    ErrorCode D6_MNDTRY_FIELD_NOT_PRESENT = new ErrorCode(DYNAMIC, 6, "ERR D6", "A mandatory field must have a value", ERROR);
    ErrorCode D7_SUBTRCTN_LEN_LONG = new ErrorCode(DYNAMIC, 7, "ERR D7", "The subtraction length is longer than the base value.",
            ERROR);
    ErrorCode D8_TEMPLATE_NOT_EXIST = new ErrorCode(DYNAMIC, 8, "ERR D8", "The referenced template does not exist.", ERROR);
    ErrorCode D9_TEMPLATE_NOT_REGISTERED = new ErrorCode(DYNAMIC, 9, "ERR D9", "The template has not been registered.", ERROR);
    // Reportable Errors
    ErrorCode R1_LARGE_DECIMAL = new ErrorCode(REPORTABLE, 1, "ERR R1", "Decimal exponent does not fit into range -63...63", WARN);
    ErrorCode R4_NUMERIC_VALUE_TOO_LARGE = new ErrorCode(REPORTABLE, 4, "ERR R4", "The value is too large.", WARN);
    ErrorCode R5_DECIMAL_CANT_CONVERT_TO_INT = new ErrorCode(REPORTABLE, 5, "ERR R5",
            "The decimal value cannot convert to an integer because of trailing decimal part.", WARN);
    ErrorCode R7_PMAP_OVERLONG = new ErrorCode(REPORTABLE, 7, "ERR R7", "The presence map is overlong.", WARN);
    ErrorCode R8_PMAP_TOO_MANY_BITS = new ErrorCode(REPORTABLE, 8, "ERR R8", "The presence map has too many bits.", WARN);
    ErrorCode R9_STRING_OVERLONG = new ErrorCode(REPORTABLE, 9, "ERR R9", "The string is overlong.", ERROR);
    // Errors not defined in the FAST specification
    ErrorCode GENERAL_ERROR = new ErrorCode(DYNAMIC, 100, "GENERAL", "An error has occurred.", ERROR);
    ErrorCode IMPOSSIBLE_EXCEPTION = new ErrorCode(DYNAMIC, 101, "IMPOSSIBLE", "This should never happen.", ERROR);
    ErrorCode IO_ERROR = new ErrorCode(DYNAMIC, 102, "IOERROR", "An IO error occurred.", FATAL);
    ErrorCode PARSE_ERROR = new ErrorCode(DYNAMIC, 103, "PARSEERR", "An exception occurred while parsing.", ERROR);
    ErrorCode END_OF_STREAM = new ErrorCode(DYNAMIC, 104, "ENDSTREAM", "There is not more data in the stream.", ERROR);
    String TEMPLATE_DEFINITION_1_1 = "http://www.fixprotocol.org/ns/fast/td/1.1";
    QName LENGTH_FIELD = new QName("length", TEMPLATE_DEFINITION_1_1);
    QName LENGTH_NAME_ATTR = new QName("name", TEMPLATE_DEFINITION_1_1);
    QName LENGTH_NS_ATTR = new QName("namespace", TEMPLATE_DEFINITION_1_1);
    QName LENGTH_ID_ATTR = new QName("id", TEMPLATE_DEFINITION_1_1);
    ErrorHandler BASIC_ERROR_HANDLER = new ErrorHandler() {
        public void error(ErrorCode code, String message) {
            if (REPORTABLE.equals(code.getType()))
                System.out.println("WARNING: " + message);
            else
                code.throwException(message);
        }

        public void error(ErrorCode code, String message, Throwable t) {
            if (REPORTABLE.equals(code.getType()))
                System.out.println(message);
            else
                throw new FastException(message, code, t);
            
        }};
}
