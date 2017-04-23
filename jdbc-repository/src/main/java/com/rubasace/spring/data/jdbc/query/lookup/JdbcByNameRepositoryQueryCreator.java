package com.rubasace.spring.data.jdbc.query.lookup;


import com.rubasace.spring.data.jdbc.query.JdbcQueryMethod;
import com.rubasace.spring.data.jdbc.query.lookup.JdbcByNameRepositoryQuery.Strategy;
import com.rubasace.spring.data.jdbc.sql.SqlGenerator;
import com.rubasace.spring.data.repository.TableDescription;
import com.rubasace.spring.data.repository.util.ReflectionMethodsUtils;
import com.rubasace.spring.data.repository.util.SQLJavaNamingUtils;
import org.apache.commons.lang3.ClassUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mapping.PropertyPath;
import org.springframework.data.repository.query.parser.AbstractQueryCreator;
import org.springframework.data.repository.query.parser.Part;
import org.springframework.data.repository.query.parser.Part.Type;
import org.springframework.data.repository.query.parser.PartTree;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

//TODO allow choose only some columns from the table
public class JdbcByNameRepositoryQueryCreator extends AbstractQueryCreator<JdbcByNameRepositoryQuery, String> {

    private final NamedParameterJdbcOperations template;
    private final RowMapper rowMapper;
    private final SqlGenerator builder;
    private final TableDescription tableDescription;
    private final JdbcQueryMethod method;
    private Strategy strategy;

    private PartTree tree;

    private List<ParameterProperties> parameterProperties;

    {
        parameterProperties = new ArrayList<>();
    }

    // TODO check alternatives for generateParameters (Spring has something)
    public JdbcByNameRepositoryQueryCreator(JdbcQueryMethod method, PartTree tree, SqlGenerator builder,
                                            NamedParameterJdbcOperations template, TableDescription tableDescription,
                                            @SuppressWarnings("rawtypes")
                                                    RowMapper rowMapper) {
        super(tree, ReflectionMethodsUtils.generateParameters(method));
        this.tree = tree;
        this.method = method;
        this.builder = builder;
        this.template = template;
        this.rowMapper = rowMapper;
        this.tableDescription = tableDescription;
        this.strategy = chooseStrategy(tree, method);
    }

    private Strategy chooseStrategy(PartTree tree, JdbcQueryMethod method) {
        if (tree.isCountProjection()) {
            return Strategy.COUNT;
        }
        if (method.isCollectionQuery()) {
            return Strategy.COLLECTION_QUERY;
        }
        if (method.isQueryForEntity()) {
            return Strategy.SINGLE_QUERY;
        }
        if (method.isPageQuery()) {
            return Strategy.PAGE_QUERY;
        }
        if (tree.isDelete()) {
            return Strategy.UPDATE_QUERY;
        }
        Class<?> returnedClass = method.getReturnedObjectType();
        if (returnedClass.isPrimitive()) {
            returnedClass = ClassUtils.primitiveToWrapper(returnedClass);
        }
        if (returnedClass.equals(Boolean.class)) {
            return Strategy.EXISTS_QUERY;
        }
        throw new IllegalArgumentException("Don't know what strategy to follow!!");
    }

    public boolean isUpdate() {
        return tree.isDelete();
    }

    private class QueryPartBuilder {

        private final Part part;

        /**
         * Creates a new {@link QueryPartBuilder} for the given {@link Part} and
         * {@link Root}.
         *
         * @param part must not be {@literal null}.
         * @param root must not be {@literal null}.
         */
        public QueryPartBuilder(Part part, Iterator<Object> iterator) {

            Assert.notNull(part);
            Assert.notNull(iterator);
            this.part = part;
        }

        /**
         * Builds a JPA {@link Predicate} from the underlying {@link Part}.
         *
         * @return
         */

