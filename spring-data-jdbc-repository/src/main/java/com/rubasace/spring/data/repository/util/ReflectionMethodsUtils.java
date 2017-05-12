package com.rubasace.spring.data.repository.util;

import org.springframework.data.repository.query.ParametersParameterAccessor;
import org.springframework.data.repository.query.QueryMethod;
import org.springframework.util.ClassUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class ReflectionMethodsUtils {

    private static final String SETTER_START = "set";
    private static final String GETTER_START = "get";
    private static final String GETTER_START_BOOLEAN = "is";

    private ReflectionMethodsUtils() {
    }

    //TODO probably move elsewhere
    public static ParametersParameterAccessor generateParameters(QueryMethod method) {
        return new ParametersParameterAccessor(method.getParameters(),
                                               new Object[method.getParameters().getNumberOfParameters()]);
    }

    public static Method findSetterMethod(Field field, Class<?> clazz) {
        try {
            return clazz.getMethod(calculateSetterName(field.getName()), field.getType());
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    private static String calculateSetterName(String fieldName) {
        return SETTER_START + StringUtils.firstToUpper(fieldName);
    }

    // TODO do it smoother
    public static Method findGetterMethod(Field field, Class<?> clazz) {
        return findGetterMethod(field.getName(), clazz, ClassUtils.isAssignable(Boolean.class, field.getType()));
    }

    private static Method findGetterMethod(String fieldName, Class<?> clazz, boolean shouldTryBoolean) {
        try {
            return clazz.getMethod(calculateGetterName(fieldName));
        } catch (NoSuchMethodException e) {
            if (shouldTryBoolean) {
                return findBooleanGetterMethod(fieldName, clazz);
            } else {
                throw new RuntimeException(e);
            }
        }
    }

    private static String calculateGetterName(String fieldName) {
        return GETTER_START + StringUtils.firstToUpper(fieldName);
    }

    private static Method findBooleanGetterMethod(String fieldName, Class<?> clazz) {
        try {
            return clazz.getMethod(calculateBooleanGetterName(fieldName));
        } catch (NoSuchMethodException | SecurityException e) {
            throw new RuntimeException(e);
        }
    }

    private static String calculateBooleanGetterName(String fieldName) {
        return GETTER_START_BOOLEAN + StringUtils.firstToUpper(fieldName);
    }
}
