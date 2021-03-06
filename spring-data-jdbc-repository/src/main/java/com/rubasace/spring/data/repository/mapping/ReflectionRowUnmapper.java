package com.rubasace.spring.data.repository.mapping;

import com.rubasace.spring.data.repository.RowUnmapper;
import com.rubasace.spring.data.repository.model.JdbcPersistable;
import com.rubasace.spring.data.repository.util.SQLJavaNamingUtils;

import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.LinkedHashMap;
import java.util.Map;

public class ReflectionRowUnmapper<T> implements RowUnmapper<T> {

    private static final String PERSISTABLE_IS_NEW_METHOD = "isNew";

    private Class<? extends T> entityClass;
    private Map<String, Method> methodsMap;

    // TODO revisar exception
    public ReflectionRowUnmapper(Class<T> objectClass) {
        super();
        this.entityClass = objectClass;
        createMethodsMap();
    }

    private void createMethodsMap() {
        methodsMap = new LinkedHashMap<String, Method>();
        Method method;
        try {
            for (PropertyDescriptor propertyDescriptor : Introspector.getBeanInfo(entityClass)
                                                                     .getPropertyDescriptors()) {
                if (propertyDescriptor.getWriteMethod() != null && (method = propertyDescriptor.getReadMethod()) != null) {
                    if (JdbcPersistable.class.isAssignableFrom(entityClass) && method.getName().equals(PERSISTABLE_IS_NEW_METHOD)) {
                        continue;
                    }
                    methodsMap.put(
                            SQLJavaNamingUtils.geColumnNameFromAttributeName(propertyDescriptor.getDisplayName()),
                            propertyDescriptor.getReadMethod());
                }
            }
        } catch (IntrospectionException e) {
            throw new RuntimeException(e);
        }
    }

    // TODO revisar excepciones, add logs, etc
    @Override
    public Map<String, Object> mapColumns(T entity) {
        try {
            Map<String, Object> rs = new LinkedHashMap<>(methodsMap.size());
            for (String columnName : methodsMap.keySet()) {
                rs.put(columnName, methodsMap.get(columnName).invoke(entity));
            }
            return rs;
        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }

    }
}
