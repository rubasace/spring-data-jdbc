/*
 * Copyright 2008-2015 the original author or authors.
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
package com.rubasace.spring.data.jdbc.repository.support;

import com.rubasace.spring.data.jdbc.TableDescription;
import com.rubasace.spring.data.jdbc.model.BaseReflectionJdbcRepository;
import com.rubasace.spring.data.jdbc.repository.query.JdbcQueryLookupStrategy;
import com.rubasace.spring.data.jdbc.repository.sql.SqlGenerator;
import com.rubasace.spring.data.jdbc.repository.sql.SqlGeneratorFactory;
import org.springframework.data.repository.core.EntityInformation;
import org.springframework.data.repository.core.RepositoryInformation;
import org.springframework.data.repository.core.RepositoryMetadata;
import org.springframework.data.repository.core.support.RepositoryFactorySupport;
import org.springframework.data.repository.query.EvaluationContextProvider;
import org.springframework.data.repository.query.QueryLookupStrategy;
import org.springframework.data.repository.query.QueryLookupStrategy.Key;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.util.Assert;
import org.springframework.util.ReflectionUtils;

import javax.sql.DataSource;
import java.io.Serializable;
import java.lang.reflect.Field;

/**
 * JPA specific generic repository factory.
 *
 * @author Oliver Gierke
 */
public class JdbcRepositoryFactory extends RepositoryFactorySupport {

    private static final String FIELD_ENTITY_INFO = "entityInfo";
    private static final String FIELD_SQL_GENERATOR = "sqlGenerator";
    private static final String FIELD_JDBC_OPS = "jdbcOps";
    private static final String FIELD_ROWMAPPER = "rowMapper";
    private static final String FIELD_TABLE_DESCRIPTION = "table";

    private static final Class<?> BASE_REPOSITORY_CLASS = BaseReflectionJdbcRepository.class;

    private DataSource datasource;
    private Class<?> entityClass;
    private BaseReflectionJdbcRepository repository;

    private TableDescription tableDescription;
    private SqlGenerator generator;
    private NamedParameterJdbcTemplate template;
    private RowMapper rowMapper;


    public JdbcRepositoryFactory(DataSource datasource) {
        super();
        this.datasource = datasource;
        //		//TODO use factory with cache?
        //		template = new JdbcTemplate(datasource);
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    @Override
    public <T, ID extends Serializable> EntityInformation<T, ID> getEntityInformation(Class<T> domainClass) {
        return extractRepositoryField(repository, FIELD_ENTITY_INFO);
    }

    @Override
    protected Object getTargetRepository(RepositoryInformation metadata) {
        entityClass = metadata.getDomainType();
        @SuppressWarnings("rawtypes")
        BaseReflectionJdbcRepository repository = getTargetRepositoryViaReflection(metadata, entityClass);
        repository.setDataSource(datasource);
        repository.afterPropertiesSet();
        this.repository = repository;

        generator = SqlGeneratorFactory.getInstance().getGenerator(datasource);
        template = new NamedParameterJdbcTemplate((JdbcOperations) extractRepositoryField(repository, FIELD_JDBC_OPS));
        rowMapper = extractRepositoryField(repository, FIELD_ROWMAPPER);
        tableDescription = extractRepositoryField(repository, FIELD_TABLE_DESCRIPTION);

        return repository;
    }

    @Override
    protected Class<?> getRepositoryBaseClass(RepositoryMetadata metadata) {
        return BASE_REPOSITORY_CLASS;
    }

    @Override
    protected QueryLookupStrategy getQueryLookupStrategy(Key key, EvaluationContextProvider evaluationContextProvider) {

        return JdbcQueryLookupStrategy.create(key, evaluationContextProvider, generator, template, rowMapper, tableDescription);
    }

    @SuppressWarnings("unchecked")
    private <T> T extractRepositoryField(
            @SuppressWarnings("rawtypes")
                    BaseReflectionJdbcRepository repository, String fieldName) {
        try {
            try {
                Field field = ReflectionUtils.findField(BASE_REPOSITORY_CLASS, fieldName);
                Assert.notNull(field, "Couldn't find the field " + fieldName);
                ReflectionUtils.makeAccessible(field);
                return (T) field.get(repository);

            } catch (SecurityException e) {
                throw new RuntimeException(e);
            }
        } catch (IllegalArgumentException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }


}
