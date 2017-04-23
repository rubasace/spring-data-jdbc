package com.rubasace.spring.data.repository.strategy.crud;

import com.rubasace.spring.data.repository.util.EntityUtils;
import org.springframework.data.repository.core.EntityInformation;

import java.io.Serializable;

public class RepositoryStrategyFactory {

    private RepositoryStrategyFactory() {
    }

    public static <T, ID extends Serializable> AbstractRepositoryStrategy chooseStrategy(EntityInformation<T, ID> entityInformation) {
        switch (EntityUtils.getEntityType(entityInformation)) {
            case AUTO_INCREMENTAL:
                return new AutoIncrementRepositoryStrategy(entityInformation);
            case MANUALLY_ASSIGNED:
                return new ManuallyAssignedRepositoryStrategy(entityInformation);
            case READ_ONLY:
                return new ReadOnlyRepositoryStrategy(entityInformation);
            default:
                throw new IllegalArgumentException("Don't know what strategy to instantiate");
        }

    }
}
