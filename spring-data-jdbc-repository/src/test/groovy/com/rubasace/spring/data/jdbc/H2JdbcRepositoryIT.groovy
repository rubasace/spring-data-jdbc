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
import com.rubasace.spring.data.repository.sql.LimitOffsetSqlGenerator
import groovy.transform.AnnotationCollector
import org.h2.jdbcx.JdbcDataSource
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.test.context.ContextConfiguration
import org.springframework.transaction.annotation.EnableTransactionManagement
import spock.lang.Requires

import javax.sql.DataSource

import static TestUtils.env

@H2TestContext
class H2JdbcRepositoryCompoundPkIT extends JdbcRepositoryCompoundPkIT {}

@H2TestContext
class H2JdbcRepositoryGeneratedKeyIT extends JdbcRepositoryGeneratedKeyIT {}

@H2TestContext
class H2JdbcRepositoryManualKeyIT extends JdbcRepositoryManualKeyIT {}

@H2TestContext
class H2JdbcRepositoryManyToOneIT extends JdbcRepositoryManyToOneIT {}

@H2TestContext
class H2SqlGeneratorFactoryIT extends SqlGeneratorFactoryIT {
    Class getExpectedGenerator() { LimitOffsetSqlGenerator }
}

@AnnotationCollector
@Requires({ env('CI') ? env('DB').equals('embedded') : true })
@ContextConfiguration(classes = H2TestConfig)
@interface H2TestContext {}

@Configuration
@EnableTransactionManagement
class H2TestConfig extends AbstractTestConfig {

    @Bean
    DataSource dataSource() {
        new JdbcDataSource(
                url: "jdbc:h2:mem:DB_CLOSE_DELAY=-1;INIT=RUNSCRIPT FROM 'classpath:schema_h2.sql'"
        )
    }
}
