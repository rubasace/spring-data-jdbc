package com.rubasace.spring.data.jdbc.sql
///*
// * Copyright 2016 Jakub Jirutka <jakub@jirutka.cz>.
// *
// * Licensed under the Apache License, Version 2.0 (the "License");
// * you may not use this file except in compliance with the License.
// * You may obtain a copy of the License at
// *
// *     http://www.apache.org/licenses/LICENSE-2.0
// *
// * Unless required by applicable law or agreed to in writing, software
// * distributed under the License is distributed on an "AS IS" BASIS,
// * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// * See the License for the specific language governing permissions and
// * limitations under the License.
// */
//package cz.jirutka.spring.data.jdbc.sql
//
//import TableDescription
//import org.springframework.data.domain.Pageable
//import org.springframework.data.domain.Sort
//
//import static org.springframework.data.domain.Sort.Direction.ASC
//
//class SQL2008SqlGeneratorTest extends SqlGeneratorTest {
//
//    def sqlGenerator = new SQL2008SqlGenerator()
//
//
//    @Override
//    expectedPaginatedQuery(TableDescription table, Pageable page) {
//
//        // If sort is not specified, then it should be sorted by primary key columns.
//        def sort = page.sort ?: new Sort(ASC, table.pkColumns)
//
//        """
//            SELECT a, b FROM tabx ${orderBy(sort)}
//            OFFSET ${page.offset} ROWS FETCH NEXT ${page.pageSize} ROW ONLY
//        """.trim().replaceAll(/\s+/, ' ')
//    }
//}
