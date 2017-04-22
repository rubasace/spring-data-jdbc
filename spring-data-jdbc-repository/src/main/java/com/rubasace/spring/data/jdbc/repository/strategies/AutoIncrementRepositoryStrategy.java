package com.rubasace.spring.data.jdbc.repository.strategies;

import com.rubasace.spring.data.jdbc.model.JdbcEntityInformation;
import com.rubasace.spring.data.jdbc.util.GeneratedValueUtil;
import com.rubasace.spring.data.jdbc.util.ReflectionMethodsUtils;
import com.sun.xml.internal.bind.v2.model.core.ID;
import org.springframework.data.repository.core.EntityInformation;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class AutoIncrementRepositoryStrategy extends AbstractRepositoryStrategy {
    private final Method idSetter;
    private final Method getNumberValueMethod;

    public AutoIncrementRepositoryStrategy(final EntityInformation entityInformation) {
        super(entityInformation);
        idSetter = getIdSetter();
        getNumberValueMethod = GeneratedValueUtil.getNumberConversionMethod(entityInformation.getIdType());
    }

    // TODO implement custom EntityInformation for avoiding this hack
    private Method getIdSetter() {
        try {
            JdbcEntityInformation info = ((JdbcEntityInformation) getEntityInformation());
            Field idField = (Field) info.getIdFields().get(0);
            Class<?> entityClass = info.getJavaType();
            return ReflectionMethodsUtils.findSetterMethod(idField, entityClass);
        } catch (SecurityException | NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public <T> T postInsert(T entity, Number generatedId) {
        try {
            ID id = (ID) getNumberValueMethod.invoke(generatedId);
            idSetter.invoke(entity, id);
        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
        return entity;
    }
}
