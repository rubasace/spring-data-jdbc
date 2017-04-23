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
package com.rubasace.spring.data.jdbc.sql;

import org.springframework.data.domain.Pageable;

import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.List;

import static java.lang.String.format;
import static java.util.Arrays.asList;

/**
 * SQL Generator for DB servers that support LIMIT ... OFFSET clause:
 * PostgreSQL, H2, HSQLDB, SQLite, MariaDB, and MySQL.
 */
public class LimitOffsetSqlGenerator extends DefaultSqlGenerator {

    private static final List<String> SUPPORTED_PRODUCTS =
            asList("PostgreSQL", "H2", "HSQL Database Engine", "MySQL");


    @Override
    public boolean isCompatible(DatabaseMetaData metadata) throws SQLException {
        return SUPPORTED_PRODUCTS.contains(metadata.getDatabaseProductName());
    }


    @Override
    public String limit(String query, Pageable page) {
        String sort = orderByExpression(page.getSort());
        return format("%s%s LIMIT %d OFFSET %d",
                      query, sort, page.getPageSize(), page.getOffset());
    }


}
