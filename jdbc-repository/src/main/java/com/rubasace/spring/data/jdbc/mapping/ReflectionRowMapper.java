package com.rubasace.spring.data.jdbc.mapping;

import com.rubasace.spring.data.jdbc.repository.model.JdbcEntity;
import com.rubasace.spring.data.jdbc.repository.model.ProxyUtils;
import com.rubasace.spring.data.jdbc.util.MethodsUtils;
import com.rubasace.spring.data.jdbc.util.SQLJavaNamingUtils;
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

    private Class<? extends T> entityClass;
    private Map<String, Method> methodsMap;

    // TODO revisar exception
    public ReflectionRowMapper(Class<? extends T> objectClass) {
        super();
        this.entityClass = objectClass;
        createMethodsMap();
    }

    //TODO utilizar anotaciones y fields en lugar de esto
    private void createMethodsMap() {
        methodsMap = new LinkedHashMap<String, Method>();
        try {
            for (PropertyDescriptor propertyDescriptor : Introspector.getBeanInfo(entityClass)
                                                                     .getPropertyDescriptors()) {
                if (propertyDescriptor.getWriteMethod() != null && propertyDescriptor.getReadMethod() != null) {
                    methodsMap.put(SQLJavaNamingUtils.geColumnNameFromAttributeName(propertyDescriptor.getDisplayName()),
                                   propertyDescriptor.getWriteMethod());
                }
            }
        } catch (IntrospectionException e) {
            throw new RuntimeException(e);
        }
    }

    //TODO revisar excepciones, add logs, etc
    @Override
    public T mapRow(ResultSet rs, int rowNum) throws SQLException {
        T entity;
        try {
            entity = ProxyUtils.getProxiedEntity(entityClass.newInstance());
            Object o;
            for (String columnName : methodsMap.keySet()) {
                //TODO ejecutar el get correspondiente a la clase para evitar problemas
                Class<?> type = methodsMap.get(columnName).getParameterTypes()[0];
                o = MethodsUtils.getResultSetGetMethod(type).invoke(rs, columnName);
                if (!rs.wasNull()) {
                    methodsMap.get(columnName).invoke(entity, o);
                }
            }
            ((JdbcEntity) entity)._setNew(false);
            return entity;
        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | InstantiationException e) {
            throw new SQLException(e);
        }

    }

}
