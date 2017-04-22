package com.rubasace.spring.data.jdbc.repository.model;

import com.rubasace.spring.data.jdbc.EntityType;
import com.rubasace.spring.data.jdbc.mapping.ReflectionRowMapper;
import com.rubasace.spring.data.jdbc.mapping.ReflectionRowUnmapper;
import com.rubasace.spring.data.jdbc.util.EntityUtils;
import com.rubasace.spring.data.jdbc.util.GeneratedValueUtil;
import com.rubasace.spring.data.jdbc.util.ReflectionMethodsUtils;
import com.rubasace.spring.data.jdbc.BaseJdbcRepository;
import com.rubasace.spring.data.jdbc.UnsupportedRowUnmapper;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class BaseReflectionJdbcRepository<T, ID extends Serializable> extends BaseJdbcRepository<T, ID> {

    RepositoryStrategy repositoryStrategy;
    IdToArrayStrategy idToArrayStrategy;

    public BaseReflectionJdbcRepository(Class<T> entityClass) {
        super(EntityUtils.getEntityInformation(entityClass), new ReflectionRowMapper<>(entityClass),
              new UnsupportedRowUnmapper<>(), EntityUtils.getTableDescription(entityClass));
        repositoryStrategy = chooseStrategy(entityClass);
        if (!EntityUtils.getEntityType(entityInfo).equals(EntityType.READ_ONLY)) {
            this.rowUnmapper = new ReflectionRowUnmapper<>(entityClass);
        }
        if (((JdbcEntityInformation) entityInfo).isCompoundKey()) {
            idToArrayStrategy = new CompoundIdToArray();
        } else {
            idToArrayStrategy = new SingleIdToArray();
        }
    }

    private RepositoryStrategy chooseStrategy(Class<T> entityClass) {
        switch (EntityUtils.getEntityType(entityInfo)) {
            case AUTO_INCREMENTAL:
                return new AutoincrementRepositoryStrategy(entityClass);
            case MANUALLY_ASSIGNED:
                return new ManuallyAssignedRepositoryStrategy();
            case READ_ONLY:
                return new ReadOnlyRepositoryStrategy();
            default:
                throw new IllegalArgumentException("Don't know what strategy to instantiate");
        }

    }

    @Override
    public <S extends T> S save(S entity) {
        return super.save(entity);
    }

    @Override
    protected <S extends T> S postUpdate(S entity) {
        return repositoryStrategy.postUpdate(entity);
    }

    @Override
    protected <S extends T> S postInsert(S entity, Number generatedId) {
        return repositoryStrategy.postInsert(entity, generatedId);
    }


    // Private classes for implementing strategy pattern

    @Override
    protected Object[] getIdArray(Object id) {
        return idToArrayStrategy.getIdArray(id);

    }

    private interface IdToArrayStrategy {
        Object[] getIdArray(Object id);
    }

    private abstract class RepositoryStrategy {

        private RepositoryStrategy() {
            super();
        }

        private RepositoryStrategy(Class<?> entityClass) {
        }

        <S extends T> S postInsert(S entity, Number generatedId) {
            return entity;
        }

        <S extends T> S postUpdate(S entity) {
            return entity;
        }
    }

    private class AutoincrementRepositoryStrategy extends RepositoryStrategy {

        private final Method idSetter;
        private final Method getNumberValueMethod;

        private AutoincrementRepositoryStrategy(Class<?> entityClass) {
            super(entityClass);
            getNumberValueMethod = GeneratedValueUtil
                    .getNumberConversionMethod(BaseReflectionJdbcRepository.this.entityInfo.getIdType());
            idSetter = getIdSetter();
        }

        // TODO implement custom EntityInformation for avoiding this hack
        private Method getIdSetter() {
            try {
                JdbcEntityInformation info = ((JdbcEntityInformation) BaseReflectionJdbcRepository.this.entityInfo);
                Field idField = (Field) info.getIdFields().get(0);
                Class<?> entityClass = info.getJavaType();
                return ReflectionMethodsUtils.findSetterMethod(idField, entityClass);
            } catch (SecurityException | NoSuchMethodException e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        protected <S extends T> S postInsert(S entity, Number generatedId) {
            try {
                ID id = (ID) getNumberValueMethod.invoke(generatedId);
                idSetter.invoke(entity, id);
            } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                throw new RuntimeException(e);
            }
            return entity;
        }
    }

    private class ManuallyAssignedRepositoryStrategy extends RepositoryStrategy {

    }

    private class ReadOnlyRepositoryStrategy extends RepositoryStrategy {
    }

    private class SingleIdToArray implements IdToArrayStrategy {

        @Override
        public Object[] getIdArray(Object id) {
            return BaseReflectionJdbcRepository.super.getIdArray(id);
        }

    }

    private class CompoundIdToArray implements IdToArrayStrategy {

        private List<Method> methods;

        {
            try {
                JdbcEntityInformation<?, ?> jdbcInfo = (JdbcEntityInformation<?, ?>) BaseReflectionJdbcRepository.this
                        .getEntityInfo();
                methods = new ArrayList<Method>(jdbcInfo.getIdFields().size());
                for (Field field : jdbcInfo.getIdFields()) {
                    methods.add(ReflectionMethodsUtils.findGetterMethod(field, jdbcInfo.getIdType()));
                }
            } catch (SecurityException e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        public Object[] getIdArray(Object id) {
            try {
                Object[] objects = new Object[methods.size()];
                for (int i = 0; i < methods.size(); i++) {
                    objects[i] = methods.get(i).invoke(id);
                }
                return objects;
            } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                throw new RuntimeException(e);
            }
        }

    }

}
