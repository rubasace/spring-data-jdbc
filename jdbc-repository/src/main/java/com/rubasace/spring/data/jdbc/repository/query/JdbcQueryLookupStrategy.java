/*
 * Copyright 2008-2016 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.rubasace.spring.data.jdbc.repository.query;

import com.rubasace.spring.data.jdbc.repository.query.annotated.JdbcAnnotatedRepositoryQueryCreator;
import com.rubasace.spring.data.jdbc.repository.query.lookup.JdbcByNameRepositoryQueryCreator;
import com.rubasace.spring.data.jdbc.repository.sql.SqlGenerator;
import com.rubasace.spring.data.jdbc.TableDescription;
import org.springframework.data.repository.core.NamedQueries;
import org.springframework.data.repository.core.RepositoryMetadata;
import org.springframework.data.repository.query.EvaluationContextProvider;
import org.springframework.data.repository.query.QueryLookupStrategy;
import org.springframework.data.repository.query.QueryLookupStrategy.Key;
import org.springframework.data.repository.query.RepositoryQuery;
import org.springframework.data.repository.query.parser.PartTree;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.util.Assert;

import javax.management.Query;
import java.lang.reflect.Method;

/**
 * Query lookup strategy to execute finders.
 *
 * @author Oliver Gierke
 * @author Thomas Darimont
 */
public final class JdbcQueryLookupStrategy {

    /**
     * Private constructor to prevent instantiation.
     */
    private JdbcQueryLookupStrategy() {
    }

    /**
     * Creates a {@link QueryLookupStrategy} for the given {@link EntityManager}
     * and {@link Key}.
     *
     * @param em                        must not be {@literal null}.
     * @param key                       may be {@literal null}.
     * @param extractor                 must not be {@literal null}.
     * @param evaluationContextProvider must not be {@literal null}.
     * @return
     */
    public static QueryLookupStrategy create(Key key, EvaluationContextProvider evaluationContextProvider,
                                             SqlGenerator generator, NamedParameterJdbcTemplate template, RowMapper rowMapper,
                                             TableDescription tableDescription) {

        Assert.notNull(evaluationContextProvider, "EvaluationContextProvider must not be null!");

        switch (key != null ? key : Key.CREATE_IF_NOT_FOUND) {
            case CREATE:
                return new CreateQueryLookupStrategy(generator, template, rowMapper, tableDescription);
            case USE_DECLARED_QUERY:
                return new DeclaredQueryLookupStrategy(generator, template, rowMapper, tableDescription);
            case CREATE_IF_NOT_FOUND:
                return new CreateIfNotFoundQueryLookupStrategy(generator, template, rowMapper, tableDescription);
            default:
                throw new IllegalArgumentException(String.format("Unsupported query lookup strategy %s!", key));
        }
    }

    /**
     * Base class for {@link QueryLookupStrategy} implementations that need
     * access to an {@link EntityManager}.
     *
     * @author Oliver Gierke
     * @author Thomas Darimont
     */
    private abstract static class AbstractQueryLookupStrategy implements QueryLookupStrategy {

        protected SqlGenerator generator;
        protected NamedParameterJdbcOperations template;
        protected RowMapper rowMapper;
        protected TableDescription tableDescription;


        public AbstractQueryLookupStrategy() {
            super();
        }

        /**
         * Creates a new {@link AbstractQueryLookupStrategy}.
         *
         * @param em
         * @param extractor
         * @param evaluationContextProvider
         */
        public AbstractQueryLookupStrategy(SqlGenerator generator, NamedParameterJdbcOperations template, RowMapper rowMapper,
                                           TableDescription tableDescription) {
            this.generator = generator;
            this.template = template;
            this.rowMapper = rowMapper;
            this.tableDescription = tableDescription;
        }

        /*
         * (non-Javadoc)
         *
         * @see org.springframework.data.repository.query.QueryLookupStrategy#
         * resolveQuery(java.lang.reflect.Method,
         * org.springframework.data.repository.core.RepositoryMetadata,
         * org.springframework.data.projection.ProjectionFactory,
         * org.springframework.data.repository.core.NamedQueries)
         */
        @Override
        public final RepositoryQuery resolveQuery(Method method, RepositoryMetadata metadata,
                                                  NamedQueries namedQueries) {
            return resolveQuery(new JdbcQueryMethod(method, metadata), namedQueries);
        }

