package com.rubasace.spring.data.jdbc.query.lookup.execution;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;

import java.util.Map;

public class ExistsJdbcRepositoryExecutionStrategy implements JdbcRepositoryExecutionStrategy {

    @Override
    public Boolean execute(final NamedParameterJdbcOperations namedParameterJdbcOperations, final String query,
                           final Map<String, Object> parameters, final RowMapper rowMapper) {
        return namedParameterJdbcOperations.queryForObject(query, parameters, Long.class) > 0;
    }
}
