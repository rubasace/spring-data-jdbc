/*
 *
 *  * Copyright (C) 2017 Ruben Pahino Verdugo <ruben.pahino.verdugo@gmail.com>
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  * you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  * http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 */
package com.rubasace.spring.data.repository.sql;

import com.rubasace.spring.data.repository.TableDescription;
import com.rubasace.spring.data.repository.internal.StringUtils;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.domain.Sort.Order;
import org.springframework.util.Assert;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static java.lang.String.format;
import static org.springframework.util.StringUtils.collectionToDelimitedString;

/**
 * SQL Generator compatible with SQL:99.
 */
public abstract class AbstractSqlGenerator implements SqlGenerator {

    static final String
            SELECT = "SELECT ",
            WHERE = " WHERE ",
            AND = " AND ",
            OR = " OR ",
            COMMA = ", ",
            PARAM = " = ?";

    private static final String EMPTY = "";

    public String selectBySelectClauseAndWhereClause(String selectClause, String whereClause) {
        return selectByClause(selectClause) + whereByClause(whereClause);
    }

    protected String selectByClause(String selectClause) {
        final StringBuilder clause = new StringBuilder(SELECT);
        return clause.append(selectClause).toString();
    }

    public String count(TableDescription table) {
        return format("SELECT count(*) FROM %s", table.getFromClause());
    }

    protected String whereByClause(String whereClause) {
        if (whereClause == null || whereClause.isEmpty()) {
            return EMPTY;
        }
        final StringBuilder clause = new StringBuilder(WHERE);
        return clause.append(whereClause).toString();
    }

    @Override
    public int getOrder() {
        return 0;
    }

    public String count(TableDescription table, String whereClause) {
        return count(table) + whereByClause(whereClause);
    }

    public String deleteAll(TableDescription table) {
        return format("DELETE FROM %s", table.getTableName());
    }

    public String deleteById(TableDescription table) {
        return deleteByIds(table, 1);
    }

    public String deleteByIds(TableDescription table, int idsCount) {
        return deleteAll(table) + whereByClause(idsPredicate(table, idsCount));
    }

    public String deleteByWhereClause(TableDescription table, String whereClause) {
        return deleteAll(table) + whereByClause(whereClause);
    }

    public String existsById(TableDescription table) {
        return format("SELECT 1 FROM %s WHERE %s", table.getTableName(), idPredicate(table));
    }

    public String insert(TableDescription table, Map<String, Object> columns) {

        return String.format("INSERT INTO %s (%s) VALUES (%s)", table.getTableName(),
                             collectionToDelimitedString(columns.keySet(), COMMA), StringUtils.repeat("?", COMMA, columns.size()));
    }

    public String selectAll(TableDescription table) {
        return format("SELECT %s FROM %s", table.getSelectClause(), table.getFromClause());
    }

    public String selectAll(TableDescription table, String whereClause) {
        return selectAll(table) + whereByClause(whereClause);
    }

    public String selectAll(TableDescription table, String whereClause, Pageable page) {
        Sort sort = page.getSort() != null ? page.getSort() : sortById(table);

        return format(
                "SELECT t2__.* FROM ( "
                        + "SELECT row_number() OVER (ORDER BY %s) AS rn__, t1__.* FROM ( %s ) t1__ "
                        + ") t2__ WHERE t2__.rn__ BETWEEN %s AND %s",
                orderByExpression(sort), selectAll(table, whereClause), page.getOffset() + 1,
                page.getOffset() + page.getPageSize());
    }

    public String selectAll(TableDescription table, String whereClause, Sort sort) {
        return selectAll(table, whereClause) + (sort != null ? orderByClause(sort) : "");
    }

    public String selectById(TableDescription table) {
        return selectByIds(table, 1);
    }

    public String selectByIds(TableDescription table, int idsCount) {
        return idsCount > 0 ? selectAll(table) + whereByClause(idsPredicate(table, idsCount))
                : selectAll(table);
    }

    public String update(TableDescription table, Map<String, Object> columns) {

        return format("UPDATE %s SET %s WHERE %s", table.getTableName(),
                      formatParameters(columns.keySet(), COMMA), idPredicate(table));
    }

    protected String orderByClause(Sort sort) {
        return " ORDER BY " + orderByExpression(sort);
    }

    protected Sort sortById(TableDescription table) {
        return new Sort(Direction.ASC, table.getPkColumns());
    }

    protected String orderByExpression(Sort sort) {
        StringBuilder sb = new StringBuilder();

        for (Iterator<Order> it = sort.iterator(); it.hasNext(); ) {
            Order order = it.next();
            sb.append(order.getProperty()).append(' ').append(order.getDirection());

            if (it.hasNext()) {
                sb.append(COMMA);
            }
        }
        return sb.toString();
    }

    private String idsPredicate(TableDescription table, int idsCount) {
        Assert.isTrue(idsCount > 0, "idsCount must be greater than zero");

        List<String> idColumnNames = table.getPkColumns();

        if (idsCount == 1) {
            return idPredicate(table);
        } else if (idColumnNames.size() > 1) {
            return StringUtils.repeat("(" + formatParameters(idColumnNames, AND) + ")", OR, idsCount);
        } else {
            return idColumnNames.get(0) + " IN (" + StringUtils.repeat("?", COMMA, idsCount) + ")";
        }
    }

    private String idPredicate(TableDescription table) {
        return formatParameters(table.getPkColumns(), AND);
    }

    private String formatParameters(Collection<String> columns, String delimiter) {
        return collectionToDelimitedString(columns, delimiter, "", PARAM);
    }
}
