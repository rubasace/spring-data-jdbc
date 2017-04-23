package com.rubasace.spring.data.jdbc.query.lookup.execution;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;

import java.util.Map;

class PageJdbcRepositoryExecutionStrategy implements JdbcRepositoryExecutionStrategy {

    // TODO 2 queries, one for results, one for total vs extra field with total
    // on same query
    @Override
    public Object execute(final NamedParameterJdbcOperations namedParameterJdbcOperations, final String query,
                          final Map<String, Object> parameters, final RowMapper rowMapper) {
        return null;
    }
}
