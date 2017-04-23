package com.rubasace.spring.data.jdbc.strategy.crud;

import org.springframework.data.repository.core.EntityInformation;

public abstract class AbstractRepositoryStrategy {

    private EntityInformation entityInformation;

    public AbstractRepositoryStrategy(final EntityInformation entityInformation) {
        this.entityInformation = entityInformation;
    }

    public <T> T postInsert(T entity, Number generatedId) {
        return entity;
    }

    public <T> T postUpdate(T entity) {
        return entity;
    }

    protected EntityInformation getEntityInformation() {
        return entityInformation;
    }
}
