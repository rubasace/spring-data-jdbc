package com.rubasace.spring.data.jdbc.util;

import org.springframework.util.ClassUtils;

import java.lang.reflect.Method;

public class GeneratedValueUtil {

    private static final String INT_VALUE = "intValue";
    private static final String LONG_VALUE = "longValue";
    private static final String FLOAT_VALUE = "floatValue";
    private static final String DOUBLE_VALUE = "doubleValue";

    public static Method getNumberConversionMethod(Class<?> objectClass) {
        try {
            return Number.class.getMethod(getNumberConversionMethodName(objectClass));
        } catch (NoSuchMethodException | SecurityException e) {
            throw new RuntimeException(e);
        }
    }

    private static String getNumberConversionMethodName(Class<?> objectClass) {
        objectClass = ClassUtils.resolvePrimitiveIfNecessary(objectClass);
        if (Integer.class.isAssignableFrom(objectClass)) {
            return INT_VALUE;
        }
        if (Long.class.isAssignableFrom(objectClass)) {
            return LONG_VALUE;
        }
        if (Float.class.isAssignableFrom(objectClass)) {
            return FLOAT_VALUE;
        }
        if (Double.class.isAssignableFrom(objectClass)) {
            return DOUBLE_VALUE;
        }
        throw new IllegalArgumentException(
                "Cannot get conversion number for class " + objectClass);
    }

}
