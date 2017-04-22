package com.rubasace.spring.data.jdbc.repository;

import com.rubasace.spring.data.jdbc.BaseJdbcRepository;
import com.rubasace.spring.data.jdbc.EntityType;
import com.rubasace.spring.data.jdbc.mapping.ReflectionRowMapper;
import com.rubasace.spring.data.jdbc.mapping.ReflectionRowUnmapper;
import com.rubasace.spring.data.jdbc.mapping.UnsupportedRowUnmapper;
import com.rubasace.spring.data.jdbc.model.JdbcEntityInformation;
import com.rubasace.spring.data.jdbc.repository.strategies.AbstractRepositoryStrategy;
import com.rubasace.spring.data.jdbc.repository.strategies.RepositoryStrategyFactory;
import com.rubasace.spring.data.jdbc.util.EntityUtils;
import com.rubasace.spring.data.jdbc.util.ReflectionMethodsUtils;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class ReflectionJdbcRepository<T, ID extends Serializable> extends BaseJdbcRepository<T, ID> {

    AbstractRepositoryStrategy repositoryStrategy;
    IdToArrayStrategy idToArrayStrategy;

    public ReflectionJdbcRepository(Class<T> entityClass) {
        super(EntityUtils.getEntityInformation(entityClass), new ReflectionRowMapper<>(entityClass),
              new UnsupportedRowUnmapper<T>(), EntityUtils.getTableDescription(entityClass));
        repositoryStrategy = RepositoryStrategyFactory.chooseStrategy(entityInfo);
        if (!EntityUtils.getEntityType(entityInfo).equals(EntityType.READ_ONLY)) {
            this.rowUnmapper = new ReflectionRowUnmapper<>(entityClass);
        }
        if (((JdbcEntityInformation) entityInfo).isCompoundKey()) {
            idToArrayStrategy = new CompoundIdToArray();
        } else {
            idToArrayStrategy = new SingleIdToArray();
        }
    }



    @Override
    protected Object[] getIdArray(Object id) {
        return idToArrayStrategy.getIdArray(id);

    }

    @Override
    protected <S extends T> S postUpdate(S entity) {
        return repositoryStrategy.postUpdate(entity);
    }

    @Override
    protected <S extends T> S postInsert(S entity, Number generatedId) {
        return repositoryStrategy.postInsert(entity, generatedId);
    }

    private interface IdToArrayStrategy {
        Object[] getIdArray(Object id);
    }



    private class SingleIdToArray implements IdToArrayStrategy {

        @Override
        public Object[] getIdArray(Object id) {
            return ReflectionJdbcRepository.super.getIdArray(id);
        }

    }

    private class CompoundIdToArray implements IdToArrayStrategy {

        private List<Method> methods;

        {
            try {
                JdbcEntityInformation<?, ?> jdbcInfo = (JdbcEntityInformation<?, ?>) ReflectionJdbcRepository.this
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
