package com.rubasace.spring.data.jdbc.query.annotated;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.repository.query.QueryMethod;
import org.springframework.data.repository.query.RepositoryQuery;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.util.CollectionUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JdbcAnnotatedRepositoryQuery implements RepositoryQuery {

    private static final String GET_TEMPLATE_PARAMETERS_METHOD_NAME = "getParameterNames";
    private static final Logger LOGGER = LoggerFactory.getLogger(RepositoryQuery.class);
    private final BaseJdbcRepositoryStrategy strategy;
    private List<String> parametersNames;
    private QueryMethod method;
    private String query;
    private NamedParameterJdbcOperations template;
    @SuppressWarnings("rawtypes")
    private RowMapper rowMapper;

    public JdbcAnnotatedRepositoryQuery(QueryMethod method, NamedParameterJdbcOperations template, String query, RowMapper rowMapper,
                                        Strategy strategy, List<String> parametersNames) {
        this.method = method;
        this.query = query;
        this.template = template;
        this.rowMapper = rowMapper;
        this.parametersNames = parametersNames;

        LOGGER.debug("applying strategy {}", strategy.name());

        switch (strategy) {
            case COLLECTION_QUERY:
                this.strategy = new CollectionQueryJdbcRepositoryStrategy();
                break;
            case COUNT:
                this.strategy = new CountJdbcRepositoryStrategy();
                break;
            case SINGLE_QUERY:
                this.strategy = new SingleQueryJdbcRepositoryStrategy();
                break;
            case UPDATE_QUERY:
                this.strategy = new UpdatetJdbcRepositoryStrategy();
                break;
            case PAGE_QUERY:
                this.strategy = new PageJdbcRepositoryStrategy();
                break;
            case EXISTS_QUERY:
                this.strategy = new ExistsJdbcRepositoryStrategy();
                break;
            default:
                throw new IllegalArgumentException("Unkwnown strategy provided");
        }

    }

    @Override
    public Object execute(Object[] parameters) {
        LOGGER.info("executing query {} ", query);
        LOGGER.trace("parameters -> {}", parameters);

        Map<String, Object> namedParameters = new HashMap<>();
        for (int i = 0; i < parameters.length; i++) {
            if (parametersNames.get(i) != null) {
                namedParameters.put(parametersNames.get(i), parameters[i]);
            }
        }
        return strategy.execute(namedParameters);
    }

    @Override
    public QueryMethod getQueryMethod() {
        return method;
    }

    protected enum Strategy {
        COUNT,
        SINGLE_QUERY,
        COLLECTION_QUERY,
        UPDATE_QUERY,
        PAGE_QUERY,
        EXISTS_QUERY
    }

    private interface BaseJdbcRepositoryStrategy {
        Object execute(Map<String, Object> parameters);
    }

    private class CountJdbcRepositoryStrategy implements BaseJdbcRepositoryStrategy {

        @Override
        public Object execute(Map<String, Object> parameters) {
            return JdbcAnnotatedRepositoryQuery.this.template.queryForObject(query, parameters, Long.class);
        }

    }

    private class ExistsJdbcRepositoryStrategy implements BaseJdbcRepositoryStrategy {

        @Override
        public Object execute(Map<String, Object> parameters) {
            return JdbcAnnotatedRepositoryQuery.this.template.queryForObject(query, parameters, Long.class) > 0;
        }

    }

    // TODO 2 queries, one for results, one for total vs extra field with total
    // on same query
    private class PageJdbcRepositoryStrategy implements BaseJdbcRepositoryStrategy {

        @Override
        public Object execute(Map<String, Object> parameters) {
            // return BaseJdbcRepositoryQuery.this.template.query(query,
            // BaseJdbcRepositoryQuery.this.rowMapper);
            // return new PageImpl<>(jdbcOps., page, count())
            return null;
        }

    }

    private class SingleQueryJdbcRepositoryStrategy implements BaseJdbcRepositoryStrategy {

        @Override
        public Object execute(Map<String, Object> parameters) {
            List result = JdbcAnnotatedRepositoryQuery.this.template.query(query, parameters, rowMapper);
            return CollectionUtils.isEmpty(result) ? null : result.get(0);
        }

    }

    private class CollectionQueryJdbcRepositoryStrategy implements BaseJdbcRepositoryStrategy {

        @Override
        public Object execute(Map<String, Object> parameters) {
            return JdbcAnnotatedRepositoryQuery.this.template.query(query, parameters, JdbcAnnotatedRepositoryQuery.this.rowMapper);
        }

    }

    private class UpdatetJdbcRepositoryStrategy implements BaseJdbcRepositoryStrategy {

        @Override
        public Object execute(Map<String, Object> parameters) {
            JdbcAnnotatedRepositoryQuery.this.template.update(query, parameters);
            return null;
        }

    }

}