        protected abstract RepositoryQuery resolveQuery(JdbcQueryMethod method, NamedQueries namedQueries);
    }

    /**
     * {@link QueryLookupStrategy} to create a query from the method name.
     *
     * @author Oliver Gierke
     * @author Thomas Darimont
     */
    private static class CreateQueryLookupStrategy extends AbstractQueryLookupStrategy {

        public CreateQueryLookupStrategy(SqlGenerator generator, NamedParameterJdbcOperations template, RowMapper rowMapper,
                                         TableDescription tableDescription) {
            super(generator, template, rowMapper, tableDescription);
        }

        @Override
        protected RepositoryQuery resolveQuery(JdbcQueryMethod method, NamedQueries namedQueries) {
            PartTree tree = new PartTree(method.getName(), method.getEntityInformation().getJavaType());
            JdbcByNameRepositoryQueryCreator creator = new JdbcByNameRepositoryQueryCreator(method, tree, generator, template,
                                                                                            tableDescription, rowMapper);
            return creator.createQuery();
        }

    }

    /**
     * {@link QueryLookupStrategy} that tries to detect a declared query
     * declared via {@link Query} annotation followed by a JPA named query
     * lookup.
     *
     * @author Oliver Gierke
     * @author Thomas Darimont
     */
    private static class DeclaredQueryLookupStrategy extends AbstractQueryLookupStrategy {


        /**
         * Creates a new {@link DeclaredQueryLookupStrategy}.
         *
         * @param datasource
         * @param em
         * @param extractor
         * @param evaluationContextProvider
         */
        public DeclaredQueryLookupStrategy(SqlGenerator generator, NamedParameterJdbcOperations template, RowMapper rowMapper,
                                           TableDescription tableDescription) {
            super(generator, template, rowMapper, tableDescription);
        }

        /*
         * (non-Javadoc)
         *
         * @see
         * org.springframework.data.jpa.repository.query.JpaQueryLookupStrategy.
         * AbstractQueryLookupStrategy#resolveQuery(org.springframework.data.jpa
         * .repository.query.JpaQueryMethod, javax.persistence.EntityManager,
         * org.springframework.data.repository.core.NamedQueries)
         */
        @Override
        protected RepositoryQuery resolveQuery(JdbcQueryMethod method, NamedQueries namedQueries) {
            JdbcAnnotatedRepositoryQueryCreator creator = new JdbcAnnotatedRepositoryQueryCreator(method, generator, template,
                                                                                                  tableDescription, rowMapper);
            return creator.createQuery();
        }
    }

    /**
     * {@link QueryLookupStrategy} to try to detect a declared query first (
     * {@link org.springframework.data.jpa.repository.Query}, JPA named query).
     * In case none is found we fall back on query creation.
     *
     * @author Oliver Gierke
     * @author Thomas Darimont
     */
    private static class CreateIfNotFoundQueryLookupStrategy extends AbstractQueryLookupStrategy {

        private final DeclaredQueryLookupStrategy lookupStrategy;
        private final CreateQueryLookupStrategy createStrategy;

        /**
         * Creates a new {@link CreateIfNotFoundQueryLookupStrategy}.
         *
         * @param em
         * @param extractor
         * @param createStrategy
         * @param lookupStrategy
         * @param evaluationContextProvider
         */
        public CreateIfNotFoundQueryLookupStrategy(SqlGenerator generator, NamedParameterJdbcOperations template, RowMapper rowMapper,
                                                   TableDescription tableDescription) {
            super();

            this.createStrategy = new CreateQueryLookupStrategy(generator, template, rowMapper, tableDescription);
            this.lookupStrategy = new DeclaredQueryLookupStrategy(generator, template, rowMapper, tableDescription);
        }

        /*
         * (non-Javadoc)
         *
         * @see
         * org.springframework.data.jpa.repository.query.JpaQueryLookupStrategy.
         * AbstractQueryLookupStrategy#resolveQuery(org.springframework.data.jpa
         * .repository.query.JpaQueryMethod, javax.persistence.EntityManager,
         * org.springframework.data.repository.core.NamedQueries)
         */
        @Override
        protected RepositoryQuery resolveQuery(JdbcQueryMethod method, NamedQueries namedQueries) {

            try {
                return lookupStrategy.resolveQuery(method, namedQueries);
            } catch (IllegalStateException e) {
                return createStrategy.resolveQuery(method, namedQueries);
            }
        }
    }
}
