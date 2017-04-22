package com.rubasace.spring.data.jdbc.repository.strategy.id;

import com.rubasace.spring.data.jdbc.model.JdbcEntityInformation;

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
