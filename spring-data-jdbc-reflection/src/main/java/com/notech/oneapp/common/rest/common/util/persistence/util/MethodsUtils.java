package com.notech.oneapp.common.rest.common.util.persistence.util;

import org.springframework.util.ClassUtils;

import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.Array;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.ResultSet;
import java.sql.RowId;
import java.sql.SQLXML;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


//TODO move to configurable bean as ResultSetConverter
public class MethodsUtils {

    private static final Map<Class<?>, Method> resultSetGetMethods;

    private static final String SETTER_START = "set";
    private static final String GETTER_START = "get";

    private static final Class<?> RESULTSET_CLASS = ResultSet.class;

    private static final String REPRESENTATION_DEFAULT = "Object";
    private static final String REPRESENTATION_STRING = "String";
    private static final String REPRESENTATION_NSTRING = "NString";
    private static final String REPRESENTATION_BOOLEAN = "Boolean";
    private static final String REPRESENTATION_BYTE = "Byte";
    private static final String REPRESENTATION_SHORT = "Short";
    private static final String REPRESENTATION_INTEGER = "Int";
    private static final String REPRESENTATION_LONG = "Long";
    private static final String REPRESENTATION_FLOAT = "Float";
    private static final String REPRESENTATION_DOUBLE = "Double";
    private static final String REPRESENTATION_BIGDECIMAL = "BigDecimal";
    private static final String REPRESENTATION_DATE = "Date";
    private static final String REPRESENTATION_TIME = "Time";
    private static final String REPRESENTATION_TIMESTAMP = "Timestamp";
    private static final String REPRESENTATION_BLOB = "Blob";
    private static final String REPRESENTATION_CLOB = "Clob";
    private static final String REPRESENTATION_ARRAY = "Array";
    private static final String REPRESENTATION_URL = "URL";
    private static final String REPRESENTATION_ROWID = "RowId";
    private static final String REPRESENTATION_NCLOB = "NClob";
    private static final String REPRESENTATION_SQLXML = "SQLXML";
    private static final String REPRESENTATION_BYTE_ARRAY = "Bytes";
    private static final String REPRESENTATION_ASCIISTREAM = "AsciiStream";
    private static final String REPRESENTATION_UNICODESTREAM = "UnicodeStream";
    private static final String REPRESENTATION_BINARYSTREAM = "BinaryStream";
    private static final String REPRESENTATION_CHARACTERSTREAM = "CharacterStream";

    //GETTERS
    // Standard
    private static final Method GET_DEFAULT;
    private static final Method GET_STRING;
    private static final Method GET_NSTRING;
    private static final Method GET_BOOLEAN;
    private static final Method GET_BYTE;
    private static final Method GET_SHORT;
    private static final Method GET_INTEGER;
    private static final Method GET_LONG;
    private static final Method GET_FLOAT;
    private static final Method GET_DOUBLE;
    private static final Method GET_BIGDECIMAL;
    private static final Method GET_DATE;
    private static final Method GET_TIME;
    private static final Method GET_TIMESTAMP;
    private static final Method GET_BLOB;
    private static final Method GET_CLOB;
    private static final Method GET_ARRAY;
    private static final Method GET_URL;
    private static final Method GET_ROWID;
    private static final Method GET_NCLOB;
    private static final Method GET_SQLXML;
    private static final Method GET_BYTE_ARRAY;

    // Special cases
    private static final Method GET_ASCIISTREAM;
    private static final Method GET_UNICODESTREAM;
    private static final Method GET_BINARYSTREAM;
    private static final Method GET_CHARACTERSTREAM;

