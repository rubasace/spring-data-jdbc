package com.rubasace.spring.data.jdbc.query.lookup;

import com.rubasace.spring.data.jdbc.query.lookup.execution.JdbcRepositoryExecutionStrategy;
import com.rubasace.spring.data.jdbc.query.lookup.execution.JdbcRepositoryExecutionStrategyFactory;
import com.rubasace.spring.data.jdbc.query.processor.QueryParameterProcessor;
import com.rubasace.spring.data.jdbc.query.processor.QueryParameterProcessorFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.repository.query.QueryMethod;
import org.springframework.data.repository.query.RepositoryQuery;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.jdbc.core.namedparam.NamedParameterUtils;
import org.springframework.jdbc.core.namedparam.ParsedSql;
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
    private final JdbcRepositoryExecutionStrategy strategy;
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
                                     LookupStrategy lookupStrategy, List<ParameterProperties> parameterProperties) {
        this.method = method;
        this.query = query;
        this.template = template;
        this.rowMapper = rowMapper;

        LOGGER.debug("applying lookupStrategy {}", lookupStrategy.name());

        this.strategy = JdbcRepositoryExecutionStrategyFactory.chooseStrategy(lookupStrategy);
        processMethodParameters(parameterProperties);


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
        LOGGER.debug("executing query {} ", query);
        LOGGER.trace("parameters -> {}", parameters);

        Map<String, Object> namedParameters = new HashMap<>();
        for (int i = 0; i < parameters.length; i++) {
            namedParameters.put(parametersNames.get(i), parameterMethodsList.get(i).processParameter(parameters[i]));
        }
        return strategy.execute(template, query, namedParameters, rowMapper);
    }

    @Override
    public QueryMethod getQueryMethod() {
        return method;
    }


}
