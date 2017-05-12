package com.rubasace.spring.data.repository.strategy.crud;

import com.rubasace.spring.data.repository.model.JdbcEntityInformation;
import com.rubasace.spring.data.repository.util.GeneratedValueUtil;
import com.rubasace.spring.data.repository.util.ReflectionMethodsUtils;
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
        JdbcEntityInformation info = ((JdbcEntityInformation) getEntityInformation());
        Field idField = (Field) info.getIdFields().get(0);
        Class<?> entityClass = info.getJavaType();
        return ReflectionMethodsUtils.findSetterMethod(idField, entityClass);
    }

    @Override
    public <T> T postInsert(T entity, Number generatedId) {
        try {
            Number id = (Number) getNumberValueMethod.invoke(generatedId);
            idSetter.invoke(entity, id);
        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
        return entity;
    }
}
