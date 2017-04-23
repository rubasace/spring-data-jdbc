package com.rubasace.spring.data.jdbc;

import com.rubasace.spring.data.jdbc.mapping.ReflectionRowMapper;
import com.rubasace.spring.data.jdbc.mapping.ReflectionRowUnmapper;
import com.rubasace.spring.data.jdbc.mapping.UnsupportedRowUnmapper;
import com.rubasace.spring.data.jdbc.model.JdbcEntityInformation;
import com.rubasace.spring.data.jdbc.strategy.crud.AbstractRepositoryStrategy;
import com.rubasace.spring.data.jdbc.strategy.crud.RepositoryStrategyFactory;
import com.rubasace.spring.data.jdbc.strategy.id.IdToArrayStrategy;
import com.rubasace.spring.data.jdbc.strategy.id.IdToArrayStrategyFactory;
import com.rubasace.spring.data.jdbc.util.EntityUtils;

import java.io.Serializable;

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
        idToArrayStrategy = IdToArrayStrategyFactory.getIdToArrayStrategy(getEntityInfo());
    }

    @Override
    protected Object[] getIdArray(Object id) {
        return idToArrayStrategy.getIdArray(id);

    }

    @Override
    protected JdbcEntityInformation<T, ID> getEntityInfo() {
        return (JdbcEntityInformation<T, ID>) super.getEntityInfo();
    }

    @Override
    protected <S extends T> S postUpdate(S entity) {
        return repositoryStrategy.postUpdate(entity);
    }

    @Override
    protected <S extends T> S postInsert(S entity, Number generatedId) {
        return repositoryStrategy.postInsert(entity, generatedId);
    }








}
