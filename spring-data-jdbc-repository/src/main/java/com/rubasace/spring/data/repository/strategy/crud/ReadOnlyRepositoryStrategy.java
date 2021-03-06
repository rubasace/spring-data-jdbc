package com.rubasace.spring.data.repository.strategy.crud;

import org.springframework.data.repository.core.EntityInformation;

class ReadOnlyRepositoryStrategy extends AbstractRepositoryStrategy {

    ReadOnlyRepositoryStrategy(final EntityInformation entityInformation) {
        super(entityInformation);
    }
}
