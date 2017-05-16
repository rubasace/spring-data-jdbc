package com.rubasace.spring.data.repository.strategy.id;

import com.rubasace.spring.data.repository.information.AbstractJdbcEntityInformation;

public class IdToArrayStrategyFactory {

    private IdToArrayStrategyFactory() {

    }

    public static IdToArrayStrategy getIdToArrayStrategy(AbstractJdbcEntityInformation entityInformation) {
        if (entityInformation.isCompoundKey()) {
            return new CompoundIdToArrayStrategy(entityInformation);
        }
        return new SingleIdToArrayStrategy();
    }
}
