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
package com.rubasace.spring.data.jdbc.config

import com.rubasace.spring.data.jdbc.fixtures.BoardingPassRepository
import com.rubasace.spring.data.jdbc.fixtures.CommentRepository
import com.rubasace.spring.data.jdbc.fixtures.CommentWithUserRepository
import com.rubasace.spring.data.jdbc.fixtures.UserRepository
import com.rubasace.spring.data.repository.sql.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.core.io.ClassPathResource
import org.springframework.core.io.ResourceLoader
import org.springframework.jdbc.datasource.DataSourceTransactionManager
import org.springframework.jdbc.datasource.init.DataSourceInitializer
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator
import org.springframework.transaction.PlatformTransactionManager

import javax.sql.DataSource

abstract class AbstractTestConfig {

    static final String DATABASE_NAME = 'spring_data_jdbc_repository_test'

    @Autowired
    ResourceLoader resourceLoader

    @Autowired
    List<SqlGenerator> generators;


    @Bean
    abstract DataSource dataSource()

    @Bean
    SqlGeneratorFactory sqlGeneratorFactory() {
        return new SqlGeneratorFactory(generators);
    }


    def getInitSqlScript() {
    }

    @Bean
    dataSourceInitializer() {
        if (!initSqlScript) {
            return null
        }
        new DataSourceInitializer(
                dataSource: dataSource(),
                databasePopulator: new ResourceDatabasePopulator(
                        scripts: new ClassPathResource(initSqlScript)
                )
        )
    }

    @Bean
    PlatformTransactionManager transactionManager() {
        new DataSourceTransactionManager(dataSource())
    }

    @Bean
    CommentRepository commentRepository() {
        new CommentRepository(sqlGeneratorFactory())
    }

    @Bean
    UserRepository userRepository() {
        new UserRepository(sqlGeneratorFactory())
    }

    @Bean
    BoardingPassRepository boardingPassRepository() {
        new BoardingPassRepository(sqlGeneratorFactory())
    }

    @Bean
    CommentWithUserRepository commentWithUserRepository() {
        new CommentWithUserRepository(sqlGeneratorFactory())
    }

    @Bean
    DefaultSqlGenerator defaultSqlGenerator() {
        return new DefaultSqlGenerator();
    }

    @Bean
    LimitOffsetSqlGenerator limitOffsetSqlGenerator() {
        return new LimitOffsetSqlGenerator();
    }

    @Bean
    Oracle9SqlGenerator oracle9SqlGenerator() {
        return new Oracle9SqlGenerator();
    }

    @Bean
    SQL2008SqlGenerator sql2008SqlGenerator() {
        return new SQL2008SqlGenerator();
    }


}
