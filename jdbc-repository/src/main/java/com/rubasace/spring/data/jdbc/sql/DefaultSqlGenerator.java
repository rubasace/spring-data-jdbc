/*
 * Copyright 2012-2014 Tomasz Nurkiewicz <nurkiewicz@gmail.com>.
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
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;

import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.Iterator;

import static java.lang.String.format;

//TODO do specific implementations

/**
 * SQL Generator compatible with SQL:99.
 */
public class DefaultSqlGenerator implements SqlGenerator {

    static final String AND = " AND ", OR = " OR ", COMMA = ", ", NOT_EQUAL = " <> ",
            EQUAL = " = ", PARAM_START = ":";

    public boolean isCompatible(DatabaseMetaData metadata) throws SQLException {
        return true;
    }

    public String select(String table) {
        return select(table, false);
    }

    public String select(String table, boolean distinct) {
        return format("SELECT %s%s FROM %s", distinct ? "DISTINCT " : "", "*", table);
    }

    public String count(String table) {
        return format("SELECT count(*) FROM %s", table);
    }

    public String delete(String table) {
        return format("DELETE FROM %s", table);
    }

    public String where() {
        return " WHERE ";
    }

    public String or(String left, String right) {
        return left + OR + right;
    }

    public String and(String left, String right) {
        return left + AND + right;
    }

    public String compareAttribute(String attribute) {
        return compareAttribute(attribute, false);
    }

    public String compareAttribute(String attribute, boolean negating) {
        return attribute + (negating ? NOT_EQUAL : EQUAL)
                + generateNamedParameter(attribute);
    }

    @Override
    public String between(String attribute) {
        return between(attribute, false);
    }

    @Override
    public String between(String attribute, boolean negating) {
        String start = negating ? " NOT " : "";
        String lower = generateNamedParameter("_lOwEr_" + attribute);
        String greater = generateNamedParameter("_gReAtEr_" + attribute);
        return String.format(" %s%s BETWEEN %s AND %s", attribute, start, lower, greater);
    }

    @Override
    public String gt(String attribute) {
        return compare(attribute, ">");
    }

    protected String compare(String attribute, String comparator) {
        return String.format(" %s %s %s", attribute, comparator,
                             generateNamedParameter(attribute));
    }

    @Override
    public String ge(String attribute) {
        return compare(attribute, ">=");
    }

    @Override
    public String lt(String attribute) {
        return compare(attribute, "<");
    }

    @Override
    public String le(String attribute) {
        return compare(attribute, "<=");
    }

    @Override
    public String isNull(String attribute) {
        return String.format(" %s IS NULL", attribute);
    }

    @Override
    public String isNotNull(String attribute) {
        return String.format(" %s IS NOT NULL", attribute);
    }

    // TODO review blank spaces
    @Override
    public String like(String attribute, boolean negating) {
        String start = negating ? "NOT " : "";
        return String.format(" %s %slike %s", attribute, start,
                             generateNamedParameter(attribute));
    }

    @Override
    public String notLike(String attribute) {
        return like(attribute, true);
    }

    @Override
    public String isTrue(String attribute) {
        return String.format(" %s IS TRUE", attribute);
    }

    @Override
    public String isFalse(String attribute) {
        return String.format(" %s IS FALSE", attribute);
    }

    @Override
    public String in(String attribute, boolean negating) {
        String start = negating ? " NOT " : "";
        return String.format(" %s%s IN (%s)", attribute, start,
                             generateNamedParameter(attribute));
    }

    @Override
    public String notIn(String attribute) {
        return in(attribute, true);
    }

    public String sort(String select, Sort sort) {
        return select + orderByExpression(sort);
    }

    public String limit(String query, Pageable page) {
        String sortClause = orderByExpression(page.getSort());
        return format(
                "SELECT t2__.* FROM ( "
                        + "SELECT row_number() OVER %s AS rn__, t1__.* FROM ( %s ) t1__ "
                        + ") t2__ WHERE t2__.rn__ BETWEEN %s AND %s",
                sortClause, query, page.getOffset() + 1,
                page.getOffset() + page.getPageSize());
    }

    protected String orderByExpression(Sort sort) {
        final String noSort = "";

        if (sort == null || !sort.iterator().hasNext()) {
            return noSort;
        }
        StringBuilder sb = new StringBuilder(" ORDER BY ");

        for (Iterator<Order> it = sort.iterator(); it.hasNext(); ) {
            Order order = it.next();
            sb.append(order.getProperty()).append(' ').append(order.getDirection());
            if (it.hasNext()) {
                sb.append(COMMA);
            }
        }
        return sb.toString();
    }

    // @Override
    // public String startingWith(String attribute) {
    // // TODO Auto-generated method stub
    // return null;
    // }
    //
    // @Override
    // public String endingWith(String attribute) {
    // // TODO Auto-generated method stub
    // return null;
    // }
    //
    // @Override
    // public String containing(String attribute) {
    // // TODO Auto-generated method stub
    // return null;
    // }
    //
    // @Override
    // public String notContaining(String attribute) {
    // // TODO Auto-generated method stub
    // return null;
    // }
    //
    protected String generateNamedParameter(String attribute) {
        return PARAM_START + attribute;
    }

}