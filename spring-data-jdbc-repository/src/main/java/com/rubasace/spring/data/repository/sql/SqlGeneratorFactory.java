/*
 * Copyright 2016 Jakub Jirutka <jakub@jirutka.cz>.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.rubasace.spring.data.repository.sql;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

@Component
public class SqlGeneratorFactory {

    private static final Logger LOG = LoggerFactory.getLogger(SqlGeneratorFactory.class);
    private final Map<DataSource, SqlGenerator> cache = new WeakHashMap<>(2, 1.0f);
    private final List<SqlGenerator> generators;

    @Autowired
    public SqlGeneratorFactory(final List<SqlGenerator> generators) {
        this.generators = generators;
    }

    /**
     * @param dataSource The DataSource for which to find compatible
     *                   SQL Generator.
     * @return An SQL Generator compatible with the given {@code dataSource}.
     * @throws DataAccessResourceFailureException if exception is thrown when
     *                                            trying to obtain Connection or MetaData from the
     *                                            {@code dataSource}.
     * @throws IllegalStateException              if no compatible SQL Generator is found.
     */
    public SqlGenerator getGenerator(DataSource dataSource) {

        if (cache.containsKey(dataSource)) {
            return cache.get(dataSource);
        }

        DatabaseMetaData metaData;
        try {
            metaData = dataSource.getConnection().getMetaData();
        } catch (SQLException ex) {
            throw new DataAccessResourceFailureException(
                    "Failed to retrieve database metadata", ex);
        }

        for (SqlGenerator generator : generators) {
            try {
                if (generator.isCompatible(metaData)) {
                    LOG.info("Using SQL Generator {} for dataSource {}",
                             generator.getClass().getName(), dataSource.getClass());

                    cache.put(dataSource, generator);
                    return generator;
                }
            } catch (SQLException ex) {
                LOG.warn("Exception occurred when invoking isCompatible() on {}",
                         generator.getClass().getSimpleName(), ex);
            }
        }

        // This should not happen, because registry should always contain one
        // "default" generator that returns true for every DatabaseMetaData.
        throw new IllegalStateException("No compatible SQL Generator found.");
    }
}
