package com.rubasace.spring.data.repository.mapping;

import com.rubasace.spring.data.repository.RowUnmapper;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.LinkedHashMap;
import java.util.Map;

public class ReflectionRowUnmapper<T> implements RowUnmapper<T> {

    private final Map<String, Method> methodsMap;

    // TODO revisar exception
    public ReflectionRowUnmapper(Class<T> objectClass) {
        super();
        methodsMap = GettersMapper.createGettersMap(objectClass);
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
