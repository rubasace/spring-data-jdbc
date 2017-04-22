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
package cz.jirutka.spring.data.jdbc

import com.rubasace.spring.data.jdbc.NoRecordUpdatedException
import cz.jirutka.spring.data.jdbc.fixtures.CommentRepository
import cz.jirutka.spring.data.jdbc.fixtures.User
import cz.jirutka.spring.data.jdbc.fixtures.UserRepository
import org.springframework.dao.DuplicateKeyException
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.data.domain.Sort.Order
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.transaction.annotation.Transactional
import spock.lang.Specification
import spock.lang.Unroll

import javax.annotation.Resource
import javax.sql.DataSource
import java.sql.Date

import static org.springframework.data.domain.Sort.Direction.ASC
import static org.springframework.data.domain.Sort.Direction.DESC

@Unroll
@Transactional
abstract class JdbcRepositoryManualKeyIT extends Specification {

    @Resource
    UserRepository repository
    @Resource
    CommentRepository commentRepository
    @Resource
    DataSource dataSource

    final someDateOfBirth = new Date(new GregorianCalendar(2013, Calendar.JANUARY, 9).timeInMillis)

    final entities = [
            Ruby : new User('Ruby', someDateOfBirth, 40, true),
            Emma : new User('Emma', someDateOfBirth, 38, true),
            Drew : new User('Drew', someDateOfBirth, 40, true),
            Lucy : new User('Lucy', someDateOfBirth, 38, true),
            Mindy: new User('Mindy', someDateOfBirth, 42, true)
    ]

    JdbcTemplate jdbc


    def setup() {
        jdbc = new JdbcTemplate(dataSource)

        for (User user : entities.values()) {
            insertUser(user)
        }
        assert selectIds() == entities.keySet()
    }


    def 'findOne(ID): returns null when the table is empty'() {
        setup:
        deleteAllUsers()
        expect:
        repository.findOne('Emma') == null
    }

    def 'findOne(ID): returns null when record for given id does not exist'() {
        expect:
        repository.findOne('John') == null
    }

    def 'findOne(ID): returns entity for the given id'() {
        expect:
        repository.findOne('Drew') == entities['Drew']
    }


    def 'findAll(): returns empty list when the table is empty'() {
        setup:
        deleteAllUsers()
        expect:
        repository.findAll().empty
    }

    def 'findAll(): returns list of all entities in the table'() {
        expect:
        repository.findAll() as Set == entities.values() as Set
    }


    def 'findAll(Iterable<ID>): returns list of entities for the given ids'() {
        when:
        def results = repository.findAll(ids) as Set
        then:
        results == ids.collect(entities.&get) as Set
        where:
        ids << [[], ['Mindy'], ['Mindy', 'Ruby']]
    }


    def 'findAll(Sort): returns entities sorted by one column'() {
        when:
        def results = repository.findAll(new Sort(new Order(ASC, 'user_name')))
        then:
        results == ['Drew', 'Emma', 'Lucy', 'Mindy', 'Ruby'].collect(entities.&get)
    }

    def 'findAll(Sort): returns entities sorted by two columns'() {
        when:
        def results = repository.findAll(
                new Sort(new Order(DESC, 'reputation'), new Order(ASC, 'user_name')))
        then:
        results == ['Mindy', 'Drew', 'Ruby', 'Emma', 'Lucy'].collect(entities.&get)
    }


    def 'findAll(Pageable): returns empty page when the table is empty'() {
        setup:
        deleteAllUsers()
        when:
        def page = repository.findAll(new PageRequest(0, 20))
        then:
        !page.hasContent()
        page.totalElements == 0
        page.size == 20
        page.number == 0
    }

    def 'findAll(Pageable): returns paged entities'() {
        when:
        def page = repository.findAll(new PageRequest(pageNum, 3))
        then:
        page.totalElements == entities.size()
        page.size == 3
        page.number == pageNum
        page.content.size() == resultSize
        entities.values().containsAll(page.content)
        where:
        pageNum | resultSize
        0       | 3
        1       | 2
    }

    def 'findAll(Pageable): returns empty page when 2nd page requested, but only one record in table'() {
        setup:
        deleteAllUsers()
        insertUser entities['Mindy']
        when:
        def page = repository.findAll(new PageRequest(1, 5))
        then:
        page.content.size() == 0
        page.number == 1
        page.size == 5
        page.totalElements == 1
    }

