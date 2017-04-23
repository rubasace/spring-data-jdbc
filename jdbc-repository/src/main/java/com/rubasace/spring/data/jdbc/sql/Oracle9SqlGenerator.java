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
 * SQL Generator for Oracle up to 11g. If you have 12g or newer, then use
 * {@link SQL2008SqlGenerator}.
 *
 * @see <a href="https://explainextended.com/2009/05/06/oracle-row_number-vs-rownum/">
 * Oracle: ROW_NUMBER vs ROWNUM</a>
 */
public class Oracle9SqlGenerator extends DefaultSqlGenerator {

    @Override
    public boolean isCompatible(DatabaseMetaData metadata) throws SQLException {
        return "Oracle".equals(metadata.getDatabaseProductName());
    }

    @Override
    public String limit(final String query, final Pageable page) {
        String sortClause = orderByExpression(page.getSort());
        return String.format("SELECT t2__.* FROM ( "
                                     + "SELECT t1__.*, ROWNUM as rn__ FROM ( %s%s ) t1__ "
                                     + ") t2__ WHERE t2__.rn__ > %d AND ROWNUM <= %d",
                             query, sortClause, page.getOffset(), page.getPageSize());
    }

}
