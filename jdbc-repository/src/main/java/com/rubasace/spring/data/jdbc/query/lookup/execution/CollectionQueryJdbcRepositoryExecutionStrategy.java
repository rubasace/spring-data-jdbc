package com.rubasace.spring.data.jdbc.query.lookup.execution;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;

import java.util.Map;

class CollectionQueryJdbcRepositoryExecutionStrategy implements JdbcRepositoryExecutionStrategy {

    @Override
    public Object execute(final NamedParameterJdbcOperations namedParameterJdbcOperations, final String query,
                          final Map<String, Object> parameters, final RowMapper rowMapper) {
        return namedParameterJdbcOperations.query(query, parameters, rowMapper);
    }
}
