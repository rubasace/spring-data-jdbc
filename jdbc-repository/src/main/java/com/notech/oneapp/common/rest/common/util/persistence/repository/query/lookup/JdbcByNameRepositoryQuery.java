package com.notech.oneapp.common.rest.common.util.persistence.repository.query.lookup;

import com.notech.oneapp.common.rest.common.util.persistence.repository.query.processor.QueryParameterProcessor;
import com.notech.oneapp.common.rest.common.util.persistence.repository.query.processor.QueryParameterProcessorFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.repository.query.QueryMethod;
import org.springframework.data.repository.query.RepositoryQuery;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.jdbc.core.namedparam.NamedParameterUtils;
import org.springframework.jdbc.core.namedparam.ParsedSql;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//TODO common factor among both implementations
public class JdbcByNameRepositoryQuery implements RepositoryQuery {

    private static final String GET_TEMPLATE_PARAMETERS_METHOD_NAME = "getParameterNames";
    private static final Logger LOGGER = LoggerFactory.getLogger(RepositoryQuery.class);
    private final Method getParametersMethod;
    private final BaseJdbcRepositoryStrategy strategy;
    private List<QueryParameterProcessor> parameterMethodsList;
    private List<String> parametersNames;
    private QueryMethod method;
    private String query;
    private NamedParameterJdbcOperations template;
    @SuppressWarnings("rawtypes")
    private RowMapper rowMapper;

    {
        try {
            parametersNames = new ArrayList<>();
            parameterMethodsList = new ArrayList<>();
            getParametersMethod = ParsedSql.class.getDeclaredMethod(GET_TEMPLATE_PARAMETERS_METHOD_NAME);
            ReflectionUtils.makeAccessible(getParametersMethod);
        } catch (NoSuchMethodException | SecurityException e) {
            throw new RuntimeException(e);
        }
    }

    public JdbcByNameRepositoryQuery(QueryMethod method, NamedParameterJdbcOperations template, String query, RowMapper rowMapper,
                                     Strategy strategy, List<ParameterProperties> parameterProperties) {
        this.method = method;
        this.query = query;
        this.template = template;
        this.rowMapper = rowMapper;

        LOGGER.debug("applying strategy {}", strategy.name());

        processMethodParameters(parameterProperties);

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
                this.strategy = new UpdatetJdbcRepositoryStrategy();
                break;
            case EXISTS_QUERY:
                this.strategy = new ExistsJdbcRepositoryStrategy();
                break;
            default:
                throw new IllegalArgumentException("Unkwnown strategy provided");
        }

    }

    private void processMethodParameters(List<ParameterProperties> parameterProperties) {
        ParsedSql parsedSQL = NamedParameterUtils.parseSqlStatement(query);
        try {
            parametersNames = (List<String>) getParametersMethod.invoke(parsedSQL);

            for (int i = 0; i < parametersNames.size(); i++) {
                ParameterProperties properties = parameterProperties.get(i);
                QueryParameterProcessor processor = QueryParameterProcessorFactory
                        .getQueryParameterProcessor(properties);
                parameterMethodsList.add(processor);
            }

        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Object execute(Object[] parameters) {
        LOGGER.info("executing query {} ", query);
        LOGGER.trace("parameters -> {}", parameters);

        Map<String, Object> namedParameters = new HashMap<>();
        for (int i = 0; i < parameters.length; i++) {
            namedParameters.put(parametersNames.get(i), parameterMethodsList.get(i).processParameter(parameters[i]));
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
            return JdbcByNameRepositoryQuery.this.template.queryForObject(query, parameters, Long.class);
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
            List result = JdbcByNameRepositoryQuery.this.template.query(query, parameters, rowMapper);
            return CollectionUtils.isEmpty(result) ? null : result.get(0);
        }

    }

    private class ExistsJdbcRepositoryStrategy implements BaseJdbcRepositoryStrategy {

        @Override
        public Object execute(Map<String, Object> parameters) {
            return JdbcByNameRepositoryQuery.this.template.queryForObject(query, parameters, Long.class) > 0;
        }

    }

    private class CollectionQueryJdbcRepositoryStrategy implements BaseJdbcRepositoryStrategy {

        @Override
        public Object execute(Map<String, Object> parameters) {
            return JdbcByNameRepositoryQuery.this.template.query(query, parameters, JdbcByNameRepositoryQuery.this.rowMapper);
        }

    }

    private class UpdatetJdbcRepositoryStrategy implements BaseJdbcRepositoryStrategy {

        @Override
        public Object execute(Map<String, Object> parameters) {
            JdbcByNameRepositoryQuery.this.template.update(query, parameters);
            return null;
        }

    }

}
