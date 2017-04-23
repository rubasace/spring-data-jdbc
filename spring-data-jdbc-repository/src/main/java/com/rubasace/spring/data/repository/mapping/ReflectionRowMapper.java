package com.rubasace.spring.data.repository.mapping;

import com.rubasace.spring.data.repository.model.JdbcPersistable;
import com.rubasace.spring.data.repository.util.MethodsUtils;
import com.rubasace.spring.data.repository.util.SQLJavaNamingUtils;
import org.springframework.jdbc.core.RowMapper;

import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.Map;

//TODO implement Strategy and clean up classes
//TODO: revisar getReadMethod que falla con Boolean
public class ReflectionRowMapper<T> implements RowMapper<T> {

    private static final String PERSISTABLE_SET_NEW_METHOD = "setNew";
    private Class<? extends T> entityClass;
    private Map<String, Method> methodsMap;

    // TODO revisar exception
    public ReflectionRowMapper(Class<? extends T> objectClass) {
        super();
        this.entityClass = objectClass;
        createMethodsMap();
    }

    // TODO utilizar anotaciones y fields en lugar de esto
    private void createMethodsMap() {
        methodsMap = new LinkedHashMap<String, Method>();
        Method method;
        try {
            for (PropertyDescriptor propertyDescriptor : Introspector.getBeanInfo(entityClass)
                                                                     .getPropertyDescriptors()) {
                if ((method = propertyDescriptor.getWriteMethod()) != null
                        && propertyDescriptor.getReadMethod() != null) {
                    if (JdbcPersistable.class.isAssignableFrom(entityClass) && method.getName().equals(PERSISTABLE_SET_NEW_METHOD)) {
                        continue;
                    }
                    methodsMap.put(
                            SQLJavaNamingUtils.geColumnNameFromAttributeName(propertyDescriptor.getDisplayName()),
                            propertyDescriptor.getWriteMethod());
                }
            }
        } catch (IntrospectionException e) {
            throw new RuntimeException(e);
        }
    }

    // TODO revisar excepciones, add logs, etc
    @Override
    @SuppressWarnings("rawtypes")
    public T mapRow(ResultSet rs, int rowNum) throws SQLException {
        T entity;
        try {
            entity = entityClass.newInstance();
            Object o;
            for (String columnName : methodsMap.keySet()) {
                // TODO ejecutar el get correspondiente a la clase para evitar
                // problemas
                Class<?> type = methodsMap.get(columnName).getParameterTypes()[0];
                o = MethodsUtils.getResultSetGetMethod(type).invoke(rs, columnName);
                if (!rs.wasNull()) {
                    methodsMap.get(columnName).invoke(entity, o);
                }
            }
            if (entity instanceof JdbcPersistable) {
                ((JdbcPersistable) entity).setNew(false);
            }
            return entity;
        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException
                | InstantiationException e) {
            throw new SQLException(e);
        }

    }

}