        //TODO have in count ignorecases
        //TODO solve startingWith and similar likes
        public String build() {

            ParameterProperties props = new ParameterProperties();
            boolean receivesAttribute = true;
            String query;

            PropertyPath property = part.getProperty();
            Type type = part.getType();
            String attribute = SQLJavaNamingUtils.geColumnNameFromAttributeName(property.getSegment());

            switch (type) {
                case BETWEEN:
                    props.setBetween(true);
                    query = builder.between(attribute);
                    break;
                case AFTER:
                case GREATER_THAN:
                    query = builder.gt(attribute);
                    break;
                case GREATER_THAN_EQUAL:
                    query = builder.ge(attribute);
                    break;
                case BEFORE:
                case LESS_THAN:
                    query = builder.lt(attribute);
                    break;
                case LESS_THAN_EQUAL:
                    query = builder.le(attribute);
                    break;
                case IS_NULL:
                    query = builder.isNull(attribute);
                    receivesAttribute = false;
                    break;
                case IS_NOT_NULL:
                    query = builder.isNotNull(attribute);
                    receivesAttribute = false;
                    break;
                case IN:
                    query = builder.in(attribute, false);
                    break;
                case NOT_IN:
                    query = builder.notIn(attribute);
                    break;
                case STARTING_WITH:
                    props.setSpecialLike(ParameterProperties.SpecialLike.STARTS_WITH);
                    query = builder.like(attribute, false);
                    break;
                case ENDING_WITH:
                    props.setSpecialLike(ParameterProperties.SpecialLike.ENDS_WITH);
                    query = builder.like(attribute, false);
                    break;
                case CONTAINING:
                    props.setSpecialLike(ParameterProperties.SpecialLike.CONTAINS);
                    query = builder.like(attribute, false);
                    break;
                case NOT_CONTAINING:
                    props.setSpecialLike(ParameterProperties.SpecialLike.CONTAINS);
                    query = builder.notLike(attribute);
                    break;
                case LIKE:
                    query = builder.like(attribute, false);
                    break;
                case NOT_LIKE:
                    query = builder.notLike(attribute);
                    break;
                case TRUE:
                    query = builder.isTrue(attribute);
                    receivesAttribute = false;
                    break;
                case FALSE:
                    query = builder.isFalse(attribute);
                    receivesAttribute = false;
                    break;
                case SIMPLE_PROPERTY:
                    query = builder.compareAttribute(attribute);
                    break;
                case NEGATING_SIMPLE_PROPERTY:
                    query = builder.compareAttribute(attribute, true);
                    break;
                default:
                    throw new IllegalArgumentException("Unsupported keyword " + type);
            }
            if (receivesAttribute) {
                parameterProperties.add(props);
                if (props.isBetween()) {
                    //between receives two argument for a single attribute, so we insert it twice (lower and greater)
                    parameterProperties.add(props);
                }
            }
            return query;
        }

        // private <T> Expression<T> upperIfIgnoreCase(Expression<? extends T>
        // expression) {
        //
        // switch (part.shouldIgnoreCase()) {
        //
        // case ALWAYS:
        //
        // Assert.state(canUpperCase(expression), "Unable to ignore case of " +
        // expression.getJavaType().getName()
        // + " types, the property '" + part.getProperty().getSegment() + "'
        // must reference a String");
        // return (Expression<T>) builder.upper((Expression<String>)
        // expression);
        //
        // case WHEN_POSSIBLE:
        //
        // if (canUpperCase(expression)) {
        // return (Expression<T>) builder.upper((Expression<String>)
        // expression);
        // }
        //
        // case NEVER:
        // default:
        //
        // return (Expression<T>) expression;
        // }
        // }
        //
        // private boolean canUpperCase(Expression<?> expression) {
        // return String.class.equals(expression.getJavaType());
        // }
    }

    @Override
    protected String create(Part part, Iterator<Object> iterator) {
        return new QueryPartBuilder(part, iterator).build();
    }


    @Override
    protected String and(Part part, String left, Iterator<Object> iterator) {
        return builder.and(left, create(part, iterator));
    }

    @Override
    protected String or(String left, String right) {
        return builder.or(left, right);
    }

    @Override
    protected JdbcByNameRepositoryQuery complete(String base, Sort sort) {
        String query;
        if (Strategy.COUNT.equals(strategy) || Strategy.EXISTS_QUERY.equals(strategy)) {
            query = builder.count(tableDescription.getTableName());
        } else if (tree.isDelete()) {
            query = builder.delete(tableDescription.getTableName());
        } else {
            query = builder.select(tableDescription.getTableName(), tree.isDistinct());
        }
        query += builder.where() + base;
        if (tree.isLimiting()) {
            Pageable page = new PageRequest(0, tree.getMaxResults(), sort);
            query = builder.limit(query, page);
        } else {
            query = builder.sort(query, sort);
        }
        return new JdbcByNameRepositoryQuery(method, template, query, rowMapper, strategy, parameterProperties);
    }


}