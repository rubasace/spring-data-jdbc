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

import java.sql.DatabaseMetaData;
import java.sql.SQLException;

public interface SqlGenerator {

    /**
     * This method is used by {@link SqlGeneratorFactory} to select a right
     * SQL Generator.
     *
     * @param metadata The database metadata.
     * @return Whether is this generator compatible with the database described
     * by the given {@code metadata}.
     */
    boolean isCompatible(DatabaseMetaData metadata) throws SQLException;

    //QUERIES

    String select(String table);

    String select(String table, boolean distinct);

    String count(String table);

    //    String count(String table, boolean distinct);

    String delete(String table);

    String where();

    //CONCATENATION

    String or(String left, String right);

    String and(String left, String right);

    //OPERATIONS

    String compareAttribute(String attribute);

    String compareAttribute(String attribute, boolean negating);

    String between(String attribute);

    String between(String attribute, boolean negating);

    String gt(String attribute);

    String ge(String attribute);

    String lt(String attribute);

    String le(String attribute);

    String isNull(String attribute);

    String isNotNull(String attribute);

    //TODO change for negating in notLike
    String like(String attribute, boolean negating);

    String notLike(String attribute);

    String isTrue(String attribute);

    String isFalse(String attribute);


    String in(String attribute, boolean negating);

    String notIn(String attribute);

    //    String startingWith(String attribute);
    //
    //    String endingWith(String attribute);
    //
    //    String containing(String attribute);
    //
    //    String notContaining(String attribute);
    //

    //QUERY MODIFIERS


    String sort(String query, Sort sort);

    String limit(String query, Pageable page);


}