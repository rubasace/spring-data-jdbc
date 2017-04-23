package com.rubasace.spring.data.jdbc.query.annotated;

import com.rubasace.spring.data.jdbc.Query;
import com.rubasace.spring.data.jdbc.TableDescription;
import com.rubasace.spring.data.jdbc.query.JdbcQueryMethod;
import com.rubasace.spring.data.jdbc.sql.SqlGenerator;
import org.apache.commons.lang3.ClassUtils;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.query.QueryMethod;
import org.springframework.data.repository.query.RepositoryQuery;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.List;

public class JdbcAnnotatedRepositoryQueryCreator {

    private static final String METHOD_FIELD = "method";

    private static final Field originalMethod;

    static {
        try {
            originalMethod = QueryMethod.class.getDeclaredField(METHOD_FIELD);
            ReflectionUtils.makeAccessible(originalMethod);
        } catch (NoSuchFieldException | SecurityException e) {
            throw new RuntimeException(e);
        }
    }

    private final NamedParameterJdbcOperations template;
    @SuppressWarnings("rawtypes")
    private final RowMapper rowMapper;
    private final SqlGenerator builder;
    private final TableDescription tableDescription;
    private final JdbcQueryMethod method;
    private JdbcAnnotatedRepositoryQuery.Strategy strategy;
    private String query;
    private List<String> parameterKeys;


    public JdbcAnnotatedRepositoryQueryCreator(JdbcQueryMethod method, SqlGenerator builder,
                                               NamedParameterJdbcOperations template, TableDescription tableDescription,
                                               @SuppressWarnings("rawtypes")
                                                       RowMapper rowMapper) {
        this.method = method;
        this.builder = builder;
        this.template = template;
        this.rowMapper = rowMapper;
        this.tableDescription = tableDescription;
        this.strategy = chooseStrategy(method);
        prepareQuery(method);
    }

    private JdbcAnnotatedRepositoryQuery.Strategy chooseStrategy(JdbcQueryMethod method) {
        Class<?> returnType = method.getReturnedObjectType();
        if (returnType.isPrimitive()) {
            returnType = ClassUtils.primitiveToWrapper(returnType);
        }
        // TODO think a better way of handling this by the method name
        if (Number.class.isAssignableFrom(returnType)) {
            return JdbcAnnotatedRepositoryQuery.Strategy.COUNT;
        }
        if (Boolean.class.isAssignableFrom(returnType)) {
            return JdbcAnnotatedRepositoryQuery.Strategy.EXISTS_QUERY;
        }
        if (method.isCollectionQuery()) {
            return JdbcAnnotatedRepositoryQuery.Strategy.COLLECTION_QUERY;
        }
        if (method.isQueryForEntity()) {
            return JdbcAnnotatedRepositoryQuery.Strategy.SINGLE_QUERY;
        }
        if (method.isPageQuery()) {
            return JdbcAnnotatedRepositoryQuery.Strategy.PAGE_QUERY;
        }
        if (void.class.isAssignableFrom(returnType)) {
            return JdbcAnnotatedRepositoryQuery.Strategy.UPDATE_QUERY;
        }
        throw new IllegalArgumentException("Don't know what strategy to follow!!");
    }

    private void prepareQuery(JdbcQueryMethod method) {
        try {
            Method m = (Method) originalMethod.get(method);
            if (!m.isAnnotationPresent(Query.class)) {
                throw new IllegalStateException("Query annotation not present");
            }
            this.query = m.getAnnotation(Query.class).value();

            parameterKeys = new ArrayList<>(m.getParameterCount());
            for (Parameter parameter : m.getParameters()) {
                if (parameter.isAnnotationPresent(Param.class)) {
                    parameterKeys.add(parameter.getAnnotation(Param.class).value());
                } else {
                    parameterKeys.add(null);
                }
            }
        } catch (IllegalArgumentException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public RepositoryQuery createQuery() {
        return new JdbcAnnotatedRepositoryQuery(method, template, query, rowMapper, strategy, parameterKeys);
    }

}