    //	//	SETTERS
    //	// Standard
    //	private static final Method SET_STRING;
    //	private static final Method SET_NSTRING;
    //	private static final Method SET_BOOLEAN;
    //	private static final Method SET_BYTE;
    //	private static final Method SET_SHORT;
    //	private static final Method SET_INTEGER;
    //	private static final Method SET_LONG;
    //	private static final Method SET_FLOAT;
    //	private static final Method SET_DOUBLE;
    //	private static final Method SET_BIGDECIMAL;
    //	private static final Method SET_DATE;
    //	private static final Method SET_TIME;
    //	private static final Method SET_TIMESTAMP;
    //	private static final Method SET_BLOB;
    //	private static final Method SET_CLOB;
    //	private static final Method SET_ARRAY;
    //	private static final Method SET_URL;
    //	private static final Method SET_ROWID;
    //	private static final Method SET_NCLOB;
    //	private static final Method SET_SQLXML;
    //	private static final Method SET_BYTE_ARRAY;
    //
    //	// Special cases
    //	private static final Method SET_ASCIISTREAM;
    //	private static final Method SET_UNICODESTREAM;
    //	private static final Method SET_BINARYSTREAM;
    //	private static final Method SET_CHARACTERSTREAM;

    static {
        try {
            //Standard getters by type


            GET_DEFAULT = generateResultSetGetter(REPRESENTATION_DEFAULT);
            GET_STRING = generateResultSetGetter(REPRESENTATION_STRING);
            GET_NSTRING = generateResultSetGetter(REPRESENTATION_NSTRING);
            GET_INTEGER = generateResultSetGetter(REPRESENTATION_INTEGER);
            GET_LONG = generateResultSetGetter(REPRESENTATION_LONG);
            GET_SHORT = generateResultSetGetter(REPRESENTATION_SHORT);
            GET_FLOAT = generateResultSetGetter(REPRESENTATION_FLOAT);
            GET_DOUBLE = generateResultSetGetter(REPRESENTATION_DOUBLE);
            GET_BIGDECIMAL = generateResultSetGetter(REPRESENTATION_BIGDECIMAL);
            GET_BOOLEAN = generateResultSetGetter(REPRESENTATION_BOOLEAN);
            GET_BYTE = generateResultSetGetter(REPRESENTATION_BYTE);
            GET_DATE = generateResultSetGetter(REPRESENTATION_DATE);
            GET_TIME = generateResultSetGetter(REPRESENTATION_TIME);
            GET_TIMESTAMP = generateResultSetGetter(REPRESENTATION_TIMESTAMP);
            GET_BLOB = generateResultSetGetter(REPRESENTATION_BLOB);
            GET_CLOB = generateResultSetGetter(REPRESENTATION_CLOB);
            GET_NCLOB = generateResultSetGetter(REPRESENTATION_NCLOB);
            GET_ARRAY = generateResultSetGetter(REPRESENTATION_ARRAY);
            GET_URL = generateResultSetGetter(REPRESENTATION_URL);
            GET_ROWID = generateResultSetGetter(REPRESENTATION_ROWID);
            GET_SQLXML = generateResultSetGetter(REPRESENTATION_SQLXML);

            //Special getters
            GET_BYTE_ARRAY = generateResultSetGetter(REPRESENTATION_BYTE_ARRAY);
            GET_ASCIISTREAM = generateResultSetGetter(REPRESENTATION_ASCIISTREAM);
            GET_UNICODESTREAM = generateResultSetGetter(REPRESENTATION_UNICODESTREAM);
            GET_BINARYSTREAM = generateResultSetGetter(REPRESENTATION_BINARYSTREAM);
            GET_CHARACTERSTREAM = generateResultSetGetter(REPRESENTATION_CHARACTERSTREAM);

            resultSetGetMethods = new HashMap<Class<?>, Method>();
            resultSetGetMethods.put(String.class, GET_NSTRING);
            resultSetGetMethods.put(Boolean.class, GET_BOOLEAN);
            resultSetGetMethods.put(Byte.class, GET_BYTE);
            resultSetGetMethods.put(Short.class, GET_SHORT);
            resultSetGetMethods.put(Integer.class, GET_INTEGER);
            resultSetGetMethods.put(Long.class, GET_LONG);
            resultSetGetMethods.put(Float.class, GET_FLOAT);
            resultSetGetMethods.put(Double.class, GET_DOUBLE);
            resultSetGetMethods.put(BigDecimal.class, GET_BIGDECIMAL);
            resultSetGetMethods.put(Date.class, GET_DATE);
            resultSetGetMethods.put(Time.class, GET_TIME);
            resultSetGetMethods.put(Timestamp.class, GET_TIMESTAMP);
            resultSetGetMethods.put(Blob.class, GET_BLOB);
            resultSetGetMethods.put(Clob.class, GET_NCLOB);
            resultSetGetMethods.put(Array.class, GET_ARRAY);
            resultSetGetMethods.put(URL.class, GET_URL);
            resultSetGetMethods.put(RowId.class, GET_ROWID);
            resultSetGetMethods.put(SQLXML.class, GET_SQLXML);


            //			//Setters
            //			SET_NSTRING = generateResultSetSetter(REPRESENTATION_STRING, String.class);
            //			SET_NSTRING = generateResultSetSetter(REPRESENTATION_NSTRING);
            //			SET_BOOLEAN = generateResultSetSetter(REPRESENTATION_BOOLEAN, Boolean.class);
            //			SET_BYTE = generateResultSetSetter(REPRESENTATION_BYTE, Byte.class);
            //			SET_SHORT = generateResultSetSetter(REPRESENTATION_SHORT, Short.class);
            //			SET_INTEGER = generateResultSetSetter(REPRESENTATION_INTEGER, Integer.class);
            //			SET_LONG = generateResultSetSetter(REPRESENTATION_LONG,Long.class);
            //			SET_FLOAT = generateResultSetSetter(REPRESENTATION_FLOAT, Float.class);
            //			SET_DOUBLE = generateResultSetSetter(REPRESENTATION_DOUBLE, Double.class);
            //			SET_BIGDECIMAL = generateResultSetSetter(REPRESENTATION_BIGDECIMAL, BigDecimal.class);
            //			SET_DATE = generateResultSetSetter(REPRESENTATION_DATE, Date.class);
            //			SET_TIME = generateResultSetSetter(REPRESENTATION_TIME, Time.class);
            //			SET_TIMESTAMP = generateResultSetSetter(REPRESENTATION_TIMESTAMP, Timestamp.class);
            //			SET_BLOB = generateResultSetSetter(REPRESENTATION_BLOB, Blob.class);
            //			SET_CLOB = generateResultSetSetter(REPRESENTATION_CLOB, Clob.class);
            //			SET_ARRAY = generateResultSetSetter(REPRESENTATION_ARRAY, Array.class);
            //			SET_URL = generateResultSetSetter(REPRESENTATION_URL, URL.class);
            //			SET_ROWID = generateResultSetSetter(REPRESENTATION_ROWID, RowId.class);
            //			SET_NCLOB = generateResultSetSetter(REPRESENTATION_NCLOB, NClob.class);
            //			SET_SQLXML = generateResultSetSetter(REPRESENTATION_SQLXML, SQLXML.class);
            //			SET_BYTE_ARRAY = generateResultSetSetter(REPRESENTATION_BYTE_ARRAY);
            //			SET_ASCIISTREAM = generateResultSetSetter(REPRESENTATION_ASCIISTREAM);
            //			SET_UNICODESTREAM = generateResultSetSetter(REPRESENTATION_UNICODESTREAM);
            //			SET_BINARYSTREAM = generateResultSetSetter(REPRESENTATION_BINARYSTREAM);
            //			SET_CHARACTERSTREAM = generateResultSetSetter(REPRESENTATION_CHARACTERSTREAM);

        } catch (NoSuchMethodException | SecurityException e) {
            throw new RuntimeException(e);
        }
    }

    public static Method getResultSetGetMethod(Class<?> targetClass) {
        targetClass = ClassUtils.resolvePrimitiveIfNecessary(targetClass);
        Method method = resultSetGetMethods.get(targetClass);
        if (method != null) {
            return method;
        }
        return GET_DEFAULT;
    }

    private static Method generateResultSetGetter(String attribute) throws NoSuchMethodException, SecurityException {
        return RESULTSET_CLASS.getMethod(generateGetterName(attribute), String.class);
    }

    private static String generateGetterName(String representation) {
        return GETTER_START + representation;
    }

    private static Method generateResultSetSetter(String attribute, Class<?> attributeClass) throws NoSuchMethodException, SecurityException {
        return RESULTSET_CLASS.getMethod(generateSetterName(attribute), attributeClass);
    }

    private static String generateSetterName(String representation) {
        return SETTER_START + representation;
    }

}
