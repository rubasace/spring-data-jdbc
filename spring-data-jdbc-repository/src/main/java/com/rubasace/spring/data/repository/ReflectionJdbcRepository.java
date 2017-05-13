package com.rubasace.spring.data.repository;

import com.rubasace.spring.data.repository.mapping.ReflectionRowMapper;
import com.rubasace.spring.data.repository.mapping.ReflectionRowUnmapper;
import com.rubasace.spring.data.repository.mapping.UnsupportedRowUnmapper;
import com.rubasace.spring.data.repository.model.JdbcEntityInformation;
import com.rubasace.spring.data.repository.sql.SqlGeneratorFactory;
import com.rubasace.spring.data.repository.strategy.crud.AbstractRepositoryStrategy;
import com.rubasace.spring.data.repository.strategy.crud.RepositoryStrategyFactory;
import com.rubasace.spring.data.repository.strategy.id.IdToArrayStrategy;
import com.rubasace.spring.data.repository.strategy.id.IdToArrayStrategyFactory;
import com.rubasace.spring.data.repository.util.EntityUtils;

import java.io.Serializable;

public class ReflectionJdbcRepository<T, ID extends Serializable> extends BaseJdbcRepository<T, ID> {

    protected AbstractRepositoryStrategy repositoryStrategy;
    protected IdToArrayStrategy idToArrayStrategy;

    public ReflectionJdbcRepository(Class<T> entityClass, final SqlGeneratorFactory sqlGeneratorFactory) {
        super(EntityUtils.getEntityInformation(entityClass), new ReflectionRowMapper<>(entityClass),
              new UnsupportedRowUnmapper<T>(), EntityUtils.getTableDescription(entityClass), sqlGeneratorFactory);
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
