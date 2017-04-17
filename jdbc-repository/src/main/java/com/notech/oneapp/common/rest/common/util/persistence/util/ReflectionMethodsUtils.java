package com.notech.oneapp.common.rest.common.util.persistence.util;

import org.springframework.data.repository.query.ParametersParameterAccessor;
import org.springframework.data.repository.query.QueryMethod;

import java.lang.reflect.Field;
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
        return clazz.getMethod(calculateSetterName(field), field.getType());
    }

    public static String calculateSetterName(Field field) {
        return SETTER_START + StringUtils.firstToUpper(field.getName());
    }

    //TODO do it smoother
    public static Method findGetterMethod(Field field, Class<?> clazz) throws SecurityException, NoSuchMethodException {
        try {
            return clazz.getMethod(calculateGetterName(field));
        } catch (NoSuchMethodException e) {
            return clazz.getMethod(calculateBooleanGetterName(field));
        }
    }

    public static String calculateGetterName(Field field) {
        return GETTER_START + StringUtils.firstToUpper(field.getName());
    }

    public static String calculateBooleanGetterName(Field field) {
        return GETTER_START_BOOLEAN + StringUtils.firstToUpper(field.getName());
    }
}
