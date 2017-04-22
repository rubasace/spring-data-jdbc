/*
 * Copyright 2016 Jakub Jirutka <jakub@jirutka.cz>.
 *
 * Licensed under the Apache License, Version 2.0 (the 'License');
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an 'AS IS' BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.rubasace.spring.data.jdbc

import com.zaxxer.hikari.HikariDataSource
import com.rubasace.spring.data.jdbc.config.AbstractTestConfig
import com.rubasace.spring.data.jdbc.sql.SQL2008SqlGenerator
import com.rubasace.spring.data.jdbc.sql.SqlGeneratorFactoryIT
import groovy.transform.AnnotationCollector
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.test.context.ContextConfiguration
import org.springframework.transaction.annotation.EnableTransactionManagement
import spock.lang.Requires

import javax.sql.DataSource

import static Mssql2012TestConfig.MSSQL_HOST
import static TestUtils.env
import static TestUtils.isPortInUse

@Mssql2012TestContext
class Mssql2012JdbcRepositoryCompoundPkIT extends JdbcRepositoryCompoundPkIT {}

@Mssql2012TestContext
class Mssql2012JdbcRepositoryGeneratedKeyIT extends JdbcRepositoryGeneratedKeyIT {}

@Mssql2012TestContext
class Mssql2012JdbcRepositoryManualKeyIT extends JdbcRepositoryManualKeyIT {}

@Mssql2012TestContext
class Mssql2012JdbcRepositoryManyToOneIT extends JdbcRepositoryManyToOneIT {}

@Mssql2012TestContext
class Mssql2012SqlGeneratorFactoryIT extends SqlGeneratorFactoryIT {
    Class getExpectedGenerator() { SQL2008SqlGenerator }
}

@AnnotationCollector
@Requires({ env('CI') ? env('DB').equals('mssql') : isPortInUse(MSSQL_HOST, 1433) })
@ContextConfiguration(classes = Mssql2012TestConfig)
@interface Mssql2012TestContext {}

@Configuration
@EnableTransactionManagement
class Mssql2012TestConfig extends AbstractTestConfig {

    static final String MSSQL_HOST = env('MSSQL_HOST', 'localhost')

    final initSqlScript = 'schema_mssql.sql'


    @Bean(destroyMethod = 'shutdown')
    DataSource dataSource() {
        new HikariDataSource(
                dataSourceClassName: 'net.sourceforge.jtds.jdbcx.JtdsDataSource',
                connectionTestQuery: 'select 1',
                dataSourceProperties: [
                        serverName  : MSSQL_HOST,
                        instance    : env('MSSQL_INSTANCE', 'SQL2012SP1'),
                        user        : env('MSSQL_USER', 'sa'),
                        password    : env('MSSQL_PASSWORD', 'Password12!'),
                        databaseName: AbstractTestConfig.DATABASE_NAME
                ]
        )
    }
}
