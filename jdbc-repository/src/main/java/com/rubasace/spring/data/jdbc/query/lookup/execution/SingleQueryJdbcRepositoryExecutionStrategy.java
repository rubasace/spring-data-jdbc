package com.rubasace.spring.data.jdbc.query.lookup.execution;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Map;

class SingleQueryJdbcRepositoryExecutionStrategy implements JdbcRepositoryExecutionStrategy {

    @Override
    public Object execute(final NamedParameterJdbcOperations namedParameterJdbcOperations, final String query,
                          final Map<String, Object> parameters, final RowMapper rowMapper) {
        List result = namedParameterJdbcOperations.query(query, parameters, rowMapper);
        return CollectionUtils.isEmpty(result) ? null : result.get(0);
    }
}
