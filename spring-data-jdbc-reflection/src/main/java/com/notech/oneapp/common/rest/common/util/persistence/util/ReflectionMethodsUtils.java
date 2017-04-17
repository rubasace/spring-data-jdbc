package com.notech.oneapp.common.rest.common.util.persistence.util;

import org.springframework.data.repository.query.ParametersParameterAccessor;
import org.springframework.data.repository.query.QueryMethod;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class ReflectionMethodsUtils {

    private static final String SETTER_START = "set";
    private static final String GETTER_START = "get";
    private static final String GETTER_START_BOOLEAN = "is";

    public static ParametersParameterAccessor generateParameters(QueryMethod method) {
        return new ParametersParameterAccessor(method.getParameters(),
                                               new Object[method.getParameters().getNumberOfParameters()]);
    }

    public static Method findSetterMethod(Field field, Class<?> clazz) throws NoSuchMethodException, SecurityException {
        return clazz.getMethod(calculateSetterName(field.getName()), field.getType());
    }

    public static String calculateSetterName(String fieldName) {
        return SETTER_START + StringUtils.firstToUpper(fieldName);
    }

    // TODO do it smoother
    public static Method findGetterMethod(Field field, Class<?> clazz) {
        return findGetterMethod(field.getName(), clazz);
    }

    public static Method findGetterMethod(String fieldName, Class<?> clazz) {
        try {
            return clazz.getMethod(calculateGetterName(fieldName));
        } catch (NoSuchMethodException e) {
            return findBooleanGetterMethod(fieldName, clazz);
        }
    }

    public static String calculateGetterName(String fieldName) {
        return GETTER_START + StringUtils.firstToUpper(fieldName);
    }

    private static Method findBooleanGetterMethod(String fieldName, Class<?> clazz) {
        try {
            return clazz.getMethod(calculateBooleanGetterName(fieldName));
        } catch (NoSuchMethodException | SecurityException e) {
            throw new RuntimeException(e);
        }
    }

    public static String calculateBooleanGetterName(String fieldName) {
        return GETTER_START_BOOLEAN + StringUtils.firstToUpper(fieldName);
    }

    public static Object executeMethod(Method method, Object target, Object... arguments) {
        try {
            return method.invoke(target, arguments);
        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }
}
