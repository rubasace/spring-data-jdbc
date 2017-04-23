package com.rubasace.spring.data.jdbc.query.lookup.execution;

import com.rubasace.spring.data.jdbc.query.lookup.LookupStrategy;

public class JdbcRepositoryExecutionStrategyFactory {

    private JdbcRepositoryExecutionStrategyFactory() {

    }

    public static JdbcRepositoryExecutionStrategy chooseStrategy(LookupStrategy lookupStrategy) {
        switch (lookupStrategy) {
            case COLLECTION_QUERY:
                return new CollectionQueryJdbcRepositoryExecutionStrategy();
            case COUNT:
                return new CountJdbcRepositoryExecutionStrategy();
            case SINGLE_QUERY:
                return new SingleQueryJdbcRepositoryExecutionStrategy();
            case UPDATE_QUERY:
                return new UpdateJdbcRepositoryExecutionStrategy();
            case PAGE_QUERY:
                return new PageJdbcRepositoryExecutionStrategy();
            case EXISTS_QUERY:
                return new ExistsJdbcRepositoryExecutionStrategy();
            default:
                throw new IllegalArgumentException("Unkwnown lookupStrategy provided");
        }
    }
}
