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
package cz.jirutka.spring.data.jdbc

import com.mysql.jdbc.jdbc2.optional.MysqlConnectionPoolDataSource
import cz.jirutka.spring.data.jdbc.config.AbstractTestConfig
import cz.jirutka.spring.data.jdbc.sql.LimitOffsetSqlGenerator
import cz.jirutka.spring.data.jdbc.sql.SqlGeneratorFactoryIT
import groovy.transform.AnnotationCollector
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.test.context.ContextConfiguration
import org.springframework.transaction.annotation.EnableTransactionManagement
import spock.lang.Requires

import javax.sql.DataSource

import static MySqlTestConfig.MYSQL_HOST
import static TestUtils.env
import static TestUtils.isPortInUse

@MySqlTestContext
class MySqlJdbcRepositoryCompoundPkIT extends JdbcRepositoryCompoundPkIT {}

@MySqlTestContext
class MySqlJdbcRepositoryGeneratedKeyIT extends JdbcRepositoryGeneratedKeyIT {}

@MySqlTestContext
class MySqlJdbcRepositoryManualKeyIT extends JdbcRepositoryManualKeyIT {}

@MySqlTestContext
class MySqlJdbcRepositoryManyToOneIT extends JdbcRepositoryManyToOneIT {}

@MySqlTestContext
class MySqlSqlGeneratorFactoryIT extends SqlGeneratorFactoryIT {
    Class getExpectedGenerator() { LimitOffsetSqlGenerator }
}

@AnnotationCollector
@Requires({ env('CI') ? env('DB').equals('mysql') : isPortInUse(MYSQL_HOST, 3306) })
@ContextConfiguration(classes = MySqlTestConfig)
@interface MySqlTestContext {}

@Configuration
@EnableTransactionManagement
class MySqlTestConfig extends AbstractTestConfig {

    static final String MYSQL_HOST = env('MYSQL_HOST', 'localhost')

    final initSqlScript = 'schema_mysql.sql'


    @Bean
    DataSource dataSource() {
        new MysqlConnectionPoolDataSource(
                serverName: MYSQL_HOST,
                user: env('MYSQL_USER', 'root'),
                password: env('MYSQL_PASSWORD', ''),
                databaseName: DATABASE_NAME
        )
    }
}
