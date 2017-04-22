package com.rubasace.spring.data.jdbc.repository.strategies;

import org.springframework.data.repository.core.EntityInformation;

class ReadOnlyRepositoryStrategy extends AbstractRepositoryStrategy {

    ReadOnlyRepositoryStrategy(final EntityInformation entityInformation) {
        super(entityInformation);
    }
}
