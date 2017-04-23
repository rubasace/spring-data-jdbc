package com.rubasace.spring.data.jdbc.strategy.crud;

import org.springframework.data.repository.core.EntityInformation;

class ManuallyAssignedRepositoryStrategy extends AbstractRepositoryStrategy {

    ManuallyAssignedRepositoryStrategy(final EntityInformation entityInformation) {
        super(entityInformation);
    }
}
