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
package com.rubasace.spring.data.jdbc

import com.rubasace.spring.data.jdbc.fixtures.CommentWithUserRepository
import com.rubasace.spring.data.repository.TableDescription
import groovy.transform.AnnotationCollector
import org.springframework.context.annotation.Configuration
import org.springframework.test.context.ContextConfiguration
import org.springframework.transaction.annotation.EnableTransactionManagement
import spock.lang.Requires

import static MssqlTestConfig.MSSQL_HOST
import static TestUtils.env
import static TestUtils.isPortInUse

@MssqlTestContext
class MssqlJdbcRepositoryCompoundPkIT extends JdbcRepositoryCompoundPkIT {}

@MssqlTestContext
class MssqlJdbcRepositoryGeneratedKeyIT extends JdbcRepositoryGeneratedKeyIT {}

@MssqlTestContext
class MssqlJdbcRepositoryManualKeyIT extends JdbcRepositoryManualKeyIT {}

@MssqlTestContext
class MssqlJdbcRepositoryManyToOneIT extends JdbcRepositoryManyToOneIT {}

@AnnotationCollector
@Requires({ env('CI') ? env('DB').equals('mssql') : isPortInUse(MSSQL_HOST, 1433) })
@ContextConfiguration(classes = MssqlTestConfig)
@interface MssqlTestContext {}

@Configuration
@EnableTransactionManagement
class MssqlTestConfig extends Mssql2012TestConfig {

    @Override
    CommentWithUserRepository commentWithUserRepository() {
        new CommentWithUserRepository(
                new TableDescription(
                        tableName: 'COMMENTS',
                        selectClause: 'c.*, u.date_of_birth, u.reputation, u.enabled',
                        fromClause: 'COMMENTS c JOIN USERS u ON c.USER_NAME = u.USER_NAME',
                        pkColumns: ['ID']
                )
        )
    }
}
