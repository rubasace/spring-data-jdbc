package com.rubasace.spring.data.repository.strategy.id;

import com.rubasace.spring.data.repository.model.JdbcEntityInformation;

public class IdToArrayStrategyFactory {

    private IdToArrayStrategyFactory() {

    }

    public static IdToArrayStrategy getIdToArrayStrategy(JdbcEntityInformation entityInformation) {
        if (entityInformation.isCompoundKey()) {
            return new CompoundIdToArrayStrategy(entityInformation);
        }
        return new SingleIdToArrayStrategy();
    }
}
