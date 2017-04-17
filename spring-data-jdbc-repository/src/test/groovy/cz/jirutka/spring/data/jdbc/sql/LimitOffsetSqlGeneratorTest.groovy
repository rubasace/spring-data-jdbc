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
//import cz.jirutka.spring.data.jdbc.TableDescription
//import org.springframework.data.domain.Pageable
//
//class LimitOffsetSqlGeneratorTest extends SqlGeneratorTest {
//
//    def sqlGenerator = new LimitOffsetSqlGenerator()
//
//
//    @Override expectedPaginatedQuery(TableDescription table, Pageable page) {
//        def orderBy = page.sort ? orderBy(page.sort) + ' ' : ''
//
//        "SELECT a, b FROM tabx ${orderBy}LIMIT ${page.pageSize} OFFSET ${page.offset}"
//    }
//}
