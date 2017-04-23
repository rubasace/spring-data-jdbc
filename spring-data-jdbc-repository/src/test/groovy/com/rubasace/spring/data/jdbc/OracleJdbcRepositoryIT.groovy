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

import com.rubasace.spring.data.jdbc.config.AbstractTestConfig
import com.rubasace.spring.data.jdbc.sql.SqlGeneratorFactoryIT
import com.rubasace.spring.data.repository.sql.Oracle9SqlGenerator
import com.zaxxer.hikari.HikariDataSource
import groovy.transform.AnnotationCollector
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.test.context.ContextConfiguration
import org.springframework.transaction.annotation.EnableTransactionManagement
import spock.lang.Requires

import javax.sql.DataSource

import static OracleTestConfig.ORACLE_HOST
import static TestUtils.env
import static TestUtils.isPortInUse

@OracleTestContext
class OracleJdbcRepositoryCompoundPkIT extends JdbcRepositoryCompoundPkIT {}

@OracleTestContext
class OracleJdbcRepositoryGeneratedKeyIT extends JdbcRepositoryGeneratedKeyIT {}

@OracleTestContext
class OracleJdbcRepositoryManualKeyIT extends JdbcRepositoryManualKeyIT {}

@OracleTestContext
class OracleJdbcRepositoryManyToOneIT extends JdbcRepositoryManyToOneIT {}

@OracleTestContext
class OracleSqlGeneratorFactoryIT extends SqlGeneratorFactoryIT {
    Class getExpectedGenerator() { Oracle9SqlGenerator }
}

@AnnotationCollector
@Requires({ env('CI') ? env('DB').equals('oracle') : isPortInUse(ORACLE_HOST, 1521) })
@ContextConfiguration(classes = OracleTestConfig)
@interface OracleTestContext {}

@Configuration
@EnableTransactionManagement
class OracleTestConfig extends AbstractTestConfig {

    static final String ORACLE_HOST = env('ORACLE_HOST', 'localhost')


    @Bean(destroyMethod = 'shutdown')
    def DataSource dataSource() {
        new HikariDataSource(
                dataSourceClassName: 'oracle.jdbc.pool.OracleDataSource',
                dataSourceProperties: [
                        driverType : 'thin',
                        serverName : ORACLE_HOST,
                        portNumber : 1521,
                        serviceName: env('ORACLE_SID', 'XE'),
                        user       : env('ORACLE_USER', 'test'),
                        password   : env('ORACLE_PASSWORD', 'test')
                ]
        )
    }
}
