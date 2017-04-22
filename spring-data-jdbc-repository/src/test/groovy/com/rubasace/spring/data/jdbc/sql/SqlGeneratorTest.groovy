package com.rubasace.spring.data.jdbc.sql
///*
// * Copyright 2012-2014 Tomasz Nurkiewicz <nurkiewicz@gmail.com>.
// * Copyright 2016 Jakub Jirutka <jakub@jirutka.cz>.
// *
// * Licensed under the Apache License, Version 2.0 (the 'License')
// * you may not use this file except in compliance with the License.
// * You may obtain a copy of the License at
// *
// *     http://www.apache.org/licenses/LICENSE-2.0
// *
// * Unless required by applicable law or agreed to in writing, software
// * distributed under the License is distributed on an 'AS IS' BASIS,
// * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// * See the License for the specific language governing permissions and
// * limitations under the License.
// */
//package cz.jirutka.spring.data.jdbc.sql
//
//import TableDescription
//import org.springframework.data.domain.PageRequest
//import org.springframework.data.domain.Pageable
//import org.springframework.data.domain.Sort
//import org.springframework.data.domain.Sort.Direction
//import org.springframework.data.domain.Sort.Order
//import spock.lang.Specification
//import spock.lang.Unroll
//
//import static org.springframework.data.domain.Sort.Direction.ASC
//import static org.springframework.data.domain.Sort.Direction.DESC
//
//@Unroll
//class SqlGeneratorTest extends Specification {
//
//    final ANY = new Object()
//
//    def table = new TableDescription (
//        tableName: 'tab',
//        selectClause: 'a, b',
//        fromClause: 'tabx',
//        pkColumns: ['tid']
//    )
//
//    def getSqlGenerator() { new DefaultSqlGenerator() }
//
//
//    def 'count()'() {
//        expect:
//            sqlGenerator.count(table) == 'SELECT count(*) FROM tabx'
//    }
//
//
//    def 'deleteAll()'() {
//        expect:
//            sqlGenerator.deleteAll(table) == 'DELETE FROM tab'
//    }
//
//
//    def 'deleteById(): with #desc'() {
//        setup:
//            table.pkColumns = pkColumns(pkSize)
//        expect:
//            sqlGenerator.deleteById(table) == "DELETE FROM tab WHERE ${pkPredicate(pkSize)}"
//        where:
//            pkSize || desc
//            1      || 'simple PK'
//            3      || 'composite PK'
//    }
//
//
//    def 'deleteByIds(): with idsCount = #idsCount'() {
//        when:
//            sqlGenerator.deleteByIds(table, idsCount)
//        then:
//            thrown IllegalArgumentException
//        where:
//            idsCount << [0, -1]
//    }
//
//    def 'deleteByIds(): when simple PK and given #desc'() {
//        expect:
//            sqlGenerator.deleteByIds(table, idsCount) == "DELETE FROM tab WHERE ${whereClause}"
//        where:
//            idsCount || whereClause        | desc
//            1        || 'tid = ?'          | 'one id'
//            3        || 'tid IN (?, ?, ?)' | 'several ids'
//    }
//
//    def 'deleteByIds(): when composite PK and given #desc'() {
//        setup:
//            table.pkColumns = pkColumns(2)
//        expect:
//            sqlGenerator.deleteByIds(table, idsCount) == "DELETE FROM tab WHERE ${whereClause}"
//        where:
//            idsCount || whereClause                                  | desc
//            1        || pkPredicate(2)                               | 'one id'
//            2        || "(${pkPredicate(2)}) OR (${pkPredicate(2)})" | 'several ids'
//    }
//
//
//    def 'existsById(): with #desc'() {
//        setup:
//            table.pkColumns = pkColumns(pkSize)
//        expect:
//            sqlGenerator.existsById(table) == "SELECT 1 FROM tab WHERE ${pkPredicate(pkSize)}"
//        where:
//            pkSize || desc
//            1      || 'simple PK'
//            3      || 'composite PK'
//    }
//
//
//    def 'insert()'() {
//        when:
//            def actual = sqlGenerator.insert(table, [x: ANY, y: ANY, z: ANY])
//        then:
//            actual == 'INSERT INTO tab (x, y, z) VALUES (?, ?, ?)'
//    }
//
//
//    def 'selectAll()'() {
//        expect:
//            sqlGenerator.selectAll(table) == 'SELECT a, b FROM tabx'
//    }
//
//    def "selectAll(Pageable): #desc"() {
//        setup:
//            table.pkColumns = pkColumns(pkSize)
//            def expected = expectedPaginatedQuery(table, pageable)
//        expect:
//            sqlGenerator.selectAll(table, pageable) == expected
//        where:
//            pkSize | pageable                      || desc
//            1      | page(0, 10)                   || 'when simple key and requested first page'
//            1      | page(20, 10)                  || 'when simple key and requested third page'
//            1      | page(0, 10, order(ASC, 'a'))  || 'when simple key and requested first page with sort'
//            3      | page(0, 10)                   || 'when composite key and requested first page'
//            3      | page(20, 10, order(ASC, 'a')) || 'when composite key and requested third page with sort'
//    }
//
//    def 'selectAll(Sort): #expected'() {
//        when:
//            def actual = sqlGenerator.selectAll(table, new Sort(orders))
//        then:
//            actual == "SELECT a, b FROM tabx ${expected}"
//        where:
//            orders                              || expected
//            [order(ASC, 'a')]                   || 'ORDER BY a ASC'
//            [order(DESC, 'a')]                  || 'ORDER BY a DESC'
//            [order(ASC, 'a'), order(DESC, 'b')] || 'ORDER BY a ASC, b DESC'
//    }
//
//
//    def 'selectById(): with #desc'() {
//        setup:
//            table.pkColumns = pkColumns(pkSize)
//        expect:
//            sqlGenerator.selectById(table) == "SELECT a, b FROM tabx WHERE ${pkPredicate(pkSize)}"
//        where:
//            pkSize || desc
//            1      || 'simple PK'
//            3      || 'composite PK'
//    }
//
//
//    def 'selectByIds(): when simple PK and given #desc'() {
//        expect:
//            sqlGenerator.selectByIds(table, idsCount) == "SELECT a, b FROM tabx${expected}"
//        where:
//            idsCount || expected                  | desc
//            0        || ''                        | 'no id'
//            1        || ' WHERE tid = ?'          | 'one id'
//            2        || ' WHERE tid IN (?, ?)'    | 'two ids'
//            3        || ' WHERE tid IN (?, ?, ?)' | 'several ids'
//    }
//
//    def 'selectByIds(): when composite PK and given #desc'() {
//        setup:
//            table.pkColumns = pkColumns(3)
//            expected = expected.replaceAll('%1', pkPredicate(3))
//        when:
//            def actual = sqlGenerator.selectByIds(table, idsCount)
//        then:
//            actual == "SELECT a, b FROM tabx${expected}"
//        where:
//            idsCount || expected                      | desc
//            0        || ''                            | 'no id'
//            1        || ' WHERE %1'                   | 'one id'
//            2        || ' WHERE (%1) OR (%1)'         | 'two ids'
//            3        || ' WHERE (%1) OR (%1) OR (%1)' | 'several ids'
//    }
//
//
//    def 'update(): with #desc'() {
//        setup:
//            table.pkColumns = pkColumns(idsCount)
//        when:
//            def actual = sqlGenerator.update(table, [x: ANY, y: ANY, z: ANY])
//        then:
//            actual == "UPDATE tab SET x = ?, y = ?, z = ? WHERE ${pkPredicate(idsCount)}"
//        where:
//            idsCount || desc
//            1        || 'simple PK'
//            2        || 'composite PK'
//    }
//
//
//    def expectedPaginatedQuery(TableDescription table, Pageable page) {
//
//        // If sort is not specified, then it should be sorted by primary key columns.
//        def sort = page.sort ?: new Sort(ASC, table.pkColumns)
//
//        def firstIndex = page.offset + 1
//        def lastIndex = page.offset + page.pageSize
//
//        """
//            SELECT t2__.* FROM (
//                SELECT row_number() OVER (${orderBy(sort)}) AS rn__, t1__.* FROM (
//                    SELECT ${table.selectClause} FROM ${table.fromClause}
//                ) t1__
//            ) t2__ WHERE t2__.rn__ BETWEEN ${firstIndex} AND ${lastIndex}
//        """.trim().replaceAll(/\s+/, ' ')
//    }
//
//
//    def page(int offset, int limit, Order... orders) {
//        def sort = orders.length > 0 ? new Sort(orders) : null
//        new PageRequest(offset / limit as int, limit, sort)
//    }
//
//    def order(Direction dir, String property) {
//        new Order(dir, property)
//    }
//
//    def orderBy(Sort sort) {
//        'ORDER BY ' + sort.collect { "${it.property} ${it.direction.name()}" }.join(', ')
//    }
//
//    static pkColumns(count) {
//        (1..count).collect { "id${it}" }*.toString()
//    }
//
//    static pkPredicate(count) {
//        pkColumns(count).collect { "$it = ?" }.join(' AND ')
//    }
//}
