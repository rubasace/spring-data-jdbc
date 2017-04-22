/*
 * Copyright 2012-2014 Tomasz Nurkiewicz <nurkiewicz@gmail.com>.
 * Copyright 2016 Jakub Jirutka <jakub@jirutka.cz>.
 *
 * Licensed under the Apache License, Version 2.0 (the 'License')
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

import com.rubasace.spring.data.jdbc.fixtures.User
import com.rubasace.spring.data.jdbc.fixtures.UserRepository
import org.h2.jdbcx.JdbcDataSource
import org.springframework.jdbc.datasource.DataSourceTransactionManager
import org.springframework.transaction.TransactionStatus
import org.springframework.transaction.support.TransactionTemplate
import spock.lang.Specification

class StandaloneUsageIT extends Specification {

    final JDBC_URL = "jdbc:h2:mem:DB_CLOSE_DELAY=-1;INIT=RUNSCRIPT FROM 'classpath:schema_h2.sql'"

    def dataSource = new JdbcDataSource(url: JDBC_URL)
    def repository = new UserRepository(dataSource: dataSource)


    def setup() {
        repository.afterPropertiesSet()
    }

    def cleanup() {
        repository.deleteAll()
    }


    def 'start repository without Spring'() {
        expect:
        repository.findAll().isEmpty()
    }

    def 'insert into database'() {
        given:
        def tx = new TransactionTemplate(new DataSourceTransactionManager(dataSource))
        when:
        def users = tx.execute { TransactionStatus status ->
            def user = new User('john', new Date(), 0, false)
            repository.save(user)
            repository.findAll()
        }
        then:
        users.size() == 1
    }
}
