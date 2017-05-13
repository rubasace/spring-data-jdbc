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
//class Oracle9SqlGeneratorTest extends SqlGeneratorTest {
//
//    def sqlGenerator = new Oracle9SqlGenerator()
//
//
//    @Override expectedPaginatedQuery(TableDescription tableDescription, Pageable page) {
//
//        // If sort is not specified, then it should be sorted by primary key columns.
//        def sort = page.sort ?: new Sort(ASC, tableDescription.pkColumns)
//
//        """
//            SELECT t2__.* FROM (
//                SELECT t1__.*, ROWNUM as rn__ FROM (
//                    SELECT ${tableDescription.selectClause} FROM ${tableDescription.fromClause} ${orderBy(sort)}
//                ) t1__
//            ) t2__ WHERE t2__.rn__ > ${page.offset} AND ROWNUM <= ${page.pageSize}
//        """.trim().replaceAll(/\s+/, ' ')
//    }
//}
