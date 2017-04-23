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

/**
 * SQL Generator for DB servers that support the SQL:2008 standard OFFSET
 * feature: Apache Derby, Microsoft SQL Server 2012, and Oracle 12c.
 */
public class SQL2008SqlGenerator extends DefaultSqlGenerator {

    @Override
    public boolean isCompatible(DatabaseMetaData metadata) throws SQLException {
        String productName = metadata.getDatabaseProductName();
        int majorVersion = metadata.getDatabaseMajorVersion();

        return "Apache Derby".equals(productName)
                || "Oracle".equals(productName) && majorVersion >= 12
                || "Microsoft SQL Server".equals(productName) && majorVersion >= 11;  // >= 2012
    }

    @Override
    public String limit(final String query, final Pageable page) {
        String sortClause = orderByExpression(page.getSort());
        return String.format("%s%s OFFSET %d ROWS FETCH NEXT %d ROW ONLY",
                             query, sortClause, page.getOffset(), page.getPageSize());
    }

}