    def 'findAll(Pageable): returns paged entities sorted by two columns'() {
        when:
        def page = repository.findAll(new PageRequest(pageNum, 3,
                new Sort(new Order(DESC, 'reputation'), new Order(ASC, 'user_name'))))
        then:
        page.number == pageNum
        page.size == 3
        page.totalElements == 5
        page.content == resultIds.collect(entities.&get)
        where:
        pageNum | resultIds
        0       | ['Mindy', 'Drew', 'Ruby']
        1       | ['Emma', 'Lucy']
    }


    def '#method(T): inserts the given new entity'() {
        setup:
        deleteAllUsers()
        when:
        repository./$method/(entities['Mindy'])
        then:
        selectUserById('Mindy') == entities['Mindy']
        where:
        method << ['save', 'insert']
    }

    def 'save(T): throws DuplicateKeyException when the given entity is marked as new, but already exists'() {
        when:
        repository.save(entities['Mindy'])
        then:
        thrown DuplicateKeyException
    }

    def 'insert(): throws DuplicateKeyException when given entity that is already persisted'() {
        when:
        repository.save(entities['Mindy'])
        then:
        thrown DuplicateKeyException
    }

    def '#method(T): updates the record when already exists'() {
        setup:
        deleteAllUsers()
        and:
        def entity = repository.save(entities['Lucy'])
        entity.enabled = false
        entity.reputation = 42
        when:
        repository./$method/(entity)
        then:
        repository.findOne(entity.id) == new User(entity.id, entity.dateOfBirth, 42, false)
        where:
        method << ['save', 'update']
    }

    def 'update(): throws NoRecordUpdatedException when record does not exist'() {
        setup:
        deleteAllUsers()
        when:
        repository.update(entities['Emma'])
        then:
        def ex = thrown(NoRecordUpdatedException)
        ex.tableName == repository.tableDesc.tableName
        ex.id == ['Emma']
    }

    def 'update(): throws IllegalArgumentException when given entity with null id'() {
        setup:
        deleteAllUsers()
        when:
        repository.update(new User(null, someDateOfBirth, 0, true))
        then:
        thrown IllegalArgumentException
    }


    def 'save(Iterable<T>): inserts given entities'() {
        setup:
        deleteAllUsers()
        when:
        repository.save(entities.values())
        then:
        entities.values().every { User expected ->
            selectUserById(expected.id) == expected
        }
    }


    def 'exists(ID): returns false when DB is empty'() {
        setup:
        deleteAllUsers()
        expect:
        !repository.exists('John')
    }

    def 'exists(ID): returns false when record with such id does not exist'() {
        expect:
        !repository.exists('John')
    }

    def 'exists(ID): returns true when record for given id exists'() {
        expect:
        repository.exists('Mindy')
    }


    def 'delete(ID): does nothing when record for given id does not exist'() {
        when:
        repository.delete('Johny')
        then:
        notThrown Exception
    }

    def 'delete(ID): deletes record by given id'() {
        when:
        repository.delete('Lucy')
        then:
        !selectIds().contains('Lucy')
        selectIds() == entities.keySet() - 'Lucy'
    }

    def 'delete(T): deletes record by given entity'() {
        when:
        repository.delete(entities['Lucy'])
        then:
        !selectIds().contains('Lucy')
        selectIds() == entities.keySet() - 'Lucy'
    }

    def 'delete(Iterable): deletes multiple entities'() {
        setup:
        def toDelete = ['Lucy', 'Emma'].collect(entities.&get)
        when:
        repository.delete(toDelete as List<User>)
        then:
        selectIds() == entities.keySet() - toDelete*.id
    }

    def 'delete(Iterable): ignores non existing entities'() {
        setup:
        def toDelete = [entities['Lucy'], new User('John', someDateOfBirth, 15, true)]
        when:
        repository.delete(toDelete as List<User>)
        then:
        selectIds() == entities.keySet() - toDelete*.id
    }


    def 'deleteAll(): deletes all records in the table'() {
        when:
        repository.deleteAll()
        then:
        selectIds().empty
    }


    def 'count(): returns zero when DB is empty'() {
        setup:
        deleteAllUsers()
        expect:
        repository.count() == 0
    }

    def 'count(): returns correct number of records in the table'() {
        expect:
        repository.count() == 5
    }


    def insertUser(User user) {
        jdbc.update('INSERT INTO USERS VALUES (?, ?, ?, ?)',
                user.userName, user.dateOfBirth, user.reputation, user.enabled)
        assert selectIds().contains(user.userName)
    }

    def selectUserById(String id) {
        jdbc.queryForObject('SELECT * FROM USERS WHERE user_name = ?', UserRepository.ROW_MAPPER, id)
    }

    def selectIds() {
        jdbc.queryForList('SELECT user_name FROM USERS', String) as Set
    }

    def deleteAllUsers() {
        jdbc.execute('DELETE FROM USERS')
    }
}